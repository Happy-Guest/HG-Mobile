package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("id")
    private Long id;
    @SerializedName("quantity")
    private int quantity;

    public OrderItem(Long id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
