package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Complaint;


public class ComplaintResponse extends MessageResponse {

    @SerializedName("data")
    private final Complaint complaint;

    public ComplaintResponse(String message, Complaint complaint) {
        super(message);
        this.complaint = complaint;
    }

    public Complaint getComplaint() {
        return complaint;
    }
}
