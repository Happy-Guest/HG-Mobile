package ipl.estg.happyguest.app.home.complaint;


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
import java.util.Objects;

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
        long id = position + 1;
        String title = context.getString(R.string.menu_complaint) + " " + id;
        holder.id.setText(title);
        String local = context.getString(R.string.complaint_local) + " " + (Objects.requireNonNull(complaint.getLocal()).length() > 10 ? complaint.getLocal().substring(0, 10) + "..." : complaint.getLocal());
        holder.local.setText(local);
        holder.date.setText(complaint.getCreatedAt());

        // Set Status
        String complaintStatus = "";
        switch (complaint.getStatus()) {
            case 'P':
                complaintStatus = context.getString(R.string.pending);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                break;
            case 'S':
                complaintStatus = context.getString(R.string.solving);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA810F")));
                break;
            case 'R':
                complaintStatus = context.getString(R.string.resolved);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                break;
            case 'C':
                complaintStatus = context.getString(R.string.canceled);
                holder.status.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                break;
        }
        holder.status.setText(complaintStatus);

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
        private final TextView local;
        private final TextView date;
        private final TextView status;
        private final Button complaintOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdComplaint);
            local = itemView.findViewById(R.id.txtLocalComplaint);
            date = itemView.findViewById(R.id.txtDateComplaint);
            status = itemView.findViewById(R.id.txtStatusComplaint);
            complaintOpen = itemView.findViewById(R.id.btnComplaintOpen);
        }
    }
}