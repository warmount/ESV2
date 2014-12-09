package org.sfsteam.easyscrum.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by warmount on 09.12.2014.
 */
public class ListViewArrayAdapter<T> extends ArrayAdapter {
    public ListViewArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }
    @Override
    public View getView(int position, android.view.View convertView, android.view.ViewGroup parent){
        TextView tv = (TextView) super.getView(position,convertView,parent);
        tv.setTextColor(Color.WHITE);
        return tv;
    }
}