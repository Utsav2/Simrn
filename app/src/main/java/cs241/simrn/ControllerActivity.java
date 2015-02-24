package cs241.simrn;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.regex.Pattern;


public class ControllerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        setUpView();
    }

    private void setUpView(){
        final EditText imageUrl = (EditText)findViewById(R.id.imageUrlEdit);
        final EditText subImageUrl = (EditText)findViewById(R.id.subImageUrlEdit);
        Button createRequestButton = (Button)findViewById(R.id.startJobButton);
        createRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrlString = imageUrl.getText().toString().trim();
                String subImageUrlString = subImageUrl.getText().toString().trim();
                if(Patterns.WEB_URL.matcher(imageUrlString).matches()
                        && Patterns.WEB_URL.matcher(subImageUrlString).matches()){
                    Toast.makeText(getApplicationContext(), "Attempting to create image search request", Toast.LENGTH_SHORT).show();
                    createJob(imageUrlString, subImageUrlString);

                }
                else{
                    Toast.makeText(getApplicationContext(), "Are you sure you typed in a proper URL?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createJob(String imageUrl, String subImageUrl){

        SimrnNetworker.initialize(this);
        SimrnNetworker.createJob(imageUrl, subImageUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Intent intent = new Intent(getApplicationContext(), ControllerPhaseActiivty.class);
                intent.putExtra("data", jsonObject.toString());
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Request failed!", volleyError.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller, menu);
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


}
