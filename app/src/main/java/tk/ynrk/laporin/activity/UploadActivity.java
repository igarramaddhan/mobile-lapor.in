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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import tk.ynrk.laporin.BuildConfig;
import tk.ynrk.laporin.R;
import tk.ynrk.laporin.controller.CameraController;
import tk.ynrk.laporin.helper.Code;
import tk.ynrk.laporin.helper.PermissionHelper;
import tk.ynrk.laporin.object.CustomBottomSheetDialog;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private Snackbar snackbar;
    private ImageView imageView;
    private CardView imageContainer;
    private Button button;
    private LinearLayout gallery, camera;

    private Bitmap bitmap;

    private PermissionHelper permissionHelper;
    private CameraController cameraController;
    private CustomBottomSheetDialog customBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.upload_image);
        coordinatorLayout = findViewById(R.id.upload_coor_layout);
        imageContainer = findViewById(R.id.upload_image_container);


        toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customBottomSheetDialog = new CustomBottomSheetDialog();

        button = findViewById(R.id.upload_camera);
        button.setOnClickListener(this);

        cameraController = new CameraController(getApplicationContext());
        permissionHelper = new PermissionHelper(this,getApplicationContext());
        permissionHelper.checkPermissionAll();

        camera = findViewById(R.id.pick_camera);
        camera.setOnClickListener(this);

        gallery = findViewById(R.id.pick_photo);
        gallery.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case Code.FILE:
                    try {
                        if (bitmap != null) {
                            bitmap.recycle();
                        }

                        Log.d("SET PICTURE", "success");
                        stream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(stream);

                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        Log.d("SET PICTURE", "failed");
                        e.printStackTrace();
                    } finally {
                        if (stream != null){
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;

                case Code.CAMERA:
                    imageView.setImageURI(Uri.parse(cameraController.getCurrentPhotoPath()));
                    imageView.setVisibility(View.VISIBLE);
                    imageView.animate().alpha(1).setStartDelay(100);
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
            case R.id.pick_camera:
                Log.d("PICK CAMERA", "onClick: ");
                if(!permissionHelper.checkPermission(Code.CAMERA) || !permissionHelper.checkPermission(Code.WRITE)){
                    permissionHelper.checkPermissionAll();
                } else {
                    startActivityForResult(cameraController.getCameraIntent(), Code.CAMERA);
                }
                break;
            case R.id.upload_camera:
                customBottomSheetDialog.show(getSupportFragmentManager(), "BOTTOM_SHEET");
                break;
            case R.id.pick_photo:
                if(!permissionHelper.checkPermission(Code.WRITE)){
                    permissionHelper.checkPermission(Code.WRITE);
                }else {
                    startActivityForResult(cameraController.getPhotoIntent(), Code.FILE);
                }
                break;
            default:
                break;
        }
    }
}
