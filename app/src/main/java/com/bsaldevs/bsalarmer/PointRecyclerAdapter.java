package com.bsaldevs.bsalarmer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by azatiSea on 02.09.2018.
 */

public class PointRecyclerAdapter extends RecyclerView.Adapter<PointRecyclerAdapter.ViewHolder> {

    private List<Point> points;
    private Context context;

    public PointRecyclerAdapter(Context context, List<Point> points) {
        this.context = context;
        this.points = points;
    }

    /*public PointRecyclerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects, List<Point> points) {
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
    }*/

    private Point getPoint(int position) {
        return points.get(position);
    }

    private void sendUpdatedStateOfPoint(Point point) {
        Log.d(Constants.TAG, "PointRecyclerAdapter: sendUpdatedStateOfPoint");
        Intent location = new Intent(Constants.LOCATION_MANAGER_ACTION)
                .putExtra("task", BroadcastActions.CHANGE_TARGET)
                .putExtra("point", point)
                .putExtra("packedPointExtras", "active");
        context.sendBroadcast(location);
    }

    @NonNull
    @Override
    public PointRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointRecyclerAdapter.ViewHolder holder, int position) {
        final Point point = points.get(position);
        holder.textViewName.setText(point.getName());
        holder.activationButton.setChecked(point.isActive());
        holder.activationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                point.setActive(!point.isActive());
                sendUpdatedStateOfPoint(point);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ToggleButton activationButton;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.listPointItemName);
            activationButton = itemView.findViewById(R.id.activeToggleButton);
        }
    }
}
