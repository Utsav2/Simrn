package cs241.simrn;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

class WorkerListAdapter extends ArrayAdapter<JSONObject> {

    Activity mActivity;

    public WorkerListAdapter(Activity activity) {

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
            tv.setText(object.getString("image"));
            tv2.setText(object.getString("rtime"));
            tv3.setText(object.getString("sub_image"));
        }
        catch (JSONException e){
            Log.e("JSON", "Got incorrectly formatted stuff in List Adapter");
        }

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MapPhaseActivity.class);
                i.putExtra("data", getItem(position).toString());
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

}