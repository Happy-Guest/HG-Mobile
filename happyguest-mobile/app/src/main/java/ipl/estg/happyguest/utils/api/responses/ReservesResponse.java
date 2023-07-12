package ipl.estg.happyguest.utils.api.responses;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.Reserve;

public class ReservesResponse extends PaginationResponse<Reserve> {

    public ReservesResponse(ArrayList<Reserve> reserves, Meta meta) {
        super(reserves, meta);
    }
}
