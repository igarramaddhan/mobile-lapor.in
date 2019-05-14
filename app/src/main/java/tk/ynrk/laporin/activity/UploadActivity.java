package tk.ynrk.laporin.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tk.ynrk.laporin.BuildConfig;
import tk.ynrk.laporin.R;
import tk.ynrk.laporin.controller.CameraController;
import tk.ynrk.laporin.helper.AppPreferences;
import tk.ynrk.laporin.helper.Code;
import tk.ynrk.laporin.helper.NetworkJSONLoader;
import tk.ynrk.laporin.helper.PermissionHelper;
import tk.ynrk.laporin.helper.VolleyMultipartRequest;
import tk.ynrk.laporin.helper.VolleySingleton;
import tk.ynrk.laporin.object.CustomBottomSheet;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener, CustomBottomSheet.BottomSheetContract, NetworkJSONLoader.NetworkJSONLoaderContract {

    private final String TAG = "UPLOAD_ACTIVITY";
    private CoordinatorLayout coordinatorLayout;
    private View bottomSheet;
    private BottomSheetBehavior behavior;
    private Toolbar toolbar;
    private Snackbar snackbar;
    private ImageView ivBukti;
    private CardView imageContainer;
    private Button button;
    private EditText etDeskripsi, etMap, etDistrict;

    private Bitmap bitmap;

    private PermissionHelper permissionHelper;
    private CameraController cameraController;
    private CustomBottomSheet customBottomSheet;

    private NetworkJSONLoader client;

    private AppPreferences prefs;

    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        ivBukti = findViewById(R.id.upload_image);
        etDeskripsi = findViewById(R.id.upload_description);
        coordinatorLayout = findViewById(R.id.upload_coor_layout);
        imageContainer = findViewById(R.id.upload_image_container);
        button = findViewById(R.id.upload_camera);
        button.setOnClickListener(this);

        toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customBottomSheet = new CustomBottomSheet();

        cameraController = new CameraController(getApplicationContext());
        permissionHelper = new PermissionHelper(this,getApplicationContext());
        permissionHelper.checkPermissionAll();

        prefs = new AppPreferences(getApplicationContext());
        client = new NetworkJSONLoader(getApplicationContext(), this);

        etMap = findViewById(R.id.upload_location);
        etMap.setOnClickListener(this);

        etDistrict = findViewById(R.id.upload_district);
        etDistrict.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        Log.d(TAG, "code: "+requestCode);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case Code.FILE:
                    try {
                        if (bitmap != null) {
                            bitmap.recycle();
                        }

                        Log.d(TAG, "success");
                        stream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(stream);

                        ivBukti.setImageBitmap(bitmap);
                        ivBukti.setVisibility(View.VISIBLE);
                        button.setText("UPLOAD");
                    } catch (FileNotFoundException e) {
                        Log.d(TAG, "failed");
                        e.printStackTrace();
                    }
                    break;

                case Code.CAMERA:
                    try {
                        Log.d(TAG, "Path: " + cameraController.getCurrentPhotoPath());
                        Uri filePath = Uri.parse(cameraController.getCurrentPhotoPath());
                        bitmap = BitmapFactory.decodeFile(cameraController.getCurrentPhotoPath());
                        ivBukti.setImageBitmap(bitmap);
                        ivBukti.setVisibility(View.VISIBLE);
                        ivBukti.animate().alpha(1).setStartDelay(100);
                        button.setText("UPLOAD");
                    }catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case Code.MAP:
                    latitude = data.getStringExtra("latitude");
                    longitude = data.getStringExtra("longitude");
                    String address = data.getStringExtra("address");
                    Toast.makeText(this, "Retrieve data", Toast.LENGTH_SHORT).show();

                    etMap.setText(address);

                case Code.DISTRICT:
                    String district = data.getStringExtra("district");
                    etDistrict.setText(district);
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Code.WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(coordinatorLayout, "Granted!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Denied!", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
            case Code.CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(coordinatorLayout, "Granted!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Camera Permission Denied!", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
            case Code.ALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(coordinatorLayout, "Granted!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(coordinatorLayout, "Camera Permission Denied!", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.save_to_draft){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload_camera:
                if(etDeskripsi.getText().toString() != "" && bitmap != null){
                    Snackbar.make(coordinatorLayout, "Upload", Snackbar.LENGTH_SHORT).show();
                    String BASE_URL = getResources().getString(R.string.BASE_URL);

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "bearer " + prefs.getValue("token"));
                    VolleyMultipartRequest request = new VolleyMultipartRequest( BASE_URL + "/laporan/create", headers, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse networkResponse) {
                            String resultResponse = new String(networkResponse.data);
                            Log.d("UPLOAD_REQUEST", resultResponse);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            NetworkResponse networkResponse = volleyError.networkResponse;
                            String errorMessage = "Unknown error";

                            Log.e("UPLOAD_ERROR", "" + networkResponse.statusCode);
                            String resultResponse = new String(networkResponse.data);
                            Log.e("UPLOAD_ERROR", "" + resultResponse);
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {

                            Map<String, String> data = new HashMap<>();
                            data.put("description", etDeskripsi.getText().toString());
                            data.put("longitude", longitude);
                            data.put("latitude", latitude);
                            data.put("district", etDistrict.getText().toString());
                            return data;
                        }

                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            // file name could found file base or direct access from real path
                            // for now just get bitmap data from ImageView
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            params.put("image", new DataPart("file_avatar.png", byteArray, "image/png"));

                            return params;
                        }
                    };
                    VolleySingleton.getInstance().addToRequestQueue(request);
                }else {
                    customBottomSheet.show(getSupportFragmentManager(), "BOTTOM_SHEET");
                }
                break;
            case R.id.upload_location:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, Code.MAP);
                break;
            case R.id.upload_district:
                Intent dstrIntent = new Intent(getApplicationContext(), DistrictActivity.class);
                startActivityForResult(dstrIntent, Code.DISTRICT);
                break;
            default:
                break;
        }
    }

    @Override
    public void createRequest(int code) {
        switch (code) {
            case Code.FILE:
                if(!permissionHelper.checkPermission(Code.WRITE)){
                    permissionHelper.checkPermissionAll();
                } else {
                    Log.d(TAG, "code: "+Code.CAMERA);
                    startActivityForResult(cameraController.getPhotoIntent(), Code.FILE);
                }
                break;

            case Code.CAMERA:
                if(!permissionHelper.checkPermission(Code.CAMERA) || !permissionHelper.checkPermission(Code.WRITE)){
                    permissionHelper.checkPermissionAll();
                } else {
                    Log.d(TAG, "code: "+Code.CAMERA);
                    startActivityForResult(cameraController.getCameraIntent(), Code.CAMERA);
                }
                break;
        }
    }

    @Override
    public void onTaskLoading() {

    }

    @Override
    public void onTaskCompleted(JSONObject response) {
        Snackbar.make(coordinatorLayout, "Upload Success!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskError(VolleyError error) {
        Snackbar.make(coordinatorLayout, "Upload Failed!", Snackbar.LENGTH_SHORT).show();
        Log.d(TAG, "onTaskError: "+ error.getMessage());
    }
}
