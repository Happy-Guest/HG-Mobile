package ipl.estg.happyguest.app.home.review;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.models.ComplaintFile;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintFilesAdapter extends RecyclerView.Adapter<ComplaintFilesAdapter.ViewHolder> {

    private final ArrayList<ComplaintFile> complaintFilesList;
    private final FragmentActivity activity;
    private final Context context;
    private final APIRoutes api;
    private final long complaintId;
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private ComplaintFile complaintFile;
    private ViewHolder holder;

    public ComplaintFilesAdapter(ArrayList<ComplaintFile> complaintFilesList, FragmentActivity activity, Context context, long complaintId, APIRoutes api) {
        this.complaintFilesList = complaintFilesList;
        this.activity = activity;
        this.context = context;
        this.complaintId = complaintId;
        this.api = api;

        // Request Permission Launcher
        requestPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        getComplaintFileAttempt(complaintId, complaintFile.getId(), complaintFile.getFilename(), holder.fileOpen);
                    } else {
                        Toast.makeText(context, R.string.write_permission_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_file_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Complaint File
        ComplaintFile complaintFile = complaintFilesList.get(position);
        this.complaintFile = complaintFile;
        this.holder = holder;

        // Set Texts
        String title = context.getString(R.string.complaint_file) + " " + (position + 1);
        holder.id.setText(title);
        String name = complaintFile.getFilename().length() > 20 ? complaintFile.getFilename().substring(0, 20) + "..." : complaintFile.getFilename();
        holder.name.setText(name);

        // View Button
        holder.fileOpen.setOnClickListener(view -> {
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (activity != null) {
                    requestPermissionLauncher.launch(permission);
                }
            } else {
                getComplaintFileAttempt(complaintId, complaintFile.getId(), complaintFile.getFilename(), holder.fileOpen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintFilesList.size();
    }

    private void getComplaintFileAttempt(long complaintId, long fileId, String fileName, Button fileOpen) {
        fileOpen.setEnabled(false);
        Call<ResponseBody> call = api.getComplaintFile(complaintId, fileId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                fileOpen.setEnabled(true);
                if (response.isSuccessful()) {
                    try {
                        byte[] fileData = Objects.requireNonNull(response.body()).bytes();
                        downloadFile(fileData, fileName);
                    } catch (IOException e) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_file), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetComplaintFile Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetComplaintFile Error: ", t.getMessage());
                fileOpen.setEnabled(true);
            }
        });
    }

    private void downloadFile(byte[] fileData, String fileName) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                boolean isDirCreated = downloadsDir.mkdirs();
                if (!isDirCreated) {
                    // Directory creation failed
                    Toast.makeText(context, context.getString(R.string.error_file_app), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            File file = new File(downloadsDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileData);
            outputStream.close();
            // Notify the MediaScanner about the new file
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
            // Open the file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            String mimeType = getMimeType(file.getName());
            intent.setDataAndType(fileUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            Toast.makeText(context, context.getString(R.string.file_downloaded), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.error_file), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getMimeType(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView id;
        private final TextView name;
        private final Button fileOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdComplaintFile);
            name = itemView.findViewById(R.id.txtNameComplaintFile);
            fileOpen = itemView.findViewById(R.id.btnComplaintFileOpen);
        }
    }
}