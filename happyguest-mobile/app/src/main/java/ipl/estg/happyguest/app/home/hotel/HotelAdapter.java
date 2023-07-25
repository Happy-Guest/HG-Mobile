package ipl.estg.happyguest.app.home.hotel;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.HotelInfo;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    private final ArrayList<HotelInfo> hotelInfoList;
    private final Context context;

    public HotelAdapter(ArrayList<HotelInfo> hotelInfoList, Context context) {
        this.hotelInfoList = hotelInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HotelInfo hotelInfo = hotelInfoList.get(position);
        String languageCode = Locale.getDefault().getLanguage();
        if (languageCode.equals("pt")) {
            holder.info.setText(position + 1 + ". " + hotelInfo.getName());
        } else {
            holder.info.setText(position + 1 + ". " + hotelInfo.getNameEN());
        }
    }

    @Override
    public int getItemCount() {
        return hotelInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.txtHotelInfo);
        }
    }
}
