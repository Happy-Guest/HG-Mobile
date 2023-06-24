package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class ComplaintFile {

    @SerializedName("id")
    private final Long id;
    @SerializedName("filename")
    private final String filename;

    public ComplaintFile(Long id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }
}
