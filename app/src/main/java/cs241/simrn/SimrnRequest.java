package cs241.simrn;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Facilitates requests made to the simrn server
 * Created by utsav on 2/23/15.
 */
public class SimrnRequest extends Request<JSONObject> {

    private final Response.Listener<JSONObject> mListener;

    private HashMap<String, String> mParams;

    static final String BASE_URL = "http://cs241.herokuapp.com/";

    public SimrnRequest(Response.Listener<JSONObject>  listener,
                        Response.ErrorListener errorListener, int method, String url,
                        HashMap<String, String> params){
        super(method, BASE_URL + addParams(url, method, params), errorListener);
        mListener = listener;
        mParams = params;
    }

    private static String addParams(String url, int method, HashMap<String, String> params){
        if(method != Method.GET || params == null)
            return url;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        for(Map.Entry<String, String> entry : params.entrySet()){
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build().toString();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            return Response.success(new JSONObject(new String(networkResponse.data)), getCacheEntry());
        }
        catch (JSONException e){
            Log.e("JSON", "Wasn't a proper json, instead received" + new String(networkResponse.data));
        }
        return Response.error(new VolleyError());
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        if (mParams == null)
            return super.getParams();

        return mParams;
    }

    @Override
    protected void deliverResponse(JSONObject jsonObject) {
        mListener.onResponse(jsonObject);
    }
}
