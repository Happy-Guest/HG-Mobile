package ipl.estg.happyguest.app.home.code;


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
        holder.entryDate.setText((CharSequence) code.getEntryDate());
        holder.exitDate.setText((CharSequence) code.getExitDate());
        holder.rooms.setText(code.getRooms().toString());

        // Remove Button
        holder.remove.setOnClickListener(view -> {
            codesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, codesList.size());
        });
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
        private final Button remove;

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