package cs241.simrn;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WorkerActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{


    private WorkerListAdapter mAdapter;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_list_layout);
        setUpView();
    }

    private void setUpView(){
        mAdapter = new WorkerListAdapter(this);
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(mAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeWorkerLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getAndShowData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_worker, menu);
        return true;
    }

    private void getAndShowData(){
        SimrnNetworker.initialize(this);
        SimrnNetworker.getRequests(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try{
                    parseAndDisplay(jsonObject);
                }
                catch (JSONException e){
                    Log.e("JSON", e.toString() + "");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("JSON", "Failed to get data!");
            }
        });
    }

    private void parseAndDisplay(JSONObject jsonObject) throws JSONException{
        clearResults();
        parse(jsonObject);
        display();
    }

    private void clearResults(){
        mAdapter.clear();
    }

    private void parse(JSONObject jsonObject) throws JSONException{
        JSONArray requests = jsonObject.getJSONArray("requests");
        for(int r = 0; r < requests.length(); r++){
            addToAdapter(requests.getJSONObject(r));
        }
    }

    private void addToAdapter(JSONObject request){
        mAdapter.add(request);
    }

    private void display(){
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
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
    public void onRefresh() {
        getAndShowData();
    }
}
