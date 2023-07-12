package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("id")
    private final Long id;
    @SerializedName("name")
    private final String name;
    @SerializedName("nameEN")
    private final String nameEN;
    @SerializedName("type")
    private final String type;
    @SerializedName("category")
    private final String category;
    @SerializedName("stock")
    private final int stock;
    @Nullable
    @SerializedName("price")
    private final Float price;


    public Item(Long id, String name, String nameEN, String type, String category, int stock, @Nullable Float price) {
        this.id = id;
        this.name = name;
        this.nameEN = nameEN;
        this.type = type;
        this.category = category;
        this.stock = stock;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameEN() {
        return nameEN;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    @Nullable
    public int getStock() {
        return stock;
    }

    @Nullable
    public Float getPrice() {
        return price;
    }


}
