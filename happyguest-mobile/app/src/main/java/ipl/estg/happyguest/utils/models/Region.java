package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Region {

    @SerializedName("description")
    private final String description;

    @SerializedName("descriptionEN")
    private final String descriptionEN;

    @Nullable
    @SerializedName("proximity")
    private final String proximity;

    @Nullable
    @SerializedName("activities")
    private final String activities;

    @Nullable
    @SerializedName("websites")
    private final String websites;

    public Region(String description, String descriptionEN, @Nullable String proximity, @Nullable String activities, @Nullable String websites) {
        this.description = description;
        this.descriptionEN = descriptionEN;
        this.proximity = proximity;
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
    public String getProximity() {
        return proximity;
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
