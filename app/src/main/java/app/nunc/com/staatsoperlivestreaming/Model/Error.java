package app.nunc.com.staatsoperlivestreaming.Model;

public class Error {

    private String attribute;
    private String error;
    private String defaultMessage;

    public String getAttribute() {
        return attribute;
    }

    public String getError() {
        return error;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return "Error{" +
                "attribute='" + attribute + '\'' +
                ", error='" + error + '\'' +
                ", defaultMessage='" + defaultMessage + '\'' +
                '}';
    }
}
