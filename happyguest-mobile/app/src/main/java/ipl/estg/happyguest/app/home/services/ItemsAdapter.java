package ipl.estg.happyguest.app.home.services;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.Item;
import ipl.estg.happyguest.utils.models.OrderItem;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private final ArrayList<Item> itemsList;
    private final Context context;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();
    private Double totalPrice = 0.0;

    public ItemsAdapter(ArrayList<Item> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Item
        Item item = itemsList.get(position);

        // Set Texts
        holder.name.setText(item.getName());
        holder.quantity.setText("0");

        // Add Qnt Button
        holder.addQnt.setOnClickListener(v -> {
            int qnt = Integer.parseInt(holder.quantity.getText().toString());
            holder.quantity.setText(qnt + 1);
        });
        // Remove Qnt Button
        holder.removeQnt.setOnClickListener(v -> {
            int qnt = Integer.parseInt(holder.quantity.getText().toString());
            if (qnt > 0) {
                holder.quantity.setText(qnt - 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView quantity;
        private final ImageButton removeQnt;
        private final ImageButton addQnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtItemName);
            quantity = itemView.findViewById(R.id.txtItemQuantity);
            removeQnt = itemView.findViewById(R.id.btnQntRemove);
            addQnt = itemView.findViewById(R.id.btnQntAdd);
        }
    }
}