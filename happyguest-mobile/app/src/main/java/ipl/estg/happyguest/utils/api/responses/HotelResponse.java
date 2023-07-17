package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Hotel;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.UserCode;

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
