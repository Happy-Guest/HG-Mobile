package ipl.estg.happyguest.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class User {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences("hg-user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Long getId() {
        return sharedPreferences.getLong("id", -1);
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
        return sharedPreferences.getString("birth_date", null);
    }

    public String getPhotoUrl() {
        return sharedPreferences.getString("photo_url", null);
    }

    public String getLastReview() {
        return sharedPreferences.getString("last_review", null);
    }

    public void setUser(Long id, String name, String email, Long phone, String address, String birthDate, String photoUrl, String lastReview) {
        editor.putLong("id", id);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putLong("phone", phone);
        editor.putString("address", address);
        editor.putString("birth_date", birthDate);
        editor.putString("photo_url", photoUrl);
        editor.putString("last_review", lastReview);
        editor.commit();
    }

    public void clearUser() {
        editor.clear();
        editor.commit();
    }
}
