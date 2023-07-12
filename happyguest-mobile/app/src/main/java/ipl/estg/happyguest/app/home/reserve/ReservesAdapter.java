package ipl.estg.happyguest.app.home.reserve;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.Reserve;

public class ReservesAdapter extends RecyclerView.Adapter<ReservesAdapter.ViewHolder> {

    private final ArrayList<Reserve> reservesList;
    private final Context context;

    public ReservesAdapter(ArrayList<Reserve> reservesList, Context context) {
        this.reservesList = reservesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reserve_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Services
        Reserve reserve = reservesList.get(position);

        // Set Texts
        switch (reserve.getService().type) {
            case 'R':
                holder.iconService.setImageResource(R.drawable.restaurant_icon);
                break;
            case 'O':
                holder.iconService.setImageResource(R.drawable.reserve_icon);
                break;
        }
        String nameService = Locale.getDefault().getLanguage().equals("pt") ? reserve.getService().name : reserve.getService().nameEN;
        holder.nameService.setText(nameService);
        holder.date.setText(reserve.getTime());

        // Set Status
        String reserveStatus = "";
        switch (reserve.getStatus()) {
            case "P":
                reserveStatus = context.getString(R.string.pending);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                break;
            case "R":
                reserveStatus = context.getString(R.string.rejected);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                break;
            case "A":
                reserveStatus = context.getString(R.string.accepted);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                break;
            case "C":
                reserveStatus = context.getString(R.string.canceled);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b45f06")));
                break;
        }
        holder.status.setText(reserveStatus);

        // View Button
        holder.reserveOpen.setOnClickListener(view -> {
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.changeFragmentService(R.id.action_nav_reserve, reserve.getId(), reserve.getService().type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameService;
        private final TextView date;
        private final TextView status;
        private final Button reserveOpen;
        private final ImageView iconService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameService = itemView.findViewById(R.id.txtNameService);
            date = itemView.findViewById(R.id.txtDateReserve);
            status = itemView.findViewById(R.id.txtStatusReserve);
            reserveOpen = itemView.findViewById(R.id.btnReserveOpen);
            iconService = itemView.findViewById(R.id.imageService);


        }
    }
}