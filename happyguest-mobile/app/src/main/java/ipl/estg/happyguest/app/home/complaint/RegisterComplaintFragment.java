package ipl.estg.happyguest.app.home.complaint;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static ipl.estg.happyguest.utils.others.Images.getStreamByteFromImage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentRegisterComplaintBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.ComplaintRequest;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterComplaintFragment extends Fragment {

    private final List<byte[]> files = new ArrayList<>();
    // Select files
    private final ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri selectedFile = result.getData().getData();
                        try {
                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedFile);
                            File tempFile = File.createTempFile("temp_file", null, requireActivity().getCacheDir());
                            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            inputStream.close();
                            fileOutputStream.close();
                            // Check if the file is an image or a PDF
                            String mimeType = requireActivity().getContentResolver().getType(selectedFile);
                            if (mimeType != null && mimeType.startsWith("image/")) {
                                files.add(getStreamByteFromImage(tempFile));
                            } else if (mimeType != null && mimeType.equals("application/pdf")) {
                                byte[] pdfBytes = getStreamByteFromFile(tempFile);
                                files.add(pdfBytes);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private FragmentRegisterComplaintBinding binding;
    private TextInputLayout inputDate;
    private TextInputLayout inputTitle;
    private TextInputLayout inputLocal;
    private TextInputLayout inputComment;
    private EditText txtDate;
    private EditText txtTitle;
    private EditText txtLocal;
    private EditText txtComment;
    private CheckBox checkAnonymous;
    private User user;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterComplaintBinding.inflate(inflater, container, false);

        // TextInputLayouts and EditTexts
        inputDate = binding.inputDate;
        inputTitle = binding.inputTitle;
        inputLocal = binding.inputLocal;
        inputComment = binding.inputComment;
        txtDate = binding.txtDate;
        txtTitle = binding.txtTitle;
        txtLocal = binding.txtLocal;
        txtComment = binding.txtComment;

        // User, API and Token
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        //Register Button
        binding.btnRegisterComplaint.setOnClickListener(v -> changeRegisterComplaintClick());

        // Cancel Button
        binding.btnClose.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_complaints);
            }
        });

        // Checkbox anonymous
        checkAnonymous = binding.checkAnonymous;
        checkAnonymous.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.complaint_anonymous_message), Toast.LENGTH_SHORT).show();
            }
        });

        // Select Files
        binding.btnAddFiles.setOnClickListener(view -> {
            Intent photoPicker = new Intent();
            photoPicker.setType("*/*");
            photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPicker.setAction(Intent.ACTION_GET_CONTENT);
            String[] mimeTypes = {"image/*", "application/pdf"};
            photoPicker.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityResult.launch(Intent.createChooser(photoPicker, getString(R.string.select_files)));
        });

        // Add "/" to birth date
        final int[] dateLength = {txtDate.getText().toString().length()};
        txtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dateLength[0] = txtDate.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String birthDate = txtDate.getText().toString();
                if ((birthDate.length() == 2 || birthDate.length() == 5) && !birthDate.endsWith("/") && dateLength[0] < birthDate.length()) {
                    txtDate.append("/");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String date = txtDate.getText().toString();
                if (date.length() == 2 && !date.endsWith("/")) {
                    txtDate.setText(date.substring(0, 1));
                    txtDate.setSelection(txtDate.getText().length());
                } else if (date.length() == 5 && !date.endsWith("/")) {
                    txtDate.setText(date.substring(0, 4));
                    txtDate.setSelection(txtDate.getText().length());
                }
            }
        });

        return binding.getRoot();
    }

    private void changeRegisterComplaintClick() {
        inputDate.setError(null);
        inputTitle.setError(null);
        inputLocal.setError(null);
        inputComment.setError(null);
        String date = txtDate.getText().toString();
        String title = txtTitle.getText().toString();
        String local = txtLocal.getText().toString();
        String comment = txtComment.getText().toString();

        if (date.isEmpty()) {
            inputDate.setError(getString(R.string.date_required));
        } else if (title.isEmpty()) {
            inputTitle.setError(getString(R.string.title_required));
        } else if (title.length() < 5) {
            inputTitle.setError(getString(R.string.title_min_length));
        } else if (local.isEmpty()) {
            inputLocal.setError(getString(R.string.local_required));
        } else if (local.length() < 5) {
            inputLocal.setError(getString(R.string.local_min_length));
        } else if (comment.isEmpty()) {
            inputComment.setError(getString(R.string.comment_required));
        } else if (comment.length() < 5) {
            inputComment.setError(getString(R.string.comment_min_length));
        } else {
            showPopup();
        }
    }

    private byte[] getStreamByteFromFile(File file) throws IOException {
        try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(Uri.fromFile(file))) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
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
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.title_Complaint));

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            registerComplaintAttempt();
            binding.btnRegisterComplaint.setEnabled(false);
            popupWindow.dismiss();

        });
    }

    // Convert photos to base64
    private List<String> convertPhotosToBase64(List<byte[]> photos) {
        List<String> base64Photos = new ArrayList<>();
        for (byte[] photo : photos) {
            String base64Photo = Base64.encodeToString(photo, Base64.DEFAULT);
            base64Photos.add(base64Photo);
        }
        return base64Photos;
    }

    private void registerComplaintAttempt() {
        Call<MessageResponse> call = api.registerComplaint(new ComplaintRequest(checkAnonymous.isChecked() ? null : user.getId(), txtTitle.getText().toString(),
                txtLocal.getText().toString(), "P", txtComment.getText().toString(),
                formatDate(txtDate.getText().toString()), files.isEmpty() ? null : convertPhotosToBase64(files)));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                binding.btnRegisterComplaint.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and change fragment
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.changeFragment(R.id.nav_complaints);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("title")) {
                                    inputTitle.setError(errors.getJSONArray("title").get(0).toString());
                                }
                                if (errors.has("local")) {
                                    inputLocal.setError(errors.getJSONArray("local").get(0).toString());
                                }
                                if (errors.has("date")) {
                                    inputDate.setError(errors.getJSONArray("date").get(0).toString());
                                }
                                if (errors.has("comment")) {
                                    inputComment.setError(errors.getJSONArray("comment").get(0).toString());
                                }
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("RegisterComplaint Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("RegisterComplaint Error: ", t.getMessage());
                binding.btnRegisterComplaint.setEnabled(true);
            }
        });

    }

    private String formatDate(String date) {
        String[] dateArray = date.split("/");
        return dateArray[2] + "/" + dateArray[1] + "/" + dateArray[0];
    }
}