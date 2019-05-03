package tk.ynrk.laporin.object;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.controller.CameraController;
import tk.ynrk.laporin.helper.Code;
import tk.ynrk.laporin.helper.PermissionHelper;

public class CustomBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetBehavior.BottomSheetCallback callback;
    private LinearLayout pick_camera, pick_photo;

    private CameraController cameraController;
    private PermissionHelper permissionHelper;

    private Bitmap bitmap;
    private ImageView imageView;

    public CustomBottomSheetDialog(){
        callback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        pick_camera = view.findViewById(R.id.pick_camera);
        pick_photo = view.findViewById(R.id.pick_photo);
        permissionHelper = new PermissionHelper(getActivity(), getContext());
        cameraController = new CameraController(getContext());
        pick_camera.setOnClickListener(this);
        pick_photo.setOnClickListener(this);
        return view;
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
            case R.id.pick_photo:
                if(!permissionHelper.checkPermission(Code.WRITE)){
                    permissionHelper.checkPermission(Code.WRITE);
                }else {
                    startActivityForResult(cameraController.getPhotoIntent(), Code.FILE);
                }
                break;
        }
    }

}
