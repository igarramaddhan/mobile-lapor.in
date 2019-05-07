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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.helper.Code;

public class CustomBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetBehavior.BottomSheetCallback callback;
    private LinearLayout pick_camera, pick_photo;

    private BottomSheetContract listener;

    public CustomBottomSheet(){
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        listener = (BottomSheetContract) getActivity();
        pick_camera = view.findViewById(R.id.pick_camera);
        pick_photo = view.findViewById(R.id.pick_photo);
        pick_camera.setOnClickListener(this);
        pick_photo.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pick_camera:
                listener.createRequest(Code.CAMERA);
                this.dismiss();
                break;
            case R.id.pick_photo:
                listener.createRequest(Code.FILE);
                this.dismiss();
                break;
        }
    }


    public interface BottomSheetContract{
        void createRequest(int code);
    }
}
