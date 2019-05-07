package tk.ynrk.laporin.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tk.ynrk.laporin.R;
import tk.ynrk.laporin.object.Laporan;
import tk.ynrk.laporin.object.LaporanAdapter;

public class NetworkImageLoader {
    Context context;
    NetworkImageLoaderContract listener;

    public NetworkImageLoader(Context context, NetworkImageLoaderContract listener) {
        this.context = context;
        this.listener = listener;
    }

    public void get(String url) {
        // Tag used to cancel the request
        final String tag_image_obj = "IMAGE_GET";

        listener.onTaskLoading();

        ImageRequest imageObjReq = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        listener.onTaskCompleted(response);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onTaskError(error);
            }
        });

        VolleySingleton.getInstance().addToRequestQueue(imageObjReq, tag_image_obj);
    }

    public interface NetworkImageLoaderContract {
        void onTaskLoading();

        void onTaskCompleted(Bitmap response);

        void onTaskError(VolleyError error);
    }
}
