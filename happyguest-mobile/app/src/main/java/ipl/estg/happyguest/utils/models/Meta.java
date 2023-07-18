package ipl.estg.happyguest.utils.models;

import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("current_page")
    private final int currentPage;
    @SerializedName("last_page")
    private final int lastPage;

    public Meta(int currentPage, int lastPage) {
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
