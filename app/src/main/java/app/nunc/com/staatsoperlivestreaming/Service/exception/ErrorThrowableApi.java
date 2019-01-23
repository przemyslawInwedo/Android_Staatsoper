package app.nunc.com.staatsoperlivestreaming.Service.exception;

import java.io.IOException;

import app.nunc.com.staatsoperlivestreaming.Model.ErrorApi;
import app.nunc.com.staatsoperlivestreaming.Model.ErrorsApi;

/**
 * Created by Inwedo on 05.03.2018.
 */

public class ErrorThrowableApi extends IOException{
    private ErrorsApi errors;
    private ErrorApi error;

    public ErrorThrowableApi(ErrorsApi errors){
        setErrors(errors);
    }
    public void setErrors(ErrorsApi errors) {
        this.errors = errors;
    }

    public void setError(ErrorApi error) {
        this.error = error;
    }
    public ErrorsApi getErrors() {
        return errors;
    }

    public ErrorApi getError() {
        return error;
    }

}
