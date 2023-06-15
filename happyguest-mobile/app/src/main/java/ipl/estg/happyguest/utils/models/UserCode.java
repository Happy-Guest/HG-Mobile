package ipl.estg.happyguest.utils.models;

public class UserCode {

    private final Long id;
    private final Code code;

    public UserCode(Long id, Code code) {
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public Code getCode() {
        return code;
    }
}
