package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComplaintRequest {
    @Nullable
    @SerializedName("user_id")
    private final Long user_id;
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
    @Nullable
    @SerializedName("filesBase64")
    private final List<String> filesBase64;
    @Nullable
    @SerializedName("fileNames")
    private final List<String> fileNames;

    public ComplaintRequest(@Nullable Long user_id, String title, String local, String status, String comment, String date, @Nullable List<String> filesBase64, @Nullable List<String> fileNames) {
        this.user_id = user_id;
        this.title = title;
        this.local = local;
        this.status = status;
        this.comment = comment;
        this.date = date;
        this.filesBase64 = filesBase64;
        this.fileNames = fileNames;
    }
}
