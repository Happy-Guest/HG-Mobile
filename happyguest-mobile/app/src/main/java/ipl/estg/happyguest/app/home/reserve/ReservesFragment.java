package ipl.estg.happyguest.app.home.reserve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentReservesBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ReservesResponse;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Reserve;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservesFragment extends Fragment {

    private FragmentReservesBinding binding;
    private User user;
    private APIRoutes api;
    private ReservesAdapter reservesAdapter;
    private ArrayList<Reserve> reservesList;
    private Meta meta;
    private int screenHeight;
    private String selectedType = "ALL";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservesBinding.inflate(inflater, container, false);

        // Get the selected type
        if (getArguments() != null) {
            Bundle args = getArguments();
            selectedType = args.getString("filter") == null ? "ALL" : args.getString("filter");
            switch (Objects.requireNonNull(selectedType)) {
                case "OR":
                    binding.spinnerSelectType.setSelection(1);
                    break;
                case "O":
                    binding.spinnerSelectType.setSelection(3);
                    break;
            }
        }

        // User, API, Token and HasCodes
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Reserves
        RecyclerView reservesRV = binding.reservesRV;
        reservesList = new ArrayList<>();
        reservesAdapter = new ReservesAdapter(reservesList, binding.getRoot().getContext());
        reservesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        reservesRV.setAdapter(reservesAdapter);

        // Set the minimum height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.1));

        // Get complaints on scroll
        binding.reservesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView codesRV, int dx, int dy) {
                super.onScrolled(codesRV, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) codesRV.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == reservesList.size() - 1) {
                    if (meta != null && meta.getCurrentPage() < meta.getLastPage()) {
                        getReservesAttempt(meta.getCurrentPage() + 1);
                    }
                }
            }
        });

        // Swipe to refresh complaints
        binding.swipeRefresh.setOnRefreshListener(this::getReserves);

        // Filters
        binding.spinnerSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (binding.spinnerSelectType.getSelectedItem().toString()) {
                    case "All":
                    case "Todos":
                        selectedType = "ALL";
                        break;
                    case "Table Reservation":
                    case "Reserva de Mesa":
                        selectedType = "OR";
                        break;
                    case "Other":
                    case "Outros":
                        selectedType = "O";
                        break;
                }
                if (binding.spinnerSelectType.isEnabled()) {
                    getReserves();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedType = "ALL";
            }
        });

        return binding.getRoot();
    }

    private void getReserves() {
        binding.spinnerSelectType.setEnabled(false);
        int previousItemCount = reservesList.size();
        reservesList.clear();
        getReservesAttempt(1);

        int newItemCount = reservesList.size();
        if (newItemCount > previousItemCount) {
            reservesAdapter.notifyItemRangeInserted(previousItemCount, newItemCount - previousItemCount);
        } else if (newItemCount < previousItemCount) {
            reservesAdapter.notifyItemRangeRemoved(newItemCount, previousItemCount - newItemCount);
        } else {
            reservesAdapter.notifyItemRangeChanged(0, newItemCount);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    private void getReservesAttempt(int page) {
        Call<ReservesResponse> call = api.getUserReserves(user.getId(), page, selectedType);
        call.enqueue(new Callback<ReservesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservesResponse> call, @NonNull Response<ReservesResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.spinnerSelectType.setEnabled(true);
                binding.spinnerSelectType.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Save orders and update the adapter
                    int lastPos = reservesList.size();
                    ArrayList<Reserve> reserves = response.body().getData();
                    reservesList.addAll(reserves);
                    meta = response.body().getMeta();
                    reservesAdapter.notifyItemRangeInserted(lastPos, reserves.size());
                    if (reservesList.size() == 0) {
                        binding.txtNoReservesRequest.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.txtNoReservesRequest.setVisibility(View.VISIBLE);
                        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.7));
                    } else {
                        binding.txtNoReservesRequest.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.txtNoReservesRequest.setVisibility(View.GONE);
                        binding.swipeRefresh.setMinimumHeight(screenHeight - 210);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.reserves_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetReserves Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservesResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.reserves_error), Toast.LENGTH_SHORT).show();
                Log.i("GetReserves Error: ", Objects.requireNonNull(t.getMessage()));
                binding.spinnerSelectType.setEnabled(true);
            }
        });
    }
}