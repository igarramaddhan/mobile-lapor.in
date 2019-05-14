package tk.ynrk.laporin.object;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.helper.NetworkImageLoader;

public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder> {

    private final OnItemClickListener listener;
    private ArrayList<String> districts = new ArrayList<String>();
    private Context context;

    public DistrictAdapter(Context context, OnItemClickListener listener) {
        String[] dArray = context.getResources().getStringArray(R.array.district_array);
        for (String d : dArray) {
            if (!d.equals("All"))
                this.districts.add(d);
        }
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DistrictViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.layout_district, viewGroup, false);
        final DistrictViewHolder holder = new DistrictViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DistrictViewHolder districtViewHolder, int i) {
        final String district = districts.get(i);
        Log.d("DISTRICT", district);
        districtViewHolder.bind(district, listener);
    }

    @Override
    public int getItemCount() {
        return districts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String district);
    }

    public class DistrictViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public DistrictViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.district);
        }

        public void bind(final String district, final OnItemClickListener listener) {
            text.setText(district);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(district);
                }
            });
        }

    }
}
