package ipl.estg.happyguest.app.home.code;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.models.Code;

public class CodesAdapter extends RecyclerView.Adapter<CodesAdapter.ViewHolder> {

    private final ArrayList<Code> codesList;
    private final Context context;

    public CodesAdapter(ArrayList<Code> codesList, Context context) {
        this.codesList = codesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.code_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Code
        Code code = codesList.get(position);

        // Set Texts
        holder.code.setText(code.getCode());
        holder.entryDate.setText(formatDate(code.getEntryDate()));
        holder.exitDate.setText(formatDate(code.getExitDate()));
        holder.rooms.setText(code.getRooms().toString());

        // Remove Button
        holder.remove.setOnClickListener(view -> {
            codesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, codesList.size());
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return codesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView code;
        private final TextView entryDate;
        private final TextView exitDate;
        private final TextView rooms;
        private final ImageButton remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.txtCode);
            entryDate = itemView.findViewById(R.id.txtEntryDate);
            exitDate = itemView.findViewById(R.id.txtExitDate);
            rooms = itemView.findViewById(R.id.txtRooms);
            remove = itemView.findViewById(R.id.btnCodeRemove);
        }
    }
}