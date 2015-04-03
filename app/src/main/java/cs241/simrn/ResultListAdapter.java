package cs241.simrn;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ResultListAdapter extends ArrayAdapter<JSONObject> {

    Activity mActivity;

    public ResultListAdapter(Activity activity) {

        super(activity, R.layout.list_item);

        mActivity = activity;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // convertView has not been inflated; inflate it
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, parent, false);

        }

        // convertView has been inflated
        TextView tv = (TextView) convertView.findViewById(R.id.textView1);

        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);

        TextView tv3 = (TextView) convertView.findViewById(R.id.sideTextView);

        TextView tv4 = (TextView) convertView.findViewById(R.id.changeTextView);

        TextView tv5 = (TextView) convertView.findViewById(R.id.timeTextView);

        try{
            JSONObject object = getItem(position);
            JSONObject result = new JSONObject(object.getString("result"));
            if(result.getBoolean("status")){
                JSONArray values = result.getJSONArray("result");
                tv.setText(String.format("Found at %d, %d", values.getInt(0), values.getInt(1)));
            }
            else{
                tv.setText("Not found");
            }
            tv2.setText(object.getString("imei"));
            tv3.setText(object.getString("parent"));
        }
        catch (JSONException e){
            Log.e("JSON", "Got incorrectly formatted stuff in List Adapter - " + e.getMessage());
        }

        return convertView;
    }

}