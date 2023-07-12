package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("total")
    private final int total;
    @SerializedName("per_page")
    private final int perPage;
    @SerializedName("current_page")
    private final int currentPage;
    @SerializedName("last_page")
    private final int lastPage;

    public Meta(int total, int perPage, int currentPage, int lastPage) {
        this.total = total;
        this.perPage = perPage;
        this.currentPage = currentPage;
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}
