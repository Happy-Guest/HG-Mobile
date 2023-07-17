package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Region {

    @SerializedName("description")
    private final String description;

    @SerializedName("descriptionEN")
    private final String descriptionEN;

    @Nullable
    @SerializedName("proximities")
    private final String proximities;

    @Nullable
    @SerializedName("activities")
    private final String activities;

    @Nullable
    @SerializedName("websites")
    private final String websites;

    public Region(String description, String descriptionEN, @Nullable String proximities, @Nullable String activities, @Nullable String websites) {
        this.description = description;
        this.descriptionEN = descriptionEN;
        this.proximities = proximities;
        this.activities = activities;
        this.websites = websites;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    @Nullable
    public String getProximities() {
        return proximities;
    }

    @Nullable
    public String getActivities() {
        return activities;
    }

    @Nullable
    public String getWebsites() {
        return websites;
    }
}
