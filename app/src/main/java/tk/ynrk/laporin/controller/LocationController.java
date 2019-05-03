package tk.ynrk.laporin.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationController {

    private FusedLocationProviderClient client;
    private LocationCallback callback;
    private LocationRequest request;
    private Geofence geofence;
    private Geocoder geocoder;
    private Context context;
    private Activity activity;

    private boolean permission = false;
    private List<Address> addresses;

    public LocationController() {

    }

    public LocationController(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        setupLocation();
    }

    private void setupLocation() {
        geocoder = new Geocoder(context, Locale.getDefault());
        client = LocationServices.getFusedLocationProviderClient(context);
        request = LocationRequest.create();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public String getAddress(float Lat, float Long) {
        try {
            addresses = geocoder.getFromLocation(Lat, Long, 1);
        } catch (IOException err) {
            err.printStackTrace();
            Log.d("LOCATION_CONTROLLER", err.getMessage());
        }

        if (addresses != null) {
            return addresses.get(0).getAddressLine(0);
        } else {
            return "Undefined";
        }
    }

    public String getCity(float Lat, float Long){
        try {
            addresses = geocoder.getFromLocation(Lat, Long, 1);
        } catch (IOException err) {
            err.printStackTrace();
            Log.d("LOCATION_CONTROLLER", err.getMessage());
        }

        if (addresses != null) {
            return addresses.get(0).getAdminArea();
        } else {
            return "Undefined";
        }
    }

}
