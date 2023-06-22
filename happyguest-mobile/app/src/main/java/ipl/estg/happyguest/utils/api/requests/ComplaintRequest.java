package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ComplaintRequest {
    @Nullable
    @SerializedName("user_id")
    private final Integer user_id;

    @SerializedName("title")
    private final String title;

    @SerializedName("local")
    private final String local;

    @SerializedName("status")
    private final String status;

    @SerializedName("comment")
    private final String comment;

    @SerializedName("date")
    private final String date;

    public ComplaintRequest(@Nullable Integer user_id, String title, String local, String status, String comment, String date) {
        this.user_id = user_id;
        this.title = title;
        this.local = local;
        this.status = status;
        this.comment = comment;
        this.date = date;
    }
}
