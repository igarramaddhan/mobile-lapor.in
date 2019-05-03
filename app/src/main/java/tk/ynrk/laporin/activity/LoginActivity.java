package tk.ynrk.laporin.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

public class LoginActivity extends AppCompatActivity implements NetworkJSONLoader.NetworkJSONLoaderContract {
    String BASE_URL;

    NetworkJSONLoader client;
    private TextView appTitle, appDetail, warning;
    private EditText email, password;
    private Button loginButton;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout redirect;

    private AppPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appTitle = findViewById(R.id.app_title);
        appDetail = findViewById(R.id.app_detail);
        email = findViewById(R.id.login_email_et);
        password = findViewById(R.id.login_password_et);
        loginButton = findViewById(R.id.login_button);
        redirect = findViewById(R.id.redirect_to_register);

        BASE_URL = getResources().getString(R.string.BASE_URL);
        prefs = new AppPreferences(getApplicationContext());

        client = new NetworkJSONLoader(this, LoginActivity.this);

        findViewById(R.id.login_parent).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        findViewById(R.id.register_navigate_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                Pair<View, String> p1 = Pair.create((View) appTitle, appTitle.getTransitionName());
                Pair<View, String> p2 = Pair.create((View) appDetail, appDetail.getTransitionName());
                Pair<View, String> p3 = Pair.create((View) email, email.getTransitionName());
                Pair<View, String> p4 = Pair.create((View) password, password.getTransitionName());
                Pair<View, String> p5 = Pair.create((View) loginButton, loginButton.getTransitionName());
                Pair<View, String> p6 = Pair.create((View) redirect, redirect.getTransitionName());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(LoginActivity.this, p1, p2, p3, p4, p5, p6);
                startActivity(intent, options.toBundle());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForm()){
                    return;
                }

                hideKeyboard(view);
                String token = prefs.getValue("token");
                Log.d("LOGIN_ACTIVITY", "token " + token);

                if (token != null || !token.isEmpty()) {
                    prefs.clear();
                }

                Map<String, Object> data = new HashMap<>();
                data.put("email", email.getText().toString());
                data.put("password", password.getText().toString());
                client.post(BASE_URL + "/login", data);
            }
        });

    }

    public boolean checkForm() {
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show();
            Log.d("LOGIN_ACTIVITY", token);
            prefs.setValue("token", token);

            //Navigate to main screen after login in
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException err) {
            Log.e("LOGIN_ACTIVITY", err.toString());
        }
    }

    @Override
    public void onTaskError(VolleyError error) {
        Log.e("LOGIN_ACTIVITY", "" + "err");
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        Snackbar.make(findViewById(R.id.login_parent), "Login failed please try again", Snackbar.LENGTH_SHORT);
    }
}
