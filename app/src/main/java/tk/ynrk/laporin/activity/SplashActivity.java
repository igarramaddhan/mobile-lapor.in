package tk.ynrk.laporin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.helper.AppPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppPreferences prefs = new AppPreferences(getApplicationContext());

        Log.d("SPLASH_ACT","="+ prefs.getValue("token"));

        if(prefs.getValue("token") != ""){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
