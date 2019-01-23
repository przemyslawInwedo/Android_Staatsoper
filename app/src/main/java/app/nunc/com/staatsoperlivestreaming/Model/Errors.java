package app.nunc.com.staatsoperlivestreaming.Model;

import java.util.List;
import java.util.Map;

public class Errors {

    private Map<String, List<String>> errors;

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "errors=" + errors +
                '}';
    }
}
