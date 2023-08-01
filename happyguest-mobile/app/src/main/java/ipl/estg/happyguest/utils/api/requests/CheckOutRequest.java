package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CheckOutRequest {

    @SerializedName("user_id")
    private final Long user_id;

    @SerializedName("code_id")
    private final Long code_id;

    @SerializedName("date")
    private final String date;

    public CheckOutRequest(Long user_id,Long code_id, String date) {
        this.user_id = user_id;
        this.code_id = code_id;
        this.date = date;
    }
}
