package ipl.estg.happyguest.app.home.complaint;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.Complaint;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {

    private final ArrayList<Complaint> complaintsList;
    private final Context context;

    public ComplaintsAdapter(ArrayList<Complaint> complaintsList, Context context) {
        this.complaintsList = complaintsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Complaint
        Complaint complaint = complaintsList.get(position);

        // Set Texts
        String title;
        long id = position + 1;
        holder.id.setText(title);
        holder.date.setText(complaint.getCreatedAt());

        // View Button
        /*holder.complaintOpen.setOnClickListener(view -> {
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.changeFragmentBundle(R.id.nav_complaint, complaint.getId(), id);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView id;
        private final TextView date;
        private final Button complaintOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdReview);
            date = itemView.findViewById(R.id.txtDateReview);
            complaintOpen = itemView.findViewById(R.id.btnComplaintOpen);
        }
    }
}