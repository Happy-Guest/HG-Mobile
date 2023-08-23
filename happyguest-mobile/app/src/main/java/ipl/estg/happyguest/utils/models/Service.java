package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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

    @Nullable
    @SerializedName("occupation")
    public final Long occupation;

    @Nullable
    @SerializedName("limit")
    public final Long limit;

    @Nullable
    @SerializedName("location")
    public final String location;

    @SerializedName("description")
    public final String description;

    @SerializedName("descriptionEN")
    public final String descriptionEN;

    @Nullable
    @SerializedName("items")
    public final ArrayList<Item> items;

    @Nullable
    @SerializedName("menu_url")
    public final String menu_url;

    @SerializedName("active")
    public final int active;

    public Service(Long id, String name, String nameEN, @Nullable String email, @Nullable Long phone, char type, String schedule, @Nullable Long occupation, @Nullable String location, String description, String descriptionEN, @Nullable ArrayList<Item> items, @Nullable String menu_url, int active, @Nullable Long limit) {
        this.id = id;
        this.name = name;
        this.nameEN = nameEN;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.schedule = schedule;
        this.occupation = occupation;
        this.location = location;
        this.description = description;
        this.descriptionEN = descriptionEN;
        this.items = items;
        this.menu_url = menu_url;
        this.active = active;
        this.limit = limit;
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
    public Long getLimit() {
        return limit;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    @Nullable
    public ArrayList<Item> getItems() {
        return items;
    }

    @Nullable
    public String getMenu_url() {
        return menu_url;
    }

    public boolean isActive() {
        return active == 1;
    }
}