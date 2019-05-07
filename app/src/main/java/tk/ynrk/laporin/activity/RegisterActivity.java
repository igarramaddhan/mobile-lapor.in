package tk.ynrk.laporin.activity;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.helper.AppPreferences;
import tk.ynrk.laporin.helper.NetworkJSONLoader;

public class RegisterActivity extends AppCompatActivity implements NetworkJSONLoader.NetworkJSONLoaderContract {

    private String BASE_URL;
    private String tag;

    private TextView appTitle, appDetail, warning;
    private EditText email, password, repassword, fullname;
    private Button registerButton;
    private LinearLayout redirect;

    private AppPreferences prefs;

    private NetworkJSONLoader client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        appTitle = findViewById(R.id.app_title);
        appDetail = findViewById(R.id.app_detail);
        email = findViewById(R.id.register_email_et);
        password = findViewById(R.id.register_password_et);
        repassword = findViewById(R.id.register_password_retype_et);
        fullname = findViewById(R.id.fullname);
        registerButton = findViewById(R.id.register_button);
        redirect = findViewById(R.id.redirect_to_login);

        prefs = new AppPreferences(getApplicationContext());
        client = new NetworkJSONLoader(this, RegisterActivity.this);

        BASE_URL = getResources().getString(R.string.BASE_URL);
        tag = getResources().getString(R.string.tag_register);

        findViewById(R.id.register_parent).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        findViewById(R.id.login_navigate_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Pair<View, String> p1 = Pair.create((View) appTitle, appTitle.getTransitionName());
                Pair<View, String> p2 = Pair.create((View) appDetail, appDetail.getTransitionName());
                Pair<View, String> p3 = Pair.create((View) email, email.getTransitionName());
                Pair<View, String> p4 = Pair.create((View) password, password.getTransitionName());
                Pair<View, String> p5 = Pair.create((View) registerButton, registerButton.getTransitionName());
                Pair<View, String> p6 = Pair.create((View) redirect, redirect.getTransitionName());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(RegisterActivity.this, p1, p2, p3, p4, p5, p6);
                startActivity(intent, options.toBundle());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForm()) {
                    return;
                }
                hideKeyboard(v);
                String token = prefs.getValue("token");
                Log.d(tag, "token " + token);
                if (token != null || !token.isEmpty()) {
                    prefs.clear();
                }
                Map<String, Object> data = new HashMap<>();
                data.put("email", email.getText().toString());
                data.put("password", password.getText().toString());
                data.put("fullname", fullname.getText().toString());
                client.post(BASE_URL + "/register", data);
            }
        });
    }

    public boolean checkForm() {
        if(email.getText().toString().isEmpty()
                || password.getText().toString().isEmpty()
                || repassword.getText().toString().isEmpty()
                || fullname.getText().toString().isEmpty()){
            Toast.makeText(this, "Form pendaftaran tidak boleh kosong", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onTaskLoading() {
        Toast.makeText(this, "LOADING", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskCompleted(JSONObject response) {
        try {
            String token = response.getString("token");
            Toast.makeText(this, "DONE, REGISTERED", Toast.LENGTH_SHORT).show();
            Log.d(tag, token);
            prefs.setValue("token", token);

            // Navigate to main screen after registering
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException err) {
            Log.e(tag, err.toString());
        }
    }

    @Override
    public void onTaskError(VolleyError error) {
        String errorString = new String(error.networkResponse.data);
        Log.e(tag, "" + errorString);
        Toast.makeText(this, "ERROR: "+errorString, Toast.LENGTH_SHORT).show();
    }
}
