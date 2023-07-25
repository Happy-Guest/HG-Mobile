package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ReserveRequest {

    @SerializedName("user_id")
    private final Long user_id;

    @SerializedName("time")
    private final String time;

    @SerializedName("service_id")
    private final Long service_id;

    @SerializedName("nr_people")
    private final int nr_people;

    @Nullable
    @SerializedName("comment")
    private final String comment;

    public ReserveRequest(Long user_id, String time, Long service_id, int nr_people, @Nullable String comment) {
        this.user_id = user_id;
        this.time = time;
        this.service_id = service_id;
        this.nr_people = nr_people;
        this.comment = comment;
    }
}
