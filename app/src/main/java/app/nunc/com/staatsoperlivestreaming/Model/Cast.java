package app.nunc.com.staatsoperlivestreaming.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cast {
    private String role_type;
    @SerializedName("castings")
    private List<Casting> casting;

    public List<Casting> getCasting() {
        return casting;
    }

    public String getRole_type() {
        return role_type;
    }

}
