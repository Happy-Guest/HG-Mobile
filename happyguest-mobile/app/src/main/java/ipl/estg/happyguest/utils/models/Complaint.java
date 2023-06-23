package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

public class Complaint {

    @SerializedName("id")
    private final Long id;
    @SerializedName("title")
    private final String title;
    @Nullable
    @SerializedName("local")
    private final String local;
    @SerializedName("status")
    private final char status;
    @SerializedName("comment")
    private final String comment;
    @Nullable
    @SerializedName("response")
    private final String response;
    @SerializedName("date")
    private final String date;
    @Nullable
    @SerializedName("files")
    private final ArrayList<File> files;
    @SerializedName("created_at")
    private final String createdAt;
    @SerializedName("updated_at")
    private final String updatedAt;

    public Complaint(Long id, String title, @Nullable String local, char status, String comment, @Nullable String response, @Nullable ArrayList<File> files, String date, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.local = local;
        this.status = status;
        this.comment = comment;
        this.response = response;
        this.files = files;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public String getLocal() {
        return local;
    }

    public char getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    @Nullable
    public String getResponse() {
        return response;
    }

    @Nullable
    public ArrayList<File> getFiles() {
        return files;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDate() {
        return date;
    }
}
