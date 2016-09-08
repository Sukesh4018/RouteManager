package com.BRP.routemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.BRP.routemanager.R;
import com.BRP.routemanager.models.Stop;

import java.util.List;

/**
 * Created by durgesh on 5/13/16.
 */
public class StopsListAdapter extends ArrayAdapter<Stop> {
    private Context context;
    private List<Stop> objects;

    public StopsListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public StopsListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public StopsListAdapter(Context context, int resource, Stop[] objects) {
        super(context, resource, objects);
    }

    public StopsListAdapter(Context context, int resource, int textViewResourceId, Stop[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public StopsListAdapter(Context context, int resource, List<Stop> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public StopsListAdapter(Context context, int resource, int textViewResourceId, List<Stop> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if (rootView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rootView = inflater.inflate(R.layout.stops_list_item, null);
            ((EditText) rootView.findViewById(R.id.stop_number)).setText(objects.get(position).getStop_pos());
            ((EditText) rootView.findViewById(R.id.stop_name)).setText(objects.get(position).getStop_name());
            ((EditText) rootView.findViewById(R.id.latitute)).setText(objects.get(position).getStop_lat() + "");
            ((EditText) rootView.findViewById(R.id.longitute)).setText(objects.get(position).getStop_lon() + "");
            Button deleteBtn = (Button) rootView.findViewById(R.id.delete_stop_btn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objects.remove(position);
                    for (int i = position; i < objects.size(); i++) {
                        String pos = objects.get(i).getStop_pos();
                        objects.get(i).setStop_pos(Integer.valueOf(pos) - 1 + "");
                        notifyDataSetChanged();
                    }
                }
            });
        }
        return super.getView(position, convertView, parent);
    }
}
