package ipl.estg.happyguest.app.home.order;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentOrderBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.OrderResponse;
import ipl.estg.happyguest.utils.models.Order;
import ipl.estg.happyguest.utils.models.OrderItem;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class OrderFragment extends Fragment {

    Long orderId;
    private FragmentOrderBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOrderBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            Bundle args = getArguments();
            orderId = args.getLong("id");
        }

        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getOrderAttempt();

        // Close Button
        binding.btnCancel.setOnClickListener(v -> {
            //code cancel order
        });

        return binding.getRoot();
    }

    private void getOrderAttempt() {
        Call<OrderResponse> call = api.getOrder(orderId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call, @NonNull retrofit2.Response<OrderResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Order and populate fields
                    Order order = response.body().getOrder();
                    if (Objects.requireNonNull(order).getService().type == 'C') {
                        binding.txtDate.setVisibility(View.VISIBLE);
                    }
                    String date = getString(R.string.date) + ": " + Objects.requireNonNull(order).getCreatedAt();
                    binding.txtDate.setText(date);
                    String orderStatus = "";
                    switch (order.getStatus()) {
                        case "P":
                            orderStatus = getString(R.string.pending);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
                            break;
                        case "R":
                            orderStatus = getString(R.string.rejected);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#932218")));
                            break;
                        case "W":
                            orderStatus = getString(R.string.working);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA810F")));
                            break;
                        case "DL":
                            orderStatus = getString(R.string.delivered);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF189329")));
                            break;
                        case "C":
                            orderStatus = getString(R.string.canceled);
                            binding.txtStatusType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b45f06")));
                            break;
                    }
                    binding.txtStatusType.setText(orderStatus);
                    String room = order.getRoom().toString();
                    binding.txtRoomOrder.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                    binding.txtRoomOrder.setText(room);
                    String schedule = getString(R.string.services_schedule) + " " + order.getTime();
                    binding.txtSchedule.setText(schedule);
                    StringBuilder sb = new StringBuilder();
                    if (order.getItems() != null) {
                        binding.txtItems.setVisibility(View.VISIBLE);
                        binding.txtItemsOrder.setVisibility(View.VISIBLE);
                        if (order.getService().type == 'B')
                            binding.txtItems.setText(getString(R.string.order_objects));
                        else
                            binding.txtItems.setText(getString(R.string.order_foods));

                        for (OrderItem item : order.getItems()) {
                            sb.append(item.getQuantity()).append("x ").append(item.getName()).append("\n");
                        }
                        sb.append(getString(R.string.total_price)).append(" ").append(order.getPrice()).append("€");
                    }
                    binding.txtItemsOrder.setText(sb.toString());
                    String comment = order.getComment() != null ? order.getComment() : getString(R.string.no_comment);
                    binding.txtCommentOrder.setText(comment);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetOrder Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetOrder Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}