package ipl.estg.happyguest.app.home.services;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.Item;
import ipl.estg.happyguest.utils.models.OrderItem;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private final ArrayList<Item> itemsList;
    private final Context context;
    private final ArrayList<OrderItem> orderItems = new ArrayList<>();
    private final TextView totalPriceText;
    private final TextView emptyListText;
    private final ArrayList<Item> backUpItemsList;
    private Double totalPrice = 0.0;
    private String languageCode;

    public ItemsAdapter(ArrayList<Item> itemsList, Context context, TextView totalPriceText, TextView emptyListText) {
        this.itemsList = itemsList;
        this.context = context;
        this.totalPriceText = totalPriceText;
        this.emptyListText = emptyListText;
        this.backUpItemsList = new ArrayList<>(itemsList);
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setFilter(String filter) {
        if (Objects.equals(filter, "All")) {
            if (!itemsList.equals(backUpItemsList)) {
                itemsList.clear();
                itemsList.addAll(backUpItemsList);
                notifyItemRangeRemoved(0, itemsList.size());
                notifyItemRangeInserted(0, backUpItemsList.size());
            }
        } else {
            List<Item> filteredList = new ArrayList<>();
            if (!filter.isEmpty()) {
                filter = filter.toLowerCase();
                for (Item item : backUpItemsList) {
                    if (item.getCategory().toLowerCase().contains(filter) || item.getName().toLowerCase().contains(filter)) {
                        filteredList.add(item);
                    }
                }
            }
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemDiffCallback(itemsList, filteredList));
            itemsList.clear();
            itemsList.addAll(filteredList);
            diffResult.dispatchUpdatesTo(this);
        }
        if (itemsList.isEmpty()) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }
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

        // Get language code and set description
        languageCode = Locale.getDefault().getLanguage();

        // Set Texts
        holder.name.setText(languageCode.equals("pt") ? item.getName() : item.getNameEN());
        holder.price.setText(item.getPrice() != null ? item.getPrice().toString() + "€" : "0€");
        holder.quantity.setText("0");

        // Add Qnt Button
        holder.addQnt.setOnClickListener(v -> {
            int qnt = Integer.parseInt(holder.quantity.getText().toString());
            if (qnt + 1 > item.getStock()) {
                Toast.makeText(context, context.getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
                return;
            }
            if (qnt < 9) {
                if (qnt > 0) {
                    // Update Old Item
                    OrderItem updatedItem = null;
                    for (OrderItem orderItem : orderItems) {
                        if (orderItem.getId().equals(item.getId())) {
                            orderItem.setQuantity(qnt + 1);
                            updatedItem = orderItem;
                        }
                    }
                    if (updatedItem != null) {
                        float price = item.getPrice() != null ? item.getPrice() : 0;
                        totalPrice += price;
                    }
                } else {
                    orderItems.add(new OrderItem(item.getId(), languageCode.equals("pt") ? item.getName() : item.getNameEN(), 1));
                    float price = item.getPrice() != null ? item.getPrice() : 0;
                    totalPrice += price;
                }
                holder.addQnt.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_fast));
                String qntString = String.valueOf(qnt + 1);
                holder.quantity.setText(qntString);
                String totalPriceString = (totalPrice == 0 ? "0" : totalPrice) + "€";
                totalPriceText.setText(totalPriceString);
            }
        });

        // Remove Qnt Button
        holder.removeQnt.setOnClickListener(v -> {
            int qnt = Integer.parseInt(holder.quantity.getText().toString());
            if (qnt > 0) {
                if (qnt > 1) {
                    // Update Old Item
                    OrderItem updatedItem = null;
                    for (OrderItem orderItem : orderItems) {
                        if (orderItem.getId().equals(item.getId())) {
                            orderItem.setQuantity(qnt - 1);
                            updatedItem = orderItem;
                        }
                    }
                    if (updatedItem != null) {
                        float price = item.getPrice() != null ? item.getPrice() : 0;
                        totalPrice -= price;
                    }
                } else {
                    // Remove Item
                    OrderItem removedItem = null;
                    for (OrderItem orderItem : orderItems) {
                        if (orderItem.getId().equals(item.getId())) {
                            removedItem = orderItem;
                            break;
                        }
                    }
                    if (removedItem != null) {
                        orderItems.remove(removedItem);
                        float price = item.getPrice() != null ? item.getPrice() : 0;
                        totalPrice -= price;
                    }
                }
                holder.removeQnt.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_fast));
                String qntString = String.valueOf(qnt - 1);
                holder.quantity.setText(qntString);
                String totalPriceString = (totalPrice == 0 ? "0" : totalPrice) + "€";
                totalPriceText.setText(totalPriceString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView price;
        private final TextView quantity;
        private final ImageButton removeQnt;
        private final ImageButton addQnt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtItemName);
            price = itemView.findViewById(R.id.txtItemPrice);
            quantity = itemView.findViewById(R.id.txtItemQuantity);
            removeQnt = itemView.findViewById(R.id.btnQntRemove);
            addQnt = itemView.findViewById(R.id.btnQntAdd);
        }
    }

    private static class ItemDiffCallback extends DiffUtil.Callback {
        private final List<Item> oldList;
        private final List<Item> newList;

        public ItemDiffCallback(List<Item> oldList, List<Item> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Item oldItem = oldList.get(oldItemPosition);
            Item newItem = newList.get(newItemPosition);
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Item oldItem = oldList.get(oldItemPosition);
            Item newItem = newList.get(newItemPosition);
            return oldItem.equals(newItem);
        }
    }
}