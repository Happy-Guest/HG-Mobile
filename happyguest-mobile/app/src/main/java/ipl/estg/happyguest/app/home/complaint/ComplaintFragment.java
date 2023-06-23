package ipl.estg.happyguest.app.home.complaint;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentComplaintBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ComplaintResponse;
import ipl.estg.happyguest.utils.api.responses.ReviewResponse;
import ipl.estg.happyguest.utils.models.Review;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;


public class ComplaintFragment extends Fragment {

    private FragmentComplaintBinding binding;

    private APIRoutes api;

    Long complaintId;

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
                    String date = getString(R.string.date) + ": " + complaint.getCreatedAt();
                    binding.txtDate.setText(date);
                    switch (complaint.getStatus()) {
                        case "P":
                            binding.txtStatusType.setBackgroundTintList();
                            binding.txtStatusType.setText(getString(R.string.pending));
                            break;
                        case "S":
                            binding.txtStatusType.setTextColor(getResources().getColor(R.color.orange));
                            binding.txtStatusType.setText(getString(R.string.solving));
                            break;
                        case "R":
                            binding.txtStatusType.setTextColor(getResources().getColor(R.color.green));
                            binding.txtStatusType.setText(getString(R.string.resolved));
                            break;
                        case "C":
                            binding.txtStatusType.setTextColor(getResources().getColor(R.color.red));
                            binding.txtStatusType.setText(getString(R.string.canceled));
                            break;
                    }
                    binding.txtTitle.setText(complaint.getTitle());
                    binding.txtLocal.setText(complaint.getLocal());
                    binding.txtComment.setText(complaint.getComment());
                    String txtResponse = getString(R.string.response) + ": " + complaint.getResponse();
                    binding.txtResponse.setText(txtResponse);
                    String dateUpdateAt = getString(R.string.response_date) + ": " + complaint.getUpdateAt();
                    binding.txtResponseDate.setText(dateUpdateAt);
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