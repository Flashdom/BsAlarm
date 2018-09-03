package com.bsaldevs.bsalarmer.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.PointListAdapter;
import com.bsaldevs.bsalarmer.Point;
import com.bsaldevs.bsalarmer.R;

import java.util.ArrayList;
import java.util.List;

public class PointListActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);
        sendMessageToLocationService();
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.POINT_LIST_ACTION);
        registerReceiver(receiver, intentFilter);

        listView = findViewById(R.id.listView);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Button button = (Button) parent.getChildAt(position);
            }
        });*/
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
                List<Point> points = (ArrayList<Point>) intent.getSerializableExtra("points");

                List<String> names = new ArrayList<>();
                for (Point point : points) {
                    names.add(point.getName());
                }

                ArrayAdapter<String> adapter = new PointListAdapter(PointListActivity.this, R.layout.point_list_item, R.id.listPointItemName, names, points);

                listView = findViewById(R.id.listView);
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
