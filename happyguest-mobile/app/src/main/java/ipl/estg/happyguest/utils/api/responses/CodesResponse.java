package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.api.models.Meta;
import ipl.estg.happyguest.utils.models.Code;

public class CodesResponse extends PaginationResponse<Code> {

    @SerializedName("data")
    private final ArrayList<Code> codes;
    @SerializedName("meta")
    private final Meta meta;

    public CodesResponse(ArrayList<Code> codes, Meta meta) {
        super(codes, meta);
        this.codes = codes;
        this.meta = meta;
    }

    public ArrayList<Code> getCodes() {
        return codes;
    }

    public Meta getMeta() {
        return meta;
    }
}
