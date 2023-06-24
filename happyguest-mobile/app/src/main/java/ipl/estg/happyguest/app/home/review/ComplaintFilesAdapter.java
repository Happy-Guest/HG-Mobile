package ipl.estg.happyguest.app.home.review;


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
import ipl.estg.happyguest.utils.models.ComplaintFile;

public class ComplaintFilesAdapter extends RecyclerView.Adapter<ComplaintFilesAdapter.ViewHolder> {

    private final ArrayList<ComplaintFile> complaintFilesList;
    private final Context context;

    public ComplaintFilesAdapter(ArrayList<ComplaintFile> complaintFilesList, Context context) {
        this.complaintFilesList = complaintFilesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Complaint File
        ComplaintFile complaintFile = complaintFilesList.get(position);

        // Set Texts
        String title = context.getString(R.string.complaint_file) + " " + (position + 1);
        holder.id.setText(title);
        String name = complaintFile.getFilename().length() > 20 ? complaintFile.getFilename().substring(0, 20) + "..." : complaintFile.getFilename();
        holder.name.setText(name);

        // View Button
        holder.fileOpen.setOnClickListener(view -> {
            // Download File
        });
    }

    @Override
    public int getItemCount() {
        return complaintFilesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView id;
        private final TextView name;
        private final Button fileOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdComplaintFile);
            name = itemView.findViewById(R.id.txtNameComplaintFile);
            fileOpen = itemView.findViewById(R.id.btnComplaintFileOpen);
        }
    }
}