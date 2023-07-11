package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    private final Long id;

    @SerializedName("service")
    private final Service service;

    @SerializedName("room")
    private final Long room;

    @SerializedName("time")
    private final String time;

    @SerializedName("status")
    private final String status;

    @SerializedName("created_at")
    private final String createdAt;

    public Order(Long id, Service service, Long room, String time, String status, String createdAt) {
        this.id = id;
        this.service = service;
        this.room = room;
        this.time = time;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Service getService() {
        return service;
    }

    public Long getRoom() {
        return room;
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
