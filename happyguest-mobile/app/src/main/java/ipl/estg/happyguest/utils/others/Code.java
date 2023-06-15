package ipl.estg.happyguest.utils.others;

import android.content.Context;
import android.content.SharedPreferences;

public class Code {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public Code(Context context){
        sharedPreferences = context.getSharedPreferences("hg-code", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setHasCode(Boolean hasCode){
        editor.putBoolean("hasCode", hasCode);
        editor.commit();
    }

    public Boolean getHasCode(){
        return sharedPreferences.getBoolean("hasCode", false);
    }

    public void clearCode(){
        editor.clear();
        editor.commit();
    }
}
