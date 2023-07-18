package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {

    @SerializedName("old_password")
    private final String old_password;

    @SerializedName("new_password")
    private final String new_password;

    @SerializedName("new_password_confirmation")
    private final String new_password_confirmation;

    public ChangePasswordRequest(String old_password, String new_password, String new_password_confirmation) {
        this.old_password = old_password;
        this.new_password = new_password;
        this.new_password_confirmation = new_password_confirmation;
    }
}
