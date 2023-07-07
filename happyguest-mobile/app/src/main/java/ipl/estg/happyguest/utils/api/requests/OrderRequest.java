package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {

    @SerializedName("user_id")
    private final Long user_id;
    @SerializedName("room")
    private final String room;
    @SerializedName("time")
    private final String time;
    @SerializedName("service_id")
    private final Long service_id;
    @Nullable
    @SerializedName("items")
    private final List<Long> items;
    @SerializedName("price")
    private final Double price;
    @Nullable
    @SerializedName("comment")
    private final String comment;

    public OrderRequest(Long user_id, String room, String time, Long service_id, @Nullable List<Long> items, Double price, @Nullable String comment) {
        this.user_id = user_id;
        this.room = room;
        this.time = time;
        this.service_id = service_id;
        this.items = items;
        this.price = price;
        this.comment = comment;
    }

}
