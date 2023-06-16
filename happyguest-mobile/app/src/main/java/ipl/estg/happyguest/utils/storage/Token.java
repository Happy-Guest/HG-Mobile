package ipl.estg.happyguest.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Token {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public Token(Context context){
        sharedPreferences = context.getSharedPreferences("hg-token", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setToken(String token){
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString("token", null);
    }

    public void setRemember(Boolean remember){
        editor.putBoolean("remember", remember);
        editor.commit();
    }

    public Boolean getRemember(){
        return sharedPreferences.getBoolean("remember", false);
    }

    public void clearToken(){
        editor.clear();
        editor.commit();
    }
}
