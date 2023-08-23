package ipl.estg.happyguest.app.home.complaint;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentComplaintsBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ComplaintsResponse;
import ipl.estg.happyguest.utils.models.Complaint;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintsFragment extends Fragment {

    private User user;
    private APIRoutes api;
    private FragmentComplaintsBinding binding;
    private ComplaintsAdapter complaintsAdapter;
    private ArrayList<Complaint> complaintsList;
    private Meta meta;
    private int screenHeight;
    private String selectedStatus = "ALL";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComplaintsBinding.inflate(inflater, container, false);

        // User, API, Token and HasCodes
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
        HasCodes hasCodes = new HasCodes(binding.getRoot().getContext());

        // Change register button
        binding.btnRegisterComplaint.setEnabled(hasCodes.getHasCode());

        // Register Complaint
        binding.btnRegisterComplaint.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_register_complaint);
            }
        });

        // Complaints
        RecyclerView reviewsRV = binding.complaintsRV;
        complaintsList = new ArrayList<>();
        complaintsAdapter = new ComplaintsAdapter(complaintsList, binding.getRoot().getContext());
        reviewsRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        reviewsRV.setAdapter(complaintsAdapter);

        // Set the minimum height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.1));

        // Get complaints on scroll
        binding.complaintsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView codesRV, int dx, int dy) {
                super.onScrolled(codesRV, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) codesRV.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == complaintsList.size() - 1) {
                    if (meta != null && meta.getCurrentPage() < meta.getLastPage()) {
                        getComplaintsAttempt(meta.getCurrentPage() + 1);
                    }
                }
            }
        });

        // Swipe to refresh complaints
        binding.swipeRefresh.setOnRefreshListener(this::getComplaints);

        // Switch status
        binding.spinnerSelectStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (binding.spinnerSelectStatus.getSelectedItem().toString()) {
                    case "All":
                    case "Todas":
                        selectedStatus = "ALL";
                        break;
                    case "Pending":
                    case "Pendentes":
                        selectedStatus = "P";
                        break;
                    case "Solving":
                    case "Resolução":
                        selectedStatus = "S";
                        break;
                    case "Resolved":
                    case "Resolvidas":
                        selectedStatus = "R";
                        break;
                    case "Annulled":
                    case "Anuladas":
                        selectedStatus = "C";
                        break;
                }
                if (binding.spinnerSelectStatus.isEnabled()) {
                    getComplaints();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedStatus = "ALL";
            }
        });

        return binding.getRoot();
    }

    private void getComplaints() {
        binding.spinnerSelectStatus.setEnabled(false);
        int previousItemCount = complaintsList.size();
        complaintsList.clear();
        getComplaintsAttempt(1);

        int newItemCount = complaintsList.size();
        if (newItemCount > previousItemCount) {
            complaintsAdapter.notifyItemRangeInserted(previousItemCount, newItemCount - previousItemCount);
        } else if (newItemCount < previousItemCount) {
            complaintsAdapter.notifyItemRangeRemoved(newItemCount, previousItemCount - newItemCount);
        } else {
            complaintsAdapter.notifyItemRangeChanged(0, newItemCount);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    private void getComplaintsAttempt(int page) {
        Call<ComplaintsResponse> call = api.getUserComplaints(user.getId(), page, selectedStatus);
        call.enqueue(new Callback<ComplaintsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ComplaintsResponse> call, @NonNull Response<ComplaintsResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.spinnerSelectStatus.setEnabled(true);
                binding.spinnerSelectStatus.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Save complaints and update the adapter
                    int lastPos = complaintsList.size();
                    ArrayList<Complaint> complaints = response.body().getData();
                    complaintsList.addAll(complaints);
                    meta = response.body().getMeta();
                    complaintsAdapter.notifyItemRangeInserted(lastPos, complaints.size());
                    if (complaintsList.size() == 0) {
                        binding.txtNoComplaints.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.txtNoComplaints.setVisibility(View.VISIBLE);
                        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.7));
                    } else {
                        binding.txtNoComplaints.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.txtNoComplaints.setVisibility(View.GONE);
                        binding.swipeRefresh.setMinimumHeight(screenHeight - 210);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.complaints_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetComplaints Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ComplaintsResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.complaints_error), Toast.LENGTH_SHORT).show();
                Log.i("GetComplaints Error: ", Objects.requireNonNull(t.getMessage()));
                binding.spinnerSelectStatus.setEnabled(true);
            }
        });
    }
}