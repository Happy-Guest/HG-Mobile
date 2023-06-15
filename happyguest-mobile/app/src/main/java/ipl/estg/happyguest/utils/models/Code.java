package ipl.estg.happyguest.utils.models;

import java.util.Date;
import java.util.List;

public class Code {

    private Long id;
    private String code;
    private Date entryDate;
    private Date exitDate;
    private List<String> rooms;

    public Code(Long id, String code, Date entryDate, Date exitDate, List<String> rooms) {
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

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }
}
