package com.bsaldevs.bsalarmer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by azatiSea on 02.09.2018.
 */

public class PointListAdapter extends ArrayAdapter<String> {

    private int layout;
    private List<Point> points;

    public PointListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects, List<Point> points) {
        super(context, resource, textViewResourceId, objects);
        this.layout = resource;
        this.points = points;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);

            final Point point = getPoint(position);

            ViewHolder holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.listPointItemName);
            holder.nameTextView.setText(point.getName());
            holder.activePointButton = (ToggleButton) convertView.findViewById(R.id.activeToggleButton);
            holder.activePointButton.setChecked(point.isActive());
            holder.activePointButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "click on toggle button", Toast.LENGTH_SHORT).show();
                    point.setActive(!point.isActive());
                    sendUpdatedStateOfPoint(point, getContext());
                }
            });
            convertView.setTag(holder);
        }

        return convertView;
    }

    public class ViewHolder {
        ToggleButton activePointButton;
        TextView nameTextView;
    }

    private Point getPoint(int position) {
        return points.get(position);
    }

    private void sendUpdatedStateOfPoint(Point point, Context context) {
        Log.d(Constants.TAG, "PointListAdapter: sendUpdatedStateOfPoint");
        Intent location = new Intent(Constants.LOCATION_MANAGER_ACTION)
                .putExtra("task", BroadcastActions.CHANGE_TARGET)
                .putExtra("point", point)
                .putExtra("packedPointExtras", "active");
        context.sendBroadcast(location);
    }
}
