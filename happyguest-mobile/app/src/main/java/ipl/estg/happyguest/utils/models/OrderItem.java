package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem extends Item {

    @SerializedName("quantity")
    private int quantity;

    public OrderItem(Long id, String name, int quantity) {
        super(id, name, null, null, null, 0, null);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}