package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class HotelInfo {

    @SerializedName("name")
    private final String name;

    @SerializedName("nameEN")
    private final String nameEN;

    public HotelInfo(String name, String nameEN) {
        this.name = name;
        this.nameEN = nameEN;
    }

    public String getName() {
        return name;
    }

    public String getNameEN() {
        return nameEN;
    }
}
