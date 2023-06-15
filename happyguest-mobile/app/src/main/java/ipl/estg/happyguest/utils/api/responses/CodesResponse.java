package ipl.estg.happyguest.utils.api.responses;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.UserCode;

public class CodesResponse extends PaginationResponse<UserCode> {

    public CodesResponse(ArrayList<UserCode> codes, Meta meta) {
        super(codes, meta);
    }
}
