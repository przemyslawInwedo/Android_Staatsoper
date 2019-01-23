package app.nunc.com.staatsoperlivestreaming.Api;

import java.util.List;

import app.nunc.com.staatsoperlivestreaming.Model.Events;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface StaatsoperApi {

    @GET("events/")
    Single<Events> getEvents(@Header("SYSTEM_VERSION") String systemVersion, @Header("APP_NAME") String appName, @Header("DEVICE_TYPE") String deviceType, @Header("DEVICE_MODEL") String deviceModel, @Header("X_DEVICE_IDENTIFIER") String deviceIdentifier);

}
