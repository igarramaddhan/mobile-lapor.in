package tk.ynrk.laporin.helper;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import tk.ynrk.laporin.R;

public class NetworkJSONLoader {
    Context context;
    NetworkJSONLoaderContract listener;

    public NetworkJSONLoader(Context context, NetworkJSONLoaderContract listener) {
        this.context = context;
        this.listener = listener;
    }

    public void get(String url) {
        // Tag used to cancel the request
        String tag_json_obj = "JSON_GET";

        listener.onTaskLoading();

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
        );

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void post(String url, Map data) {
        // Tag used to cancel the request
        String tag_json_obj = "JSON_POST";

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

        // Adding request to request queue
        VolleySingleton.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
