package com.arshooter3d.support;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExamplesAdapter extends BaseAdapter {

	private List<String> objects;
	private final Context context;

	public ExamplesAdapter(Context context, List<String> objects) {
		this.context = context;
		this.objects = objects;
	}

	public int getCount() {
		return objects.size();
	}

	public Object getItem(int position) {
		return objects.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Object obj = objects.get(position);

		TextView tv = new TextView(context);
		tv.setPadding(10, 20, 10, 10);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setText(obj.toString());
		tv.setTextColor(0xaaffffff);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 20);	
		//unit parameter is TypedValue.COMPLEX_UNIT_PX and the size parameter is the percent of total width of the screen
		
		Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
	    tv.setTypeface(font);
		
		return tv;
	}

}
