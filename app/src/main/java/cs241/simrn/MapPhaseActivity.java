package cs241.simrn;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class MapPhaseActivity extends ActionBarActivity {

    private JSONObject data;

    private String parent;

    Timer timer;

    private static final int DELAY = 1000;

    private static final int POLL_PERIOD = 10000;

    private String subImageUrl;

    static {
        System.loadLibrary("ndkModule");
    }

    public native String graphSearch(Bitmap graph, Bitmap subgraph);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_phase);
        timer = new Timer();
        try{
            data = new JSONObject(getIntent().getExtras().getString("data"));
            parent = data.getString("imei");
            timer = new Timer();
            register();
        }
        catch (JSONException e){
            Log.e("JSON", e.getMessage() + "");
            //never going to happen
        }
    }

    private void register() throws JSONException{
        SimrnNetworker.initialize(this);
        SimrnNetworker.registerWorker(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                poll();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley", volleyError.getMessage() + "");
            }
        }, data.getString("imei"));
    }

    private void poll(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkStatus();
                }
                catch (JSONException e){
                    Log.e("JSON", e.getMessage() + "");
                }
            }
        }, DELAY, POLL_PERIOD);
    }

    private void checkStatus() throws JSONException{

        SimrnNetworker.initialize(this);
        SimrnNetworker.getRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getJSONArray("request").getJSONObject(0).getBoolean("status")) {
                        subImageUrl = jsonObject.getJSONArray("request").getJSONObject(0).getString("sub_image");
                        stopPolling();
                        getSpecificData();
                    }
                }
                catch (JSONException e){
                    Log.e("JSON", e.getMessage() + "");
                    Log.e("JSON", jsonObject.toString() + "");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, parent);

    }

    private void stopPolling(){
        timer.cancel();
        timer.purge();
    }

    private void doJob(final Bitmap dividedImage){
        SimrnNetworker.initialize(this);
        SimrnNetworker.getImage(new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap subImage) {
                showImage(dividedImage);
                final TextView status = (TextView) findViewById(R.id.textView);
                status.setText("Beginning job");
                String result = graphSearch(dividedImage, subImage);

                SimrnNetworker.registerWorkerResult(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        status.setText("Completed job");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("registering worker result error", volleyError.toString());
                        status.setText("Error in sending job data");
                    }
                }, result, parent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, subImageUrl);
    }

    private void getSpecificData(){

        SimrnNetworker.getWorkerData(new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                    doJob(bitmap);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("image_error", volleyError.toString());
            }
        });
    }

    private void showImage(Bitmap bitmap){
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

    private void unregister() throws JSONException{
        SimrnNetworker.initialize(this);
        SimrnNetworker.unregisterWorker(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley", volleyError.getMessage() + "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_phase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){

        stopPolling();

        try{
            unregister();
        }
        catch (JSONException e){
            //never going to happen
            Log.e("JSON", e.getMessage() + "");
        }
        super.finish();
    }
}
