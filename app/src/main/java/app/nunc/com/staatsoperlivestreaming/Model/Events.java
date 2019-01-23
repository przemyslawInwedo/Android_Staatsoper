package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Events {

    private Integer count;
    private String next;
    private String previous;
    @SerializedName("results")
    private List<Results> resultsDataList;

    public Integer getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Results> getResults() {
        return resultsDataList;
    }
}

