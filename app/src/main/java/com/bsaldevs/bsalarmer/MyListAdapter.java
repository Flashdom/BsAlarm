package com.bsaldevs.bsalarmer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

/**
 * Created by azatiSea on 02.09.2018.
 */

public class MyListAdapter extends ArrayAdapter<String> {

    private int layout;
    private List<Point> points;

    public MyListAdapter(@NonNull Context context, int resource, @NonNull List<String> names, @NonNull List<Point> points) {
        super(context, resource, names);
        layout = resource;
        this.points = points;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.activePointButton = (ImageButton) convertView.findViewById(R.id.activeToggleButton);
            holder.activePointButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "click on toggle button", Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setTag(holder);
        }

        return convertView;
    }

    public class ViewHolder {
        ImageButton activePointButton;
    }
}
