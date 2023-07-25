package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class UserCode {

    @SerializedName("id")
    private final Long id;

    @SerializedName("code")
    private final Code code;

    public UserCode(Long id, Code code) {
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public Code getCode() {
        return code;
    }
}
