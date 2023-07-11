package ipl.estg.happyguest.app.home.order;

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

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.complaint.ComplaintsAdapter;
import ipl.estg.happyguest.databinding.FragmentOrdersBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ComplaintsResponse;
import ipl.estg.happyguest.utils.api.responses.OrdersResponse;
import ipl.estg.happyguest.utils.models.Complaint;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrdersFragment extends Fragment {

    private User user;
    private APIRoutes api;
    private FragmentOrdersBinding binding;
    private OrdersAdapter ordersAdapter;
    private ArrayList<Order> ordersList;
    private Meta meta;
    private int screenHeight;
    private String selectedType = "ALL";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        // User, API, Token and HasCodes
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Orders
        RecyclerView ordersRV = binding.ordersRV;
        ordersList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(ordersList, binding.getRoot().getContext());
        ordersRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        ordersRV.setAdapter(ordersAdapter);

        // Set the minimum height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.3));

        // Get complaints on scroll
        binding.ordersRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView codesRV, int dx, int dy) {
                super.onScrolled(codesRV, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) codesRV.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ordersList.size() - 1) {
                    if (meta != null && meta.getCurrentPage() < meta.getLastPage()) {
                        getOrdersAttempt(meta.getCurrentPage() + 1);
                    }
                }
            }
        });

        // Swipe to refresh complaints
        binding.swipeRefresh.setOnRefreshListener(this::getOrders);

       //Filters
        // Switch status
        binding.spinnerSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (binding.spinnerSelectType.getSelectedItem().toString()) {
                    case "All":
                    case "Todos":
                        selectedType = "ALL";
                        break;
                    case "Cleaning Room":
                    case "Limpeza de Quarto":
                        selectedType = "OC";
                        break;
                    case "Order Objects":
                    case "Pedido de Objetos":
                        selectedType = "OB";
                        break;
                    case "Order Food":
                    case "Pedido de Alimentos":
                        selectedType = "OF";
                        break;
                    case "Other":
                    case "Outros":
                        selectedType = "O";
                        break;
                }
                if (binding.spinnerSelectType.isEnabled()) {
                    getOrders();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedType = "ALL";
            }
        });


        return binding.getRoot();
    }

    private void getOrders(){
        binding.spinnerSelectType.setEnabled(false);
        int previousItemCount = ordersList.size();
        ordersList.clear();
        getOrdersAttempt(1);

        int newItemCount = ordersList.size();
        if (newItemCount > previousItemCount) {
            ordersAdapter.notifyItemRangeInserted(previousItemCount, newItemCount - previousItemCount);
        } else if (newItemCount < previousItemCount) {
            ordersAdapter.notifyItemRangeRemoved(newItemCount, previousItemCount - newItemCount);
        } else {
            ordersAdapter.notifyItemRangeChanged(0, newItemCount);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    private void getOrdersAttempt(int page) {
        Call<OrdersResponse> call = api.getUserOrders(user.getId(), page, selectedType);
        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrdersResponse> call, @NonNull Response<OrdersResponse> response) {
                // Check if this fragment is still attached to the activity
                binding.spinnerSelectType.setEnabled(true);
                binding.spinnerSelectType.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Save complaints and update the adapter
                    int lastPos = ordersList.size();
                    ArrayList<Order> orders = response.body().getData();
                    ordersList.addAll(orders);
                    meta = response.body().getMeta();
                    ordersAdapter.notifyItemRangeInserted(lastPos, orders.size());
                    if (ordersList.size() == 0) {
                        binding.txtNoOrderRequest.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.txtNoOrderRequest.setVisibility(View.VISIBLE);
                        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.7));
                    } else {
                        binding.txtNoOrderRequest.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.txtNoOrderRequest.setVisibility(View.GONE);
                        binding.swipeRefresh.setMinimumHeight(screenHeight - 210);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.orders_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetOrders Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrdersResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.orders_error), Toast.LENGTH_SHORT).show();
                Log.i("GetOrders Error: ", t.getMessage());
                binding.spinnerSelectType.setEnabled(true);
            }
        });
    }

}