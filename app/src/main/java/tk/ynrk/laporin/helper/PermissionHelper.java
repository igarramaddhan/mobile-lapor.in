package tk.ynrk.laporin.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionHelper {
    private static final String TAG = "PERMISSION_HELPER";
    private Activity activity;
    private Context context;

    public PermissionHelper(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public void checkPermission(){
        Log.d(TAG, "checkPermission: Checking");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Code.WRITE);
                Log.d(TAG, "checkPermission: Requested");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.d(TAG, "checkPermission: Requested");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Code.WRITE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        else if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Code.CAMERA);
                Log.d(TAG, "checkPermission: Requested");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Code.CAMERA);
                Log.d(TAG, "checkPermission: Requested");
            }
        } else {
            // Permission has already been granted
            Log.d(TAG, "checkPermission: All Granted");
        }
    }

    public void checkPermissionAll(){
        Log.d(TAG, "checkPermission: Checking");
        // Here, thisActivity is the current activity
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Code.ALL);
                Log.d(TAG, "checkPermission: Requested");
            } else {

                Log.d(TAG, "checkPermission: Requested");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Code.ALL);
            }
        }
        else {
            Log.d(TAG, "checkPermission: All Granted");
        }
    }

    public boolean checkPermission(int REQUEST_CODE){
        Log.d(TAG, "checkPermission: Checking");
        // Here, thisActivity is the current activity

        switch (REQUEST_CODE) {
            case Code.WRITE:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                   return false;
                } else {
                    Log.d(TAG, "checkPermission: Granted");
                    return true;
                }

            case Code.CAMERA:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    // Permission has already been granted
                    Log.d(TAG, "checkPermission: Granted");
                    return true;
                }

            case Code.MAP:
                if(ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    return false;
                }else {
                    Log.d(TAG, "checkPermission: Granted");
                    return true;
                }

            default:
                return false;
        }
    }

}
