package app.nunc.com.staatsoperlivestreaming.Service.exception;

import java.io.IOException;

import app.nunc.com.staatsoperlivestreaming.Model.Errors;

public class ErrorThrowable extends IOException {

    private Error error;
    private Errors errors;

    public ErrorThrowable(Error error) {
        this.error = error;
    }

    public ErrorThrowable(Errors errors) {
        this.errors = errors;
    }

    public Error getError() {
        return error;
    }

    public Errors getErrors() {
        return errors;
    }
}
