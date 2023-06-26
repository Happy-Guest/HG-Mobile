package ipl.estg.happyguest.app.home.complaint;

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
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
    private final Context context;
    private final APIRoutes api;
    private final long complaintId;
    private final ActivityResultLauncher<String> requestWritePermissionLauncher;
    private final ActivityResultLauncher<String> requestReadPermissionLauncher;
    private ComplaintFile complaintFileSelected;
    private ViewHolder holder;

    public ComplaintFilesAdapter(ArrayList<ComplaintFile> complaintFilesList, Context context, ActivityResultLauncher<String> requestWritePermissionLauncher, ActivityResultLauncher<String> requestReadPermissionLauncher, long complaintId, APIRoutes api) {
        this.complaintFilesList = complaintFilesList;
        this.context = context;
        this.requestWritePermissionLauncher = requestWritePermissionLauncher;
        this.requestReadPermissionLauncher = requestReadPermissionLauncher;
        this.complaintId = complaintId;
        this.api = api;
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

        // Set Texts
        String title = context.getString(R.string.complaint_file) + " " + (position + 1);
        holder.id.setText(title);
        String name = complaintFile.getFilename().length() > 20 ? complaintFile.getFilename().substring(0, 20) + "..." : complaintFile.getFilename();
        holder.name.setText(name);

        // Set unique tag for the fileOpen button
        holder.fileOpen.setTag(position);

        // View Button
        holder.fileOpen.setOnClickListener(view -> {
            int clickedPosition = (int) view.getTag();
            complaintFileSelected = complaintFilesList.get(clickedPosition);
            this.holder = holder;
            openFile();
        });
    }

    public void openFile() {
        String permissionWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(context, permissionWrite) != PackageManager.PERMISSION_GRANTED) {
            requestWritePermissionLauncher.launch(permissionWrite);
        } else {
            String permissionRead = Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(context, permissionRead) != PackageManager.PERMISSION_GRANTED) {
                requestReadPermissionLauncher.launch(permissionRead);
            } else {
                getComplaintFileAttempt(complaintId, complaintFileSelected.getId(), complaintFileSelected.getFilename(), holder.fileOpen);
            }
        }
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
                        Toast.makeText(context, context.getString(R.string.error_file), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetComplaintFile Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, context.getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetComplaintFile Error: ", t.getMessage());
                fileOpen.setEnabled(true);
            }
        });
    }

    private void downloadFile(byte[] fileData, String fileName) {
        try {
            File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                boolean isDirCreated = downloadsDir.mkdirs();
                if (!isDirCreated) {
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