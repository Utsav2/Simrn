package cs241.simrn;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class ControllerPhaseActivity extends ActionBarActivity {

    private JSONObject data;

    Timer numberOfWorkersTimer;

    Timer statusCheckTimer;

    private static final int DELAY = 1000;

    private static final int POLL_PERIOD = 10000;

    TextView numberOfWorkersTextView;

    private int numberOfWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numberOfWorkersTimer = new Timer();
        statusCheckTimer  = new Timer();
        try{
            data = new JSONObject(getIntent().getExtras().getString("data"));
        }
        catch (JSONException e) {
            Log.e("JSON", e.getMessage() + "");
            //never going to happen
        }
        setContentView(R.layout.activity_controller_phase_actiivty);
        setUpView();
        pollHowManyWorkersRegistered();
    }

    private void setUpView(){

        Button startJobButton = (Button)findViewById(R.id.startMap);
        startJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJob();
            }
        });

        Button deletePreviousJobButton = (Button)findViewById(R.id.deleteJob);
        deletePreviousJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteJob();
                Toast.makeText(getApplicationContext(), "Will delete job", Toast.LENGTH_SHORT).show();
            }
        });

        numberOfWorkersTextView = (TextView)findViewById(R.id.numberOfWorkers);
    }


    private void pollHowManyWorkersRegistered(){
        numberOfWorkersTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkNumberOfWorkerStatus();
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage() + "");
                }
            }
        }, DELAY, POLL_PERIOD);
    }

    private void pollForResults(){
        statusCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkWorkerResults();
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage() + "");
                }
            }
        }, DELAY, POLL_PERIOD);
    }

    private void startJob(){

        SimrnNetworker.initialize(this);
        SimrnNetworker.startJob(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWorkerNumberPoll();
                numberOfWorkersTextView.setText("The work has begun");
                pollForResults();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void checkIfCompleted(JSONArray results){

        //if all the workers have registered their data
        if(results.length() == numberOfWorkers){
            stopStatusCheckerPoll();
            Intent intent =  new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("data", results.toString());
            startActivity(intent);
            finish();
        }

    }

    private void checkWorkerResults() throws JSONException {

        SimrnNetworker.initialize(this);
        SimrnNetworker.getResults(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    checkIfCompleted(jsonObject.getJSONArray("results"));
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage() + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void checkNumberOfWorkerStatus() throws JSONException{

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
        numberOfWorkersTextView.setText(jsonObject.getString("number"));
        numberOfWorkers = jsonObject.getInt("number");
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

    private void stopWorkerNumberPoll(){
        numberOfWorkersTimer.cancel();
        numberOfWorkersTimer.purge();
    }

    private void stopStatusCheckerPoll(){
        statusCheckTimer.cancel();
        statusCheckTimer.purge();
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
        stopWorkerNumberPoll();
        stopStatusCheckerPoll();
        super.finish();
    }
}
