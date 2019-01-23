package app.nunc.com.staatsoperlivestreaming.Model;

/**
 * Created by Inwedo on 05.03.2018.
 */

public class ErrorApi {
    private String type;
    private String errorMessage;

    public String getType() {
        return type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String error){
        this.errorMessage = error;
    }


}
