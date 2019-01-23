package app.nunc.com.staatsoperlivestreaming.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Results {
    private Integer id;
    @SerializedName("_self")
    private String self;
    private String title;
    @SerializedName("begin_time")
    private String beginTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("vod_availability")
    private String vodAvailability;
    private Boolean videotheque;
    @SerializedName("metadata")
    private Metadata metaDataList;
    private String streams;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private String published;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date updated;

    public Integer getId() {
        return id;
    }

    public String getSelf() {
        return self;
    }

    public String getTitle() {
        return title;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getVodAvailability() {
        return vodAvailability;
    }

    public Boolean getVideotheque() {
        return videotheque;
    }

    public Metadata getMetaDataList() {
        return metaDataList;
    }

    public String getStreams() {
        return streams;
    }

    public String getPublished() {
        return published;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }
}
