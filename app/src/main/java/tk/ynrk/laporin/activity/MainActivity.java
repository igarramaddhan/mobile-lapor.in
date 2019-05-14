package tk.ynrk.laporin.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.design.button.MaterialButton;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.helper.AppPreferences;
import tk.ynrk.laporin.object.Laporan;
import tk.ynrk.laporin.helper.NetworkJSONLoader;
import tk.ynrk.laporin.object.LaporanAdapter;

public class MainActivity extends AppCompatActivity implements NetworkJSONLoader.NetworkJSONLoaderContract, AdapterView.OnItemSelectedListener {
    private final String tag = "MAIN_ACTIVITY";
    Snackbar snackbar;
    AppPreferences prefs;
    NetworkJSONLoader client;
    Gson gson;
    private String BASE_URL;
    private String token;
    private ArrayList<Laporan> laporan;
    private LaporanAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout greetContainer;
    private MaterialButton button;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BASE_URL = getResources().getString(R.string.BASE_URL);
        prefs = new AppPreferences(getApplicationContext());
        client = new NetworkJSONLoader(this, MainActivity.this);
        gson = new Gson();
        laporan = new ArrayList<>();

        recyclerView = findViewById(R.id.main_recyclerview);
        coordinatorLayout = findViewById(R.id.main_coor_layout);
        floatingActionButton = findViewById(R.id.main_fab);
        swipeRefreshLayout = findViewById(R.id.main_swipe_container);
        greetContainer = findViewById(R.id.main_greeting_container);
        button = findViewById(R.id.main_button);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        layoutManager = new LinearLayoutManager(MainActivity.this);
        adapter = new LaporanAdapter(laporan, getApplicationContext(), this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        token = prefs.getValue("token");
        client.get(BASE_URL + "/laporan", token);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String filter = spinner.getSelectedItem().toString();
                client.get(BASE_URL + "/laporan" + (filter.equals("All") ? "" : ("/laporan?district=" + filter)), token);
            }
        });

        spinner = findViewById(R.id.spinner_nav);
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this,
                R.array.district_array, android.R.layout.simple_spinner_item);

        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        spinner.setOnItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.draft) {
            Snackbar sbDraft = Snackbar.make(coordinatorLayout, "This is draft menu", Snackbar.LENGTH_SHORT);
            sbDraft.show();
            return true;
        }

        if (id == R.id.logout) {
            return logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskLoading() {
    }


    @Override
    public void onTaskCompleted(JSONObject response) {
        try {
            JSONArray laporanJson = response.getJSONArray("laporan");
            laporan = gson.fromJson(laporanJson.toString(), new TypeToken<ArrayList<Laporan>>() {
            }.getType());
            setView();
            adapter.clear();
            adapter.addAll(laporan);
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        } catch (JSONException err) {
            Log.e(tag, err.toString());
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onTaskError(VolleyError error) {
        try {
            if (error.networkResponse == null) {
                if (error.getClass().equals(TimeoutError.class)) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar sbRetry = Snackbar.make(coordinatorLayout, "Gagal memuat laporan", Snackbar.LENGTH_SHORT);
                    sbRetry.setAction("COBA LAGI", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            client.get(BASE_URL + "/laporan", token);
                        }
                    });
                    sbRetry.show();
                }
            } else {
                checkValidSession(error.networkResponse.statusCode);
            }
        } catch (Exception e) {
            Log.d("Task error", "status_code: " + String.valueOf(error.networkResponse));
        }
    }

    private Boolean checkValidSession(int statusCode) {
        if (statusCode == 500) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    private Boolean logout() {
        prefs.clear();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void setView() {
        if (laporan.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            greetContainer.setVisibility(View.VISIBLE);
            floatingActionButton.hide();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            greetContainer.setVisibility(View.GONE);
            floatingActionButton.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String[] dArray = getResources().getStringArray(R.array.district_array);
        String filter = dArray[i];
        if (filter.equals("All")) {
            client.get(BASE_URL + "/laporan", token);
        } else {
            client.get(BASE_URL + "/laporan?district=" + filter, token);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
