package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

public class Complaint {

    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @Nullable
    @SerializedName("local")
    private String local;
    @SerializedName("status")
    private char status;
    @SerializedName("comment")
    private String comment;
    @Nullable
    @SerializedName("response")
    private String response;
    @SerializedName("date")
    private String date;
    @Nullable
    @SerializedName("files")
    private ArrayList<File> files;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public Complaint(Long id, String title, String local, char status, String comment, String response, ArrayList<File> files, String date, String createdAt, String updatedAt) {
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getLocal() {
        return local;
    }

    public void setLocal(@Nullable String local) {
        this.local = local;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Nullable
    public String getResponse() {
        return response;
    }

    public void setResponse(@Nullable String response) {
        this.response = response;
    }

    @Nullable
    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(@Nullable ArrayList<File> files) {
        this.files = files;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
