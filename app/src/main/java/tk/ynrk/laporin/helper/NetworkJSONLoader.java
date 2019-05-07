package tk.ynrk.laporin.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class NetworkJSONLoader {
    Context context;
    NetworkJSONLoaderContract listener;

    public NetworkJSONLoader(Context context, NetworkJSONLoaderContract listener) {
        this.context = context;
        this.listener = listener;
    }

    public void get(String url, final String token) {
        // Tag used to cancel the request
        final String tag_json_obj = "JSON_GET";

        listener.onTaskLoading();
        Log.d(tag_json_obj, "Token " + token +" used");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onTaskCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onTaskError(error);
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(tag_json_obj, "Get Headers Called");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void post(String url, Map data) {
        // Tag used to cancel the request
        final String tag_json_obj = "JSON_POST";

        listener.onTaskLoading();

        JSONObject requestObject = new JSONObject();
        try {
            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                requestObject.put(pair.getKey().toString(), pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onTaskCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onTaskError(error);
                    }
                }
        );

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void post(String url, Map data, final String token) {
        // Tag used to cancel the request
        final String TAG = "JSON_POST";

        listener.onTaskLoading();

        JSONObject requestObject = new JSONObject();
        try {
            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                requestObject.put(pair.getKey().toString(), pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onTaskCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onTaskError(error);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "Get Headers Called");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq, TAG);
    }

    public interface NetworkJSONLoaderContract {
        void onTaskLoading();

        void onTaskCompleted(JSONObject response);

        void onTaskError(VolleyError error);
    }


    public interface NetworkJSONPOSTRequest {
        void onTaskLoading();

        void onTaskCompleted(JSONObject response);

        void onTaskError(VolleyError error);
    }
}
