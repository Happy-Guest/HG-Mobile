package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Reserve {

    @SerializedName("id")
    private final Long id;

    @SerializedName("service")
    private final Service service;

    @SerializedName("nr_people")
    private final Long nr_people;

    @SerializedName("time")
    private final String time;

    @SerializedName("status")
    private final String status;

    @Nullable
    @SerializedName("comment")
    private final String comment;

    @SerializedName("created_at")
    private final String createdAt;

    public Reserve(Long id, Service service, Long nr_people, String time, String status, @Nullable String comment, String createdAt) {
        this.id = id;
        this.service = service;
        this.nr_people = nr_people;
        this.time = time;
        this.status = status;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getNr_people() {
        return nr_people;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public Long getId() {
        return id;
    }

    public Service getService() {
        return service;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
