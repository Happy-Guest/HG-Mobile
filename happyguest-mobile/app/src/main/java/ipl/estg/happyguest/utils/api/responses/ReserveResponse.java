package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.Reserve;

public class ReserveResponse extends MessageResponse {

    @SerializedName("data")
    private final Reserve reserve;

    public ReserveResponse(String message, @Nullable Reserve reserve) {
        super(message);
        this.reserve = reserve;
    }


    @Nullable
    public Reserve getReserve() {
        return reserve;
    }
}
