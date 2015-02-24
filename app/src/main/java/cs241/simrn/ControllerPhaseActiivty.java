package cs241.simrn;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class ControllerPhaseActiivty extends ActionBarActivity {

    private JSONObject data;

    Timer timer;

    private static final int DELAY = 1000;

    private static final int POLL_PERIOD = 10000;

    TextView numberOfWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer();
        try{
            data = new JSONObject(getIntent().getExtras().getString("data"));
            timer = new Timer();
        }
        catch (JSONException e) {
            Log.e("JSON", e.getMessage() + "");
            //never going to happen
        }
        setContentView(R.layout.activity_controller_phase_actiivty);
        setUpView();
        poll();
    }

    private void setUpView(){
        Button deletePreviousJobButton = (Button)findViewById(R.id.deleteJob);
        deletePreviousJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJob();
                Toast.makeText(getApplicationContext(), "Will delete job", Toast.LENGTH_SHORT).show();
            }
        });

        numberOfWorkers = (TextView)findViewById(R.id.numberOfWorkers);
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
        SimrnNetworker.getNumberOfWorkers(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    displayNumberOfWorkers(jsonObject);
                }
                catch (JSONException e){
                    Log.e("JSON", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

    }

    private void displayNumberOfWorkers(JSONObject jsonObject) throws JSONException{
        numberOfWorkers.setText(jsonObject.getString("number"));
    }

    private void deleteJob(){
        SimrnNetworker.initialize(this);
        SimrnNetworker.deleteJob(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Request failed!", volleyError.toString());
            }
        });
    }



    private void stopPolling(){
        timer.cancel();
        timer.purge();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller_phase_actiivty, menu);
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

        deleteJob();
        stopPolling();
        super.finish();
    }
}
