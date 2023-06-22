package ipl.estg.happyguest.app.home.code;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.models.Code;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodesAdapter extends RecyclerView.Adapter<CodesAdapter.ViewHolder> {

    private final ArrayList<Code> codesList;
    private final Context context;
    private final APIRoutes api;
    private final User user;

    public CodesAdapter(ArrayList<Code> codesList, Context context, APIRoutes api, User user) {
        this.codesList = codesList;
        this.context = context;
        this.api = api;
        this.user = user;
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
        String entryDate = context.getString(R.string.code_entry_date) + " " + code.getEntryDate();
        holder.entryDate.setText(entryDate);
        String exitDate = context.getString(R.string.code_exit_date) + " " + code.getExitDate();
        holder.exitDate.setText(exitDate);

        // Convert and concatenate room values
        List<String> roomList = code.getRooms();
        StringBuilder roomsBuilder = new StringBuilder();
        for (int i = 0; i < roomList.size(); i++) {
            if (i > 0) roomsBuilder.append(", ");
            roomsBuilder.append(Integer.parseInt(roomList.get(i)));
        }
        String roomsText = context.getString(R.string.code_rooms) + " " + roomsBuilder;
        holder.rooms.setText(roomsText);

        // Remove Button
        holder.remove.setOnClickListener(view -> showPopUp(code.getCode(), position, holder.remove));
    }

    @Override
    public int getItemCount() {
        return codesList.size();
    }

    private void showPopUp(String code, int position, ImageButton btnRemove) {
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
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(context.getString(R.string.title_disassociateCode));

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            disassociateCodeAttempt(code, position, btnRemove);
            btnRemove.setEnabled(false);
            popupWindow.dismiss();
        });
    }

    public void disassociateCodeAttempt(String code, int position, ImageButton btnRemove) {
        Call<MessageResponse> call = api.disassociateCode(user.getId(), code);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                btnRemove.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Remove code from list and notify adapter
                    Toast.makeText(context.getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    codesList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, codesList.size());
                    // If no codes left, show text
                    if (codesList.size() == 0) {
                        HasCodes hasCodes = new HasCodes(context);
                        hasCodes.setHasCode(false, "");
                        ((Activity) context).findViewById(R.id.txtCodeText).setVisibility(View.VISIBLE);
                        ((Activity) context).findViewById(R.id.txtNoCodes).setVisibility(View.VISIBLE);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("message")) {
                                Toast.makeText(context.getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("DisassociateCode Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.e("DisassociateCode Error: ", t.getMessage());
                btnRemove.setEnabled(true);
            }
        });
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
            exitDate = itemView.findViewById(R.id.txtIdReview);
            rooms = itemView.findViewById(R.id.txtDateReview);
            remove = itemView.findViewById(R.id.btnCodeRemove);
        }
    }
}