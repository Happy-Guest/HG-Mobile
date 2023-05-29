package ipl.estg.happyguest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Token {

    String token;
    Boolean remember;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public Token(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("happyguest", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.remember = false;
    }

    public void setToken(String token){
        this.token = token;
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString("token", "");
    }

    public void setRemember(Boolean remember){
        this.remember = remember;
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
