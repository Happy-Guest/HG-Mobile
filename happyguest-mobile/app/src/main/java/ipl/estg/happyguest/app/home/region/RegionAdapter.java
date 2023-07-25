package ipl.estg.happyguest.app.home.region;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.RegionInfo;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    private final ArrayList<RegionInfo> regionInfoList;
    private final Context context;

    public RegionAdapter(ArrayList<RegionInfo> regionInfoList, Context context) {
        this.regionInfoList = regionInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.region_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegionInfo regionInfo = regionInfoList.get(position);
        holder.name.setText(position + 1 + ". " + regionInfo.getName());

        holder.moreInfo.setOnClickListener(v -> showPopup(regionInfo));
    }

    private void showPopup(RegionInfo regionInfo) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup, null);

        // Create the popup window
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Set popup texts
        String name = regionInfo.getName() + " - " + regionInfo.getDistance();
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(name);
        popupView.findViewById(R.id.txtRegionDescription).setVisibility(View.VISIBLE);
        String languageCode = Locale.getDefault().getLanguage();
        if (languageCode.equals("pt")) {
            ((TextView) popupView.findViewById(R.id.txtRegionDescription)).setText(regionInfo.getDescription());
        } else {
            ((TextView) popupView.findViewById(R.id.txtRegionDescription)).setText(regionInfo.getDescriptionEN());
        }
        popupView.findViewById(R.id.dividerRegion).setVisibility(View.VISIBLE);
        popupView.findViewById(R.id.txtPopUpRegionLink).setVisibility(View.VISIBLE);
        ((TextView) popupView.findViewById(R.id.txtPopUpRegionLink)).setText(regionInfo.getLink());

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(popupView.getRootView(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        popupView.findViewById(R.id.btnConfirm).setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return regionInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageButton moreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            moreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }
    }
}
