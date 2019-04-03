package tk.ynrk.laporin.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getValue(String key) {
        return _sharedPrefs.getString(key, "");
    }

    public void setValue(String key, String value) {
        _prefsEditor.putString(key, value);
        _prefsEditor.commit();
    }

    public void clear(){
        _prefsEditor.clear();
        _prefsEditor.commit();
    }
}