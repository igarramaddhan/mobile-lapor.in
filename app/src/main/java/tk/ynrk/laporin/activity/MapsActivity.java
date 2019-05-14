package tk.ynrk.laporin.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.controller.LocationController;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private float zoom = 13.0f;
    private Toolbar toolbar;
    private TextView coordinate;
    private EditText location;
    private String latt = "" , longt = "";
    private String address = "";

    private LocationController locationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        statusGps();

        locationController = new LocationController(getApplicationContext(), this);

        coordinate = findViewById(R.id.upload_map);
        location = findViewById(R.id.location_text);

        toolbar = findViewById(R.id.map_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                    Log.d("ON MY LICATION", "TRUE");
                    onMapClick(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()));
                }
            }
        });
        coordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latt.equals("")){
                    Toast.makeText(MapsActivity.this, "Anda Belum Memasukkan Koordinat", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapsActivity.this, "Anda Sudah memasukkan koordinat", Toast.LENGTH_SHORT).show();
                    Intent intentResult = new Intent();

                    intentResult.putExtra("latitude",latt);
                    intentResult.putExtra("longitude",longt);
                    intentResult.putExtra("address",address);
                    setResult(Activity.RESULT_OK, intentResult);
                    finish();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocationPermission()){
            mMap.setMyLocationEnabled(true);
        }

        LatLng malang = new LatLng(-7.983908, 112.621391);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(malang)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   //
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMapClickListener(this);
    }

    public boolean checkLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED	)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Di Butuhkan")
                        .setMessage("Untuk bisa Membuka")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},99);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},99);
            }
            return false;
        }
        else {
            return true;
        }
    }

    private void statusGps() {
        final LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getApplicationContext(), "GPS Anda Mati, Silhkan dihidupkan ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this,"sip",Toast.LENGTH_LONG);
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                }
                return;
            }
        }
    }


    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Lokasi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        latt = point.latitude+"";
        longt = point.longitude +"";

        address = locationController.getAddress(Float.parseFloat(latt), Float.parseFloat(longt));
//        location.setText(latt +" ; "+longt);
        location.setText(address);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}