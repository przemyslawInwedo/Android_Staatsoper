package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

public class StreamsModel {

    private String created;

    @SerializedName("playback_status")
    private String playbackStatus;

    private String id;

    private String label;

    private String type;

    private String updated;

    private String url;

    public String getCreated() {
        return created;
    }

    public String getPlaybackStatus() {
        return playbackStatus;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getUpdated() {
        return updated;
    }

    public String getUrl() {
        return url;
    }
}
