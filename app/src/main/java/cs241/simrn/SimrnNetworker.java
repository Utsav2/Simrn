package cs241.simrn;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Abstracts the networking stuff away
 * Created by utsav on 2/23/15.
 */
public class SimrnNetworker {

    private static RequestQueue mRequestQueue;
    private static Context mContext;

    public static void initialize(Context applicationContext){
        mContext = applicationContext;
        mRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    public static void createJob(String imageUrl, String subImageUrl,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener){

        checkForInitialize();
        HashMap<String, String> params = new HashMap<>();
        params.put("image", imageUrl);
        params.put("sub_image", subImageUrl);
        putImei(params);
        createRequest(listener, errorListener, "createRequest", params, Request.Method.POST);
    }

    private static void createRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener,
                               String url, HashMap<String, String> params, int method){

        addToQueue(new SimrnRequest(listener, errorListener, method, url, params));

    }

    public static void deleteJob(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        checkForInitialize();
        HashMap<String, String> params = new HashMap<>();
        putImei(params);
        createRequest(listener, errorListener, "deleteRequest", params, Request.Method.POST);
    }

    public static void getRequests(Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener){
        checkForInitialize();
        createRequest(listener, errorListener, "getRequests", null, Request.Method.GET);
    }

    public static void registerWorker(Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener, String parent){
        toggleWorkerStatus(listener, errorListener, parent, "registerWorker");
    }

    private static void toggleWorkerStatus(Response.Listener<JSONObject> listener,
                                           Response.ErrorListener errorListener,
                                           String parent, String url){
        checkForInitialize();
        HashMap<String, String> params = new HashMap<>();
        putImei(params);
        params.put("parent", parent);
        createRequest(listener, errorListener, url, params, Request.Method.POST);

    }

    public static void unregisterWorker(Response.Listener<JSONObject> listener,
                                        Response.ErrorListener errorListener, String parent){

        toggleWorkerStatus(listener, errorListener, parent, "unregisterWorker");

    }

    private static void putImei(HashMap<String, String> params){
        TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        params.put("imei", telephonyManager.getDeviceId());
    }

    private static void addToQueue(SimrnRequest request){
        mRequestQueue.add(request);
    }

    private static void checkForInitialize(){
        if(mContext == null){
            Log.e("Simrn Networker", "Network manager not initialized!");
            System.exit(1);
        }
    }
}
