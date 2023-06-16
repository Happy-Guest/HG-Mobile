package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Code {

    @SerializedName("id")
    private Long id;
    @SerializedName("code")
    private String code;
    @SerializedName("entry_date")
    private String entryDate;
    @SerializedName("exit_date")
    private String exitDate;
    @SerializedName("rooms")
    private List<String> rooms;

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }
}
