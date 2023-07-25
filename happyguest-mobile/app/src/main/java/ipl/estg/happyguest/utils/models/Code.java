package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Code {

    @SerializedName("id")
    private final Long id;

    @SerializedName("code")
    private final String code;

    @SerializedName("entry_date")
    private final String entryDate;

    @SerializedName("exit_date")
    private final String exitDate;

    @SerializedName("rooms")
    private final List<String> rooms;

    public Code(Long id, String code, String entryDate, String exitDate, List<String> rooms) {
        this.id = id;
        this.code = code;
        this.entryDate = entryDate;
        this.exitDate = exitDate;
        this.rooms = rooms;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public List<String> getRooms() {
        return rooms;
    }
}
