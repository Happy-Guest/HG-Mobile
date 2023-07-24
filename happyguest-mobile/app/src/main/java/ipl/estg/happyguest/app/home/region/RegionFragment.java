package ipl.estg.happyguest.app.home.region;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentRegionBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.RegionResponse;
import ipl.estg.happyguest.utils.models.Region;
import ipl.estg.happyguest.utils.models.RegionInfo;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class RegionFragment extends Fragment {

    private FragmentRegionBinding binding;
    private APIRoutes api;
    private RegionAdapter regionAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegionBinding.inflate(inflater, container, false);

        //API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getRegionAttempt();

        return binding.getRoot();
    }

    private void getRegionAttempt() {
        Call<RegionResponse> call = api.getRegion(1L);
        call.enqueue(new Callback<RegionResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegionResponse> call, @NonNull retrofit2.Response<RegionResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Region and populate fields
                    Region region = response.body().getRegion();
                    // Get language code and set description
                    String languageCode = Locale.getDefault().getLanguage();
                    if (languageCode.equals("pt")) {
                        binding.txtDescription.setText(Objects.requireNonNull(region).getDescription());
                    } else {
                        binding.txtDescription.setText(Objects.requireNonNull(region).getDescriptionEN());
                    }

                    ArrayList<RegionInfo> proximity = toArrayRegionInfo(region.getProximity());
                    ArrayList<RegionInfo> activities = toArrayRegionInfo(region.getActivities());

                    RecyclerView proximityRV = binding.proximitiesRV;
                    if (region.getProximity() != null) {
                        binding.regionProximities.setVisibility(View.VISIBLE);
                        regionAdapter = new RegionAdapter(proximity, binding.getRoot().getContext());
                        proximityRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        proximityRV.setAdapter(regionAdapter);
                    }
                    RecyclerView activitiesRV = binding.activitiesRV;
                    if (region.getActivities() != null) {
                        binding.regionActivities.setVisibility(View.VISIBLE);
                        regionAdapter = new RegionAdapter(activities, binding.getRoot().getContext());
                        activitiesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                        activitiesRV.setAdapter(regionAdapter);
                    }

                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetRegion Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegionResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetRegion Error: ", t.getMessage());
            }
        });
    }

    private ArrayList<RegionInfo> toArrayRegionInfo(String inform) {
        ArrayList<RegionInfo> info = null;
        try {
            JSONArray jsonArray = new JSONArray(inform);
            info = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String description = jsonObject.getString("description");
                String descriptionEN = jsonObject.getString("description_en");
                String distance = jsonObject.getString("distance");
                String link = jsonObject.getString("link");

                RegionInfo regionInfo = new RegionInfo(name, description, descriptionEN, distance, link);
                info.add(regionInfo);
            }
        } catch (Exception e) {
            Log.e("RegionFragment", "Error parsing info", e);
        }
        return info;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}