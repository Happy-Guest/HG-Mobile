package ipl.estg.happyguest.utils.api.responses;

import ipl.estg.happyguest.utils.models.Region;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class RegionResponse extends MessageResponse {

    @SerializedName("data")
    private final Region region;

    public RegionResponse(String message, @Nullable Region region) {
        super(message);
        this.region = region;
    }

    @Nullable
    public Region getRegion() {
        return region;
    }
}
