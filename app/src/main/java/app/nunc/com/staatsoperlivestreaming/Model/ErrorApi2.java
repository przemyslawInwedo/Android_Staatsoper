package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inwedo on 07.03.2018.
 */

public class ErrorApi2 {
    @SerializedName("error")
    private  String error;

    @SerializedName("error_description")
    private String errorDescription;


    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
