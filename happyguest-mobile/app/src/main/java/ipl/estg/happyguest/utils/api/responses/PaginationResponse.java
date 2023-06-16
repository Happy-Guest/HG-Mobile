package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Meta;

public class PaginationResponse<T> {

    @SerializedName("data")
    private final ArrayList<T> data;
    @SerializedName("meta")
    private final Meta meta;

    public PaginationResponse(ArrayList<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }
}
