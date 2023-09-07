package ipl.estg.happyguest.app.home.complaint;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentComplaintBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ComplaintResponse;
import ipl.estg.happyguest.utils.models.Complaint;
import ipl.estg.happyguest.utils.models.ComplaintFile;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class ComplaintFragment extends Fragment {

    Long complaintId;
    private FragmentComplaintBinding binding;
    private APIRoutes api;
    private ComplaintFilesAdapter complaintFilesAdapter;
    private ActivityResultLauncher<String> requestWritePermissionLauncher;
    private ActivityResultLauncher<String> requestReadPermissionLauncher;
    private ArrayList<ComplaintFile> complaintFilesList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Requesting write permission
        requestWritePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isWriteGranted -> {
                    if (isWriteGranted) {
                        complaintFilesAdapter.openFile();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.write_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Requesting read permission
        requestReadPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isReadGranted -> {
                    if (isReadGranted) {
                        complaintFilesAdapter.openFile();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.read_permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComplaintBinding.inflate(inflater, container, false);

        // Get complaint id
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
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Complaint and populate fields
                    Complaint complaint = response.body().getComplaint();
                    String date = getString(R.string.date) + " " + Objects.requireNonNull(complaint).getDate();
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
                    // Response
                    binding.txtResponse.setText(complaint.getResponse() != null ? complaint.getResponse() : getString(R.string.no_response));
                    if (complaint.getResponse() != null && !complaint.getResponse().isEmpty() && !Objects.equals(complaint.getResponse(), "Sem resposta") && !Objects.equals(complaint.getResponse(), "No Response")) {
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
                    // Files
                    if (complaint.getFiles() != null && complaint.getFiles().size() > 0) {
                        binding.complaintFilesRV.setVisibility(View.VISIBLE);
                        binding.txtFiles.setVisibility(View.VISIBLE);
                        RecyclerView complaintFilesRV = binding.complaintFilesRV;
                        complaintFilesList = new ArrayList<>();
                        complaintFilesAdapter = new ComplaintFilesAdapter(complaintFilesList, binding.getRoot().getContext(), requestWritePermissionLauncher, requestReadPermissionLauncher, complaintId, api);
                        complaintFilesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        complaintFilesRV.setAdapter(complaintFilesAdapter);
                        complaintFilesList.addAll(complaint.getFiles());
                        complaintFilesAdapter.notifyItemRangeInserted(0, complaint.getFiles().size());
                    } else {
                        binding.complaintFilesRV.setVisibility(View.GONE);
                        binding.txtFiles.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetComplaint Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ComplaintResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetComplaint Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}