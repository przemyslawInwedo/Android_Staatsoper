package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stream {

    @SerializedName("streams")
    private List<StreamsModel> streams;

    private String id;

    public List<StreamsModel> getStreams() {
        return streams;
    }

    public String getId() {
        return id;
    }
}
