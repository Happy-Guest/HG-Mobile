package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("id")
    public final Long id;
    @SerializedName("name")
    public final String name;
    @SerializedName("nameEN")
    public final String nameEN;
    @Nullable
    @SerializedName("email")
    public final String email;
    @Nullable
    @SerializedName("phone")
    public final Long phone;
    @SerializedName("type")
    public final char type;
    @SerializedName("schedule")
    public final String schedule;
    @SerializedName("occupation")
    @Nullable
    public final Long occupation;
    @SerializedName("location")
    @Nullable
    public final String location;
    @SerializedName("limit")
    @Nullable
    public final Long limit;
    @SerializedName("description")
    public final String description;
    @SerializedName("descriptionEN")
    public final String descriptionEN; //TODO: ITEMS
    @Nullable
    @SerializedName("menu_url")
    public final String menu_url;
    @SerializedName("active")
    public final int active;

    public Service(Long id, String name, String nameEN, @Nullable String email, @Nullable Long phone, char type, String schedule, @Nullable Long occupation, @Nullable String location, @Nullable Long limit, String description, String descriptionEN, @Nullable String menu_url, int active) {
        this.id = id;
        this.name = name;
        this.nameEN = nameEN;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.schedule = schedule;
        this.occupation = occupation;
        this.location = location;
        this.limit = limit;
        this.description = description;
        this.descriptionEN = descriptionEN;
        this.menu_url = menu_url;
        this.active = active;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public Long getPhone() {
        return phone;
    }

    public char getType() {
        return type;
    }

    public String getSchedule() {
        return schedule;
    }

    @Nullable
    public Long getOccupation() {
        return occupation;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    @Nullable
    public Long getLimit() {
        return limit;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    @Nullable
    public String getMenu_url() {
        return menu_url;
    }

    public boolean isActive() {
        return active == 1;
    }
}