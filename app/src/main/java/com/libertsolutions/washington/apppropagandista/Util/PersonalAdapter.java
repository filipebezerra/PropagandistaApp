package com.libertsolutions.washington.apppropagandista.Util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import java.util.List;
import java.util.Map;

/**
 * Created by washington on 21/09/14.
 */
public class PersonalAdapter extends SimpleAdapter {
    private int[] colors = new int[] {Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF") };

    public PersonalAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
            String[] from, int[] to) {
        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}
