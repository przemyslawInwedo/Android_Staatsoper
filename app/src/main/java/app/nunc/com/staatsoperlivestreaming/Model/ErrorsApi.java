package app.nunc.com.staatsoperlivestreaming.Model;

import java.util.List;

/**
 * Created by Inwedo on 05.03.2018.
 */

public class ErrorsApi {
    private List<ErrorApi> errors;
    public List<ErrorApi> getErrors(){
        return  this.errors;
    }
    public void setErrors(List<ErrorApi> errors){
        this.errors = errors;
    }

}
