package ipl.estg.happyguest.utils.api.responses;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Complaint;
import ipl.estg.happyguest.utils.models.Meta;

public class ComplaintsResponse extends PaginationResponse<Complaint> {

    public ComplaintsResponse(ArrayList<Complaint> complaints, Meta meta) {
        super(complaints, meta);
    }
}
