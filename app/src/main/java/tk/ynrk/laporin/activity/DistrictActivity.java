package tk.ynrk.laporin.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.object.DistrictAdapter;
import tk.ynrk.laporin.object.LaporanAdapter;

public class DistrictActivity extends AppCompatActivity {
    private DistrictAdapter districtAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);

        recyclerView = findViewById(R.id.district_recyclerview);
        layoutManager = new LinearLayoutManager(DistrictActivity.this);
        districtAdapter = new DistrictAdapter(getApplicationContext(), new DistrictAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String district) {
                Intent intentResult = new Intent();

                intentResult.putExtra("district",district);
                setResult(Activity.RESULT_OK, intentResult);
                finish();
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(districtAdapter);
        recyclerView.setHasFixedSize(true);
    }
}
