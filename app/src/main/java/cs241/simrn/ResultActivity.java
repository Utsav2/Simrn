package cs241.simrn;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ResultActivity extends ActionBarActivity {

    private ListView listView;
    private ResultListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        setUpView();
        try{
            addResults(new JSONArray(getIntent().getExtras().getString("data")));
        }
        catch (JSONException e){
            Log.e("JSON", e.toString());
        }
    }

    private void addResults(JSONArray items) throws JSONException{
        for(int i = 0; i < items.length(); i++){
            mAdapter.add(items.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void setUpView(){
        mAdapter = new ResultListAdapter(this);
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_worker, menu);
        return true;
    }

    @Override
    public void finish(){
        SimrnNetworker.initialize(this);
        SimrnNetworker.deleteJob(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(getApplicationContext(), "Deleted job successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley", volleyError.getMessage() + "");
            }
        });
        super.finish();
    }

}
