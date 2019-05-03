package tk.ynrk.laporin.object;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.TransitionRes;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.controller.LocationController;
import tk.ynrk.laporin.helper.NetworkImageLoader;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder> {

    private ArrayList<Laporan> laporans;
    private Context context;
    private LocationController locationController;

    public LaporanAdapter(ArrayList<Laporan> laporans, Context context, Activity activity) {
        this.laporans = laporans;
        this.context = context;
        locationController = new LocationController(context, activity);
    }

    public void clear() {
        laporans.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Laporan> array_laporan) {
        this.laporans.addAll(array_laporan);
        notifyDataSetChanged();
    }

     public String getColorStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending" :
                return "#2b2b2b";
            case "diproses":
                return "#2b2b2b";
            case "terverifikasi":
                Log.d("ADAPTER", "Color Accent");
                return "#29B9BE";
            case "ditolak":
                return "#FF5050";
            default:
                return "#FF5050";
        }
    }

    @NonNull
    @Override
    public LaporanViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.layout_laporan, viewGroup, false);
        final LaporanViewHolder holder = new LaporanViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final boolean visible = holder.descriprion.getVisibility() == View.VISIBLE;

                if(!visible) {
                    holder.itemView.setActivated(!visible);
                    holder.descriprion.setVisibility(View.VISIBLE);
                    holder.location.setMaxLines(2);
                    holder.descriprion.animate().alpha(1).setStartDelay(100);
                }else {
                    holder.itemView.setActivated(!visible);
                    holder.descriprion.setVisibility(View.GONE);
                    holder.location.setMaxLines(1);
                    holder.descriprion.setAlpha(0);
                }

                TransitionManager.beginDelayedTransition(viewGroup);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LaporanViewHolder laporanViewholder, final int i) {
        final Laporan laporan = laporans.get(i);
        String imageUrl = "http://" + laporan.image;
        String description = laporan.description;
        String status = laporans.get(i).status;
        String address = locationController.getAddress(laporan.latitude, laporan.longitude);
        String city = locationController.getCity(laporan.latitude, laporan.longitude);

        laporanViewholder.client.get(imageUrl);
        laporanViewholder.location.setText(address);
        laporanViewholder.city.setText(city);
        laporanViewholder.descriprion.setText(description);
        laporanViewholder.status.setText(status);
        laporanViewholder.status.setTextColor(Color.parseColor(getColorStatus(status)));

        laporanViewholder.descriprion.setVisibility(View.GONE);
        laporanViewholder.location.setMaxLines(1);
    }

    @Override
    public int getItemCount() {
        return laporans.size();
    }

    public class LaporanViewHolder extends RecyclerView.ViewHolder  implements NetworkImageLoader.NetworkImageLoaderContract {
        private TextView descriprion, status, location, city;
        private ImageView image;

        NetworkImageLoader client;

        public LaporanViewHolder(View itemView) {
            super(itemView);
            descriprion = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.listImage);
            city = itemView.findViewById(R.id.city);

            client = new NetworkImageLoader(context, this);
        }

        @Override
        public void onTaskLoading() {

        }

        @Override
        public void onTaskCompleted(Bitmap response) {
            image.setImageBitmap(response);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        @Override
        public void onTaskError(VolleyError error) {
            error.printStackTrace();
            Log.d("ADAPTER", "Set Image failed");
        }
    }
    
}


