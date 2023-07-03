package ipl.estg.happyguest.app.home.review;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentRegisterReviewBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.ReviewRequest;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterReviewFragment extends Fragment {

    public FragmentRegisterReviewBinding binding;
    private int currentStar = 0;
    private TextInputLayout inputComment;
    private EditText txtComment;
    private APIRoutes api;
    private User user;
    private CheckBox checkBox;
    private CheckBox checkAnonymous;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterReviewBinding.inflate(inflater, container, false);

        //User, API and Token
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Set click listeners for all stars
        binding.imageStar1.setOnClickListener(v -> updateStars(1));
        binding.imageStar2.setOnClickListener(v -> updateStars(2));
        binding.imageStar3.setOnClickListener(v -> updateStars(3));
        binding.imageStar4.setOnClickListener(v -> updateStars(4));
        binding.imageStar5.setOnClickListener(v -> updateStars(5));

        // TextInputLayouts and EditTexts
        inputComment = binding.inputComment;
        txtComment = binding.txtComment;

        // Cancel Button
        binding.btnClose.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_reviews);
            }
        });

        // Checkbox anonymous
        checkAnonymous = binding.checkAnonymous;
        checkAnonymous.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.review_anonymous_message), Toast.LENGTH_SHORT).show();
            }
        });

        // Change register review button
        binding.btnRegisterReview.setOnClickListener(v -> changeRegisterReviewClick());

        return binding.getRoot();
    }

    private void changeRegisterReviewClick() {
        inputComment.setError(null);
        String comment = txtComment.getText().toString();
        binding.textErrorStars.setVisibility(View.INVISIBLE);
        if (currentStar == 0) {
            binding.textErrorStars.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
            binding.textErrorStars.setVisibility(View.VISIBLE);
        } else if (!comment.isEmpty() && comment.length() < 5) {
            inputComment.setError(getString(R.string.comment_min_length));
        } else {
            showPopup();
        }
    }

    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup, null);

        // Create the popup window
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Set popup texts
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.title_Review));
        checkBox = popupView.findViewById(R.id.checkBoxAuthorization);
        checkBox.setVisibility(View.VISIBLE);

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            registerReviewAttempt();
            binding.btnRegisterReview.setEnabled(false);
            binding.btnClose.setEnabled(false);
            popupWindow.dismiss();
        });
    }

    private void registerReviewAttempt() {
        Call<MessageResponse> call = api.registerReview(new ReviewRequest(checkAnonymous.isChecked() ? null : user.getId(), currentStar, txtComment.getText().toString(), checkBox.isChecked() ? "1" : "0"));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.btnRegisterReview.setEnabled(true);
                binding.btnClose.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and change fragment
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.changeFragment(R.id.nav_reviews);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("comment")) {
                                    inputComment.setError(errors.getJSONArray("comment").get(0).toString());
                                }
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("RegisterReview Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("RegisterReview Error: ", t.getMessage());
                binding.btnRegisterReview.setEnabled(true);
                binding.btnClose.setEnabled(true);
            }
        });
    }

    // Update stars to selected star
    private void updateStars(int selectedStar) {
        currentStar = selectedStar;
        for (int i = 1; i <= 5; i++) {
            ImageButton starImageView = getStarImageView(i);
            if (i <= selectedStar) {
                starImageView.setVisibility(View.INVISIBLE);
                starImageView.setImageResource(R.drawable.star_full_icon);
                starImageView.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                starImageView.setVisibility(View.VISIBLE);
            } else {
                starImageView.setImageResource(R.drawable.star_icon);
            }
        }
    }

    // Get star image view
    private ImageButton getStarImageView(int star) {
        ImageButton starImageView = null;
        switch (star) {
            case 1:
                starImageView = binding.imageStar1;
                break;
            case 2:
                starImageView = binding.imageStar2;
                break;
            case 3:
                starImageView = binding.imageStar3;
                break;
            case 4:
                starImageView = binding.imageStar4;
                break;
            case 5:
                starImageView = binding.imageStar5;
                break;
        }
        return starImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}