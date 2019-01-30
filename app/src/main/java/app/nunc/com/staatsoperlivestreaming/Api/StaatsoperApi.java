package app.nunc.com.staatsoperlivestreaming.Api;

import app.nunc.com.staatsoperlivestreaming.Model.Events;
import app.nunc.com.staatsoperlivestreaming.Model.Stream;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface StaatsoperApi {

    @GET("events/")
    Single<Events> getEvents(@Header("SYSTEM_VERSION") String systemVersion, @Header("APP_NAME") String appName, @Header("DEVICE_TYPE") String deviceType, @Header("DEVICE_MODEL") String deviceModel, @Header("X_DEVICE_IDENTIFIER") String deviceIdentifier);

    @GET("videotheque/")
    Single<Events> getVideotheque(@Header("SYSTEM_VERSION") String systemVersion, @Header("APP_NAME") String appName, @Header("DEVICE_TYPE") String deviceType, @Header("DEVICE_MODEL") String deviceModel, @Header("X_DEVICE_IDENTIFIER") String deviceIdentifier);

    @GET("events/{id}/streams/")
    Single<Stream> getStream(@Path ("id") String id, @Header("SYSTEM_VERSION") String systemVersion, @Header("APP_NAME") String appName, @Header("DEVICE_TYPE") String deviceType, @Header("DEVICE_MODEL") String deviceModel, @Header("X_DEVICE_IDENTIFIER") String deviceIdentifier);
}
