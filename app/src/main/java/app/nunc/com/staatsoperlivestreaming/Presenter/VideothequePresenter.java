package app.nunc.com.staatsoperlivestreaming.Presenter;

import android.util.Log;

import app.nunc.com.staatsoperlivestreaming.Api.StaatsoperApi;
import app.nunc.com.staatsoperlivestreaming.Base.Keys;
import app.nunc.com.staatsoperlivestreaming.Model.Events;
import app.nunc.com.staatsoperlivestreaming.View.VideothequeView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideothequePresenter {

    private VideothequeView videothequeView;

    public VideothequePresenter(VideothequeView videothequeView) {
        this.videothequeView = videothequeView;
    }

    public void getAvailableStreams(){
        StaatsoperApi staatsoperApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Keys.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        staatsoperApi = retrofit.create(StaatsoperApi.class);

        Single<Events> eventsKey = staatsoperApi.getVideotheque(Keys.X_DEVICE_SYSTEM_VERSION, Keys.X_DEVICE_APP_NAME, Keys.X_DEVICE_TYPE, Keys.X_DEVICE_MODEL, Keys.X_DEVICE_IDENTIFIER);
        eventsKey.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Events>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        videothequeView.showProgress();
                    }

                    @Override
                    public void onSuccess(Events eventsModel) {
                        videothequeView.setVideotheque(eventsModel);
                        videothequeView.hideProgress();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("API ERROR", e.getMessage());
                        Log.d("API ERROR", e.getLocalizedMessage());
                        videothequeView.hideProgress();
                    }
                });
    }
}
