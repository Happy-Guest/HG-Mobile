package ipl.estg.happyguest.app.home.reserve;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReserveBinding;
import ipl.estg.happyguest.databinding.FragmentReservesBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.OrderResponse;
import ipl.estg.happyguest.utils.api.responses.ReserveResponse;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.OrderItem;
import ipl.estg.happyguest.utils.models.Reserve;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;


public class ReserveFragment extends Fragment {

    Long reserveId;
    private FragmentReserveBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReserveBinding.inflate(inflater, container, false);

        // Get the order id
        if (getArguments() != null) {
            Bundle args = getArguments();
            reserveId = args.getLong("id");
        }

        // Token and API
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getReserveAttempt();

        // Cancel Button
        //binding.btnCancel.setOnClickListener(v -> showPopup());

        //Close Button
        binding.btnClose.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_reserves);
            }
        });

        return binding.getRoot();
    }

    private void getReserveAttempt() {
        Call<ReserveResponse> call = api.getReserve(reserveId);
        call.enqueue(new Callback<ReserveResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReserveResponse> call, @NonNull retrofit2.Response<ReserveResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Reserve and populate fields
                    Reserve reserve = response.body().getReserve();
                    String languageCode = Locale.getDefault().getLanguage();
                    if (Objects.requireNonNull(reserve).getService().type == 'O') {
                        String serviceName = getString(R.string.service_name) + " " + (languageCode.equals("pt") ? reserve.getService().name : reserve.getService().nameEN);
                        binding.txtServiceName.setText(serviceName);
                        binding.txtServiceName.setVisibility(View.VISIBLE);
                    }
                    String date = getString(R.string.date) + " " + Objects.requireNonNull(reserve).getCreatedAt();
                    binding.txtDate.setText(date);
                    String reserveStatus = "";
                    switch (reserve.getStatus()) {
                        case "P":
                            reserveStatus = getString(R.string.pending);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                            break;
                        case "R":
                            reserveStatus = getString(R.string.rejected);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                            break;
                        case "A":
                            reserveStatus = getString(R.string.accepted);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                            break;
                        case "C":
                            reserveStatus = getString(R.string.canceled);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b45f06")));
                            break;
                    }
                    binding.txtStatusType.setText(reserveStatus);
                    String nr_people = reserve.getNr_people().toString();
                    binding.txtNrPeopleReserve.setText(nr_people);
                    String schedule = reserve.getTime();
                    binding.txtScheduleReserve.setText(schedule);
                    String comment = reserve.getComment() != null ? reserve.getComment() : getString(R.string.no_comment);
                    binding.txtComment.setText(comment);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetReserve Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReserveResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetReserve Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}