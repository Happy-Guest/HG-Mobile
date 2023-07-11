package ipl.estg.happyguest.utils.api.responses;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Order;

public class OrdersResponse extends PaginationResponse<Order> {

    public OrdersResponse(ArrayList<Order> orders, Meta meta) {
        super(orders, meta);
    }
}
