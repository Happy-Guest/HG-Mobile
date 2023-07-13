package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Hotel {

    @SerializedName("description")
    private final String description;

    @SerializedName("descriptionEN")
    private final String descriptionEN;

    @SerializedName("phone")
    private final Long phone;

    @SerializedName("email")
    private final String email;

    @SerializedName("address")
    private final String address;

    @Nullable
    @SerializedName("website")
    private final String website;

    @Nullable
    @SerializedName("capacity")
    private final Long capacity;

    @Nullable
    @SerializedName("policies")
    private final String policies;

    @Nullable
    @SerializedName("accesses")
    private final String accesses;

    @Nullable
    @SerializedName("commodities")
    private final String commodities;

    public Hotel(String description, String descriptionEN, Long phone, String email, String address, @Nullable String website, @Nullable Long capacity, @Nullable String policies, @Nullable String accesses, @Nullable String commodities) {
        this.description = description;
        this.descriptionEN = descriptionEN;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.website = website;
        this.capacity = capacity;
        this.policies = policies;
        this.accesses = accesses;
        this.commodities = commodities;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public Long getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    @Nullable
    public Long getCapacity() {
        return capacity;
    }

    @Nullable
    public String getPolicies() {
        return policies;
    }

    @Nullable
    public String getAccesses() {
        return accesses;
    }

    @Nullable
    public String getCommodities() {
        return commodities;
    }
}
