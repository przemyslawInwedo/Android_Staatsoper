package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Metadata {
    private String img;
    private String ref_id;
    @SerializedName("cast")
    private List<Cast> castList;
    private String title_ext;
    private String long_description;
    private String short_description;

    public String getImg() {
        return img;
    }

    public String getRef_id() {
        return ref_id;
    }

    public List<Cast> getCastList() {
        return castList;
    }

    public String getTitle_ext() {
        return title_ext;
    }

    public String getLong_description() {
        return long_description;
    }

    public String getShort_description() {
        return short_description;
    }
}
