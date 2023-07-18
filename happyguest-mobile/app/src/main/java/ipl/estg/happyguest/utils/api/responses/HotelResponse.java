package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Hotel;

public class HotelResponse extends MessageResponse {

    @SerializedName("data")
    private final Hotel hotel;

    public HotelResponse(String message, @Nullable Hotel hotel) {
        super(message);
        this.hotel = hotel;
    }

    @Nullable
    public Hotel getHotel() {
        return hotel;
    }
}
