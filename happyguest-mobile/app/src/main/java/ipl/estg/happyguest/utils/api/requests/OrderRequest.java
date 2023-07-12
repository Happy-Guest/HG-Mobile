package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ipl.estg.happyguest.utils.models.OrderItem;

public class OrderRequest {

    @SerializedName("user_id")
    private final Long user_id;
    @SerializedName("room")
    private final String room;
    @Nullable
    @SerializedName("time")
    private final String time;
    @SerializedName("service_id")
    private final Long service_id;
    @Nullable
    @SerializedName("items")
    private final List<OrderItem> items;

    @SerializedName("price")
    private final Double price;
    @Nullable
    @SerializedName("comment")
    private final String comment;


    public OrderRequest(Long user_id, String room, @Nullable String time, Long service_id, @Nullable List<OrderItem> items, Double price, @Nullable String comment) {
        this.user_id = user_id;
        this.room = room;
        this.time = time;
        this.service_id = service_id;
        this.items = items;
        this.price = price;
        this.comment = comment;
    }



}
