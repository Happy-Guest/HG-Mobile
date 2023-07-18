package ipl.estg.happyguest.app.home.hotel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHotelBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.HotelResponse;
import ipl.estg.happyguest.utils.models.Hotel;
import ipl.estg.happyguest.utils.models.HotelInfo;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class HotelFragment extends Fragment {

    private FragmentHotelBinding binding;
    private APIRoutes api;
    private HotelAdapter hotelAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelBinding.inflate(inflater, container, false);

        //API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getHotelAttempt();

        return binding.getRoot();
    }

    private void getHotelAttempt() {
        Call<HotelResponse> call = api.getHotel(1L);
        call.enqueue(new Callback<HotelResponse>() {
            @Override
            public void onResponse(@NonNull Call<HotelResponse> call, @NonNull retrofit2.Response<HotelResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Hotel and populate fields
                    Hotel hotel = response.body().getHotel();
                    binding.txtHotelEmail.setText(Objects.requireNonNull(hotel).getEmail());
                    String phone = hotel.getPhone().toString();
                    binding.txtHotelPhone.setText(phone);
                    binding.txtHotelAddress.setText(hotel.getAddress());
                    if (hotel.getWebsite() != null) {
                        binding.hotelWebsite.setVisibility(View.VISIBLE);
                        binding.txtHotelWebsite.setText(hotel.getWebsite());
                    }
                    if (hotel.getCapacity() != null) {
                        binding.hotelCapacity.setVisibility(View.VISIBLE);
                        String capacity = hotel.getCapacity().toString();
                        binding.txtHotelCapacity.setText(capacity);
                    }

                    // Get language code and set description
                    String languageCode = Locale.getDefault().getLanguage();
                    if (languageCode.equals("pt")) {
                        binding.txtDescription.setText(hotel.getDescription());
                    } else {
                        binding.txtDescription.setText(hotel.getDescriptionEN());
                    }

                    ArrayList<HotelInfo> commodities = toArrayHotelInfo(hotel.getCommodities());
                    ArrayList<HotelInfo> policies = toArrayHotelInfo(hotel.getPolicies());
                    ArrayList<HotelInfo> accesses = toArrayHotelInfo(hotel.getAccesses());

                    RecyclerView commoditiesRV = binding.commoditiesRV;
                    if (hotel.getCommodities() != null) {
                        binding.hotelCommodities.setVisibility(View.VISIBLE);
                        hotelAdapter = new HotelAdapter(commodities, binding.getRoot().getContext());
                        commoditiesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        commoditiesRV.setAdapter(hotelAdapter);
                    }
                    if (hotel.getPolicies() != null) {
                        binding.hotelPolicies.setVisibility(View.VISIBLE);
                        RecyclerView policiesRV = binding.policiesRV;
                        hotelAdapter = new HotelAdapter(policies, binding.getRoot().getContext());
                        policiesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        policiesRV.setAdapter(hotelAdapter);
                    }
                    if (hotel.getAccesses() != null) {
                        binding.hotelAccesses.setVisibility(View.VISIBLE);
                        RecyclerView accessesRV = binding.accessesRV;
                        hotelAdapter = new HotelAdapter(accesses, binding.getRoot().getContext());
                        accessesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        accessesRV.setAdapter(hotelAdapter);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetHotel Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HotelResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetHotel Error: ", t.getMessage());
            }
        });
    }

    public ArrayList<HotelInfo> toArrayHotelInfo(String inform) {
        ArrayList<HotelInfo> info = null;
        try {
            JSONArray jsonArray = new JSONArray(inform);
            info = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String nameEN = jsonObject.getString("nameEN");

                HotelInfo hotelInfo = new HotelInfo(name, nameEN);
                info.add(hotelInfo);
            }
        } catch (Exception e) {
            Log.e("HotelFragment", "Error parsing info", e);
        }
        return info;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}