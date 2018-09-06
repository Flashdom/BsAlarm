package com.bsaldevs.bsalarmer.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;
import com.bsaldevs.bsalarmer.R;

import java.util.ArrayList;
import java.util.List;

public class PointListActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private RecyclerView pointsList;
    List<Point> points;
    ArrayList<PointWrapper> pwlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);
        sendMessageToLocationService();
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.POINT_LIST_ACTION);
        registerReceiver(receiver, intentFilter);
        pointsList = findViewById(R.id.pointsRecyclerView);
    }

    private void sendMessageToLocationService() {
        Log.d(Constants.TAG, "PointListActivity: sendMessageToLocationService");
        Intent location = new Intent(Constants.LOCATION_MANAGER_ACTION)
                .putExtra("task", BroadcastActions.GET_TARGETS)
                .putExtra("sender", "pointListActivity");
        sendBroadcast(location);
    }

    private void changeTarget(Point point) {
        Log.d(Constants.TAG, "PointListActivity: sendMessageToLocationService");
        Intent location = new Intent(Constants.LOCATION_MANAGER_ACTION)
                .putExtra("task", BroadcastActions.CHANGE_TARGET)
                .putExtra("point", point)
                .putExtra("packedPointExtras", "active|id");
        sendBroadcast(location);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int task = intent.getIntExtra("task", 0);
            Log.d(Constants.TAG, "MapsActivity: onReceive: task code " + task);
            if (task == BroadcastActions.GET_TARGETS) {
                points = (ArrayList<Point>) intent.getSerializableExtra("points");

                pwlist = new ArrayList<>();
                for (Point p : points) {
                    pwlist.add(new PointWrapper(p));
                }
                pointsList.setLayoutManager(new LinearLayoutManager(PointListActivity.this));
                pointsList.setAdapter(new MyAdapter());

            }
        }
    }

    private class PointWrapper{
        public boolean expanded = false;
        public Point point;
        public PointWrapper(Point p){
            point = p;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PointViewHolder> {

        public class PointViewHolder extends RecyclerView.ViewHolder {

            public TextView pointName;
            public ToggleButton arrow;
            public LinearLayout expandPanel;
            private ToggleButton activation;

            public PointViewHolder(View v) {
                super(v);
                pointName = v.findViewById(R.id.listPointItemName);
                expandPanel = v.findViewById(R.id.pointItemHidden);
                arrow = v.findViewById(R.id.toggleButton2);
                activation = v.findViewById(R.id.activeToggleButton);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int itemPosition = getLayoutPosition();
                        /*
                        if (expandedPosition >= 0) {
                            int prev = expandedPosition;
                            notifyItemChanged(prev);
                        }
                        */
                        //expandedPosition = itemPosition;
                        pwlist.get(itemPosition).expanded = !pwlist.get(itemPosition).expanded;
                        notifyItemChanged(itemPosition);

                    }
                });
            }
        }


        @Override
        public PointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_list_item, parent, false);
            //PointViewHolder vh = new PointViewHolder(v);


            return new PointViewHolder(v);
        }


        @Override
        public void onBindViewHolder(PointViewHolder holder, final int position) {
            holder.pointName.setText(pwlist.get(position).point.getName());
            holder.activation.setChecked(pwlist.get(position).point.isActive());

            if(pwlist.get(position).expanded){
                holder.expandPanel.setVisibility(View.VISIBLE);
                holder.arrow.setChecked(true);
            } else {
                holder.expandPanel.setVisibility(View.GONE);
                holder.arrow.setChecked(false);
            }

            holder.activation.setChecked(pwlist.get(position).point.isActive());
            holder.activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwlist.get(position).point.setActive(!pwlist.get(position).point.isActive());
                    Point point = pwlist.get(position).point;
                    sendUpdatedStateOfPoint(point);
                }
            });


        }

        @Override
        public int getItemCount() {
            return pwlist.size();
        }
    }

    private void sendUpdatedStateOfPoint(Point point) {
        Log.d(Constants.TAG, "PointRecyclerAdapter: sendUpdatedStateOfPoint");
        Intent location = new Intent(Constants.LOCATION_MANAGER_ACTION)
                .putExtra("task", BroadcastActions.CHANGE_TARGET)
                .putExtra("point", point)
                .putExtra("packedPointExtras", "active");
        sendBroadcast(location);
    }

}
