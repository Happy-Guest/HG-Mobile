package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Order;

public class OrderResponse extends MessageResponse {

    @SerializedName("data")
    private final Order order;

    public OrderResponse(String message, @Nullable Order order) {
        super(message);
        this.order = order;
    }

    @Nullable
    public Order getOrder() {
        return order;
    }
}
