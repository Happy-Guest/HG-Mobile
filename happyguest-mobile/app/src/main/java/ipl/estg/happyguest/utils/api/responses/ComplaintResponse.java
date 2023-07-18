package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Complaint;

public class ComplaintResponse extends MessageResponse {

    @SerializedName("data")
    private final Complaint complaint;

    public ComplaintResponse(String message, @Nullable Complaint complaint) {
        super(message);
        this.complaint = complaint;
    }

    @Nullable
    public Complaint getComplaint() {
        return complaint;
    }
}
