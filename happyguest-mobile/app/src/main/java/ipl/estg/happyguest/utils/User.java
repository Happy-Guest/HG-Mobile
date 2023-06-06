package ipl.estg.happyguest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class User {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int getId() {
        return sharedPreferences.getInt("id", -1);
    }

    public String getName() {
        return sharedPreferences.getString("name", null);
    }

    public String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    public Long getPhone() {
        return sharedPreferences.getLong("phone", -1);
    }

    public String getAddress() {
        return sharedPreferences.getString("address", null);
    }

    public String getBirthDate() {
        return new String(sharedPreferences.getString("birth_date", null));
    }

    public String getPhotoUrl() {
        return sharedPreferences.getString("photo_url", null);
    }

    public void setUser(int id, String name, String email, Long phone, String address, Date birthDate, String photoUrl) {
        editor.putInt("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        if (phone != null) editor.putLong("phone", phone);
        if (address != null) editor.putString("address", address);
        if (birthDate != null) editor.putString("birth_date", birthDate.toString());
        if (photoUrl != null) editor.putString("photo_url", photoUrl);
        editor.commit();
    }

    public void clearUser() {
        editor.clear();
        editor.commit();
    }
}
