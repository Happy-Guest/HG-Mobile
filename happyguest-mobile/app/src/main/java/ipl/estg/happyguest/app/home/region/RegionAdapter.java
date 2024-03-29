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
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.models.RegionInfo;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder> {

    private final ArrayList<RegionInfo> regionInfoList;
    private final Context context;
    private final boolean isWebsite;

    public RegionAdapter(ArrayList<RegionInfo> regionInfoList, Context context, boolean isWebsite) {
        this.regionInfoList = regionInfoList;
        this.context = context;
        this.isWebsite = isWebsite;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.region_item, parent, false);

        if (isWebsite) {
            ImageButton btnMoreInfo = view.findViewById(R.id.btnMoreInfo);
            btnMoreInfo.setImageResource(R.drawable.clip_icon);
        }

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegionInfo regionInfo = regionInfoList.get(position);
        holder.name.setText(position + 1 + ". " + regionInfo.getName());

        holder.moreInfo.setOnClickListener(v -> {
            if (isWebsite) {
                ((HomeActivity) context).openWebsite(regionInfo.getLink());
            } else {
                holder.moreInfo.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_fast));
                showPopup(regionInfo);
            }
        });
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

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(popupView.getRootView(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setText(R.string.open_more);
        btnPopConfirm.setOnClickListener(view1 -> {
            // Open website in browser
            popupWindow.dismiss();
            ((HomeActivity) context).openWebsite(regionInfo.getLink());
        });
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
