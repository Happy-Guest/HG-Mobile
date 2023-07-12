package ipl.estg.happyguest.app.home.order;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.Order;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final ArrayList<Order> ordersList;
    private final Context context;

    public OrdersAdapter(ArrayList<Order> ordersList, Context context) {
        this.ordersList = ordersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Services
        Order order = ordersList.get(position);

        // Set Texts
        long id = position + 1;
        String nameService = Locale.getDefault().getLanguage().equals("pt") ? order.getService().name : order.getService().nameEN;
        holder.nameService.setText(nameService);
        String room = context.getString(R.string.services_room) + " " + order.getRoom();
        holder.room.setText(room);
        holder.date.setText(order.getTime());

        // Set Status
        String orderStatus = "";
        switch (order.getStatus()) {
            case "P":
                orderStatus = context.getString(R.string.pending);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                break;
            case "R":
                orderStatus = context.getString(R.string.rejected);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                break;
            case "W":
                orderStatus = context.getString(R.string.working);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA810F")));
                break;
            case "DL":
                orderStatus = context.getString(R.string.delivered);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                break;
            case "C":
                orderStatus = context.getString(R.string.canceled);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b45f06")));
                break;
        }
        holder.status.setText(orderStatus);

        // View Button
        holder.orderOpen.setOnClickListener(view -> {
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.changeFragmentBundle(R.id.action_nav_order, order.getId(), nameService);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameService;
        private final TextView room;
        private final TextView date;
        private final TextView status;
        private final Button orderOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameService = itemView.findViewById(R.id.txtNameService);
            room = itemView.findViewById(R.id.txtRoomOrder);
            date = itemView.findViewById(R.id.txtDateOrder);
            status = itemView.findViewById(R.id.txtStatusOrder);
            orderOpen = itemView.findViewById(R.id.btnOrderOpen);
        }
    }
}