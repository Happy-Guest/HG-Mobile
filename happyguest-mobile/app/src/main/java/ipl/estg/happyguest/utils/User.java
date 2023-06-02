package ipl.estg.happyguest.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

    public String getPhotoUrl() {
        return sharedPreferences.getString("photo_url", null);
    }

    public void setUser(int id, String name, String email, Long phone, String photoUrl) {
        editor.putInt("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        if (phone != null) editor.putLong("phone", phone);
        editor.putString("photo_url", photoUrl);
        editor.commit();
    }

    public void clearUser() {
        editor.clear();
        editor.commit();
    }
}
