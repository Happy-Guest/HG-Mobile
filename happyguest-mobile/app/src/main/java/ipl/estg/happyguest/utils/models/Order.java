package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Order {

    @Nullable
    @SerializedName("items")
    public final ArrayList<OrderItem> items;
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
    @Nullable
    @SerializedName("price")
    private final Float price;
    @Nullable
    @SerializedName("comment")
    private final String comment;
    @SerializedName("created_at")
    private final String createdAt;

    public Order(Long id, Service service, Long room, String time, String status, @Nullable ArrayList<OrderItem> items, @Nullable Float price, @Nullable String comment, String createdAt) {
        this.id = id;
        this.service = service;
        this.room = room;
        this.time = time;
        this.status = status;
        this.items = items;
        this.price = price;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @Nullable
    public ArrayList<OrderItem> getItems() {
        return items;
    }

    @Nullable
    public Float getPrice() {
        return price;
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
