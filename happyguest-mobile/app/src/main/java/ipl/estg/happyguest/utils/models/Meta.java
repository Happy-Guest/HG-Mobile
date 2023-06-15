package ipl.estg.happyguest.utils.models;

public class Meta {

    private final int total;
    private final int perPage;
    private final int currentPage;
    private final int lastPage;

    public Meta(int total, int perPage, int currentPage, int lastPage) {
        this.total = total;
        this.perPage = perPage;
        this.currentPage = currentPage;
        this.lastPage = lastPage;
    }

    public int getTotal() {
        return total;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}
