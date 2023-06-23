package ipl.estg.happyguest.app.home.complaint;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentComplaintBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ComplaintResponse;
import ipl.estg.happyguest.utils.models.Complaint;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;


public class ComplaintFragment extends Fragment {

    Long complaintId;
    private FragmentComplaintBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComplaintBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            Bundle args = getArguments();
            complaintId = args.getLong("id");
        }
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getComplaintAttempt();

        // Close Button
        binding.btnClose.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_complaints);
            }
        });

        return binding.getRoot();
    }

    private void getComplaintAttempt() {
        Call<ComplaintResponse> call = api.getComplaint(complaintId);
        call.enqueue(new Callback<ComplaintResponse>() {
            @Override
            public void onResponse(@NonNull Call<ComplaintResponse> call, @NonNull retrofit2.Response<ComplaintResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get Review and populate fields
                    Complaint complaint = response.body().getComplaint();
                    String date = getString(R.string.date) + ": " + complaint.getDate();
                    binding.txtDate.setText(date);
                    String complaintStatus = "";
                    switch (complaint.getStatus()) {
                        case 'P':
                            complaintStatus = getString(R.string.pending);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                            break;
                        case 'S':
                            complaintStatus = getString(R.string.solving);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA810F")));
                            break;
                        case 'R':
                            complaintStatus = getString(R.string.resolved);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                            break;
                        case 'C':
                            complaintStatus = getString(R.string.canceled);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                            break;
                    }
                    binding.txtStatusType.setText(complaintStatus);
                    binding.txtTitle.setText(complaint.getTitle());
                    binding.txtLocal.setText(complaint.getLocal());
                    binding.txtComment.setText(complaint.getComment());
                    binding.txtResponse.setText(complaint.getResponse() != null ? complaint.getResponse() : getString(R.string.no_response));
                    if (complaint.getResponse() != null && !complaint.getResponse().isEmpty() && !Objects.equals(complaint.getResponse(), "Sem resposta")) {
                        binding.txtResponse.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.dark_gray));
                        String dateUpdateAt = getString(R.string.response_date) + ": " + complaint.getUpdatedAt();
                        binding.txtResponseDate.setText(dateUpdateAt);
                        binding.txtResponseDate.setVisibility(View.VISIBLE);
                    } else {
                        binding.txtResponse.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray));
                        binding.txtResponseDate.setVisibility(View.GONE);
                    }
                    String dateCreatedAt = getString(R.string.createdDate) + ": " + complaint.getCreatedAt();
                    binding.txtCreatedDate.setText(dateCreatedAt);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.restore_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetComplaint Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ComplaintResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetComplaint Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}