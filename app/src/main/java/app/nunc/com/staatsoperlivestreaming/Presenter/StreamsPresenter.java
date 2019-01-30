package app.nunc.com.staatsoperlivestreaming.Presenter;

import android.util.Log;

import app.nunc.com.staatsoperlivestreaming.Api.StaatsoperApi;
import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Model.Stream;
import app.nunc.com.staatsoperlivestreaming.StreamsView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StreamsPresenter {

    private StreamsView streamsView;

    public StreamsPresenter(StreamsView streamsView) {
        this.streamsView = streamsView;
    }

    public void getStreams(String id){
        StaatsoperApi staatsoperApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Keys.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        staatsoperApi = retrofit.create(StaatsoperApi.class);

        Single<Stream> cityKey = staatsoperApi.getStream(id, Keys.X_DEVICE_SYSTEM_VERSION, Keys.X_DEVICE_APP_NAME, Keys.X_DEVICE_TYPE, Keys.X_DEVICE_MODEL, Keys.X_DEVICE_IDENTIFIER);
        cityKey.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Stream>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        streamsView.showProgress();
                    }

                    @Override
                    public void onSuccess(Stream stream) {
                        streamsView.setStream(stream);
                        streamsView.hideProgress();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("API ERROR", e.getMessage());
                        Log.d("API ERROR", e.getLocalizedMessage());
                        streamsView.hideProgress();
                    }
                });
    }
}
