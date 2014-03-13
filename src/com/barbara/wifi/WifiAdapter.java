package com.barbara.wifi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.SQLdb;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter {
//	private static int[] imageResources = { R.drawable.filled0, R.drawable.filled1,
//	R.drawable.filled2, R.drawable.filled3, R.drawable.filled4 };
//
//	public WifiAdapter(Context context, int layout, Cursor c, String[] from,
//			int[] to, int flags) {
//		super(context, layout, c, from, to, flags);
//		// TODO Auto-generated constructor stub
//	}
//	
//	@Override
//	public void bindView(View v, Context context, Cursor cursor) {
//		ViewHolder holder = (ViewHolder) v.getTag();
//		holder.wifi.setText(cursor.getString(1));
//		holder.mode.setBackgroundResource(imageResources[cursor.getInt(2)]);
//		final int mode = cursor.getInt(2);
//		final String name = cursor.getString(1);
//
//		holder.mode.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				int pos = mode;
//				pos++;
//				if(pos == 5){
//					pos = 0;
//				}
//				
//			}
//		});
//	}
//	
//	@Override
//	public View newView(Context context, Cursor cursor, ViewGroup parent) {
//		LayoutInflater inflater = LayoutInflater.from(context);
//		View v = inflater
//				.inflate(R.layout.wifi_list_item, parent, false);
//		ViewHolder holder = new ViewHolder();
//		holder.wifi = (TextView) v.findViewById(R.id.wifi_name);
//		holder.mode = (ImageButton) v.findViewById(R.id.wifi_mode);
//		v.setTag(holder);
//		return v;
//	}
//	=============================================
	private LayoutInflater mInflater;
	private  List<Map<String, Object>> _data;
	private static String[] modeResources = { "不变","铃声",
			"振动", "混合", "静音" };
	private SQLdb db;

	public WifiAdapter(Context context, List<Map<String, Object>> data, SQLdb WIFIdb) {
		this.mInflater = LayoutInflater.from(context);
		this._data = data;
		db = WIFIdb;
	}

	@Override
	public int getCount() {
		
		// TODO Auto-generated method stub
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.wifi_list_item, null);
			viewHolder.wifi = (TextView)convertView.findViewById(R.id.wifi_name);
			viewHolder.mode = (TextView) convertView.findViewById(R.id.wifi_mode);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final int pos = position;
		viewHolder.wifi.setText((String)_data.get(position).get("name"));	
		viewHolder.mode.setText(modeResources[(Integer)_data.get(pos).get("mode")]);
//		viewHolder.mode.setBackgroundResource(imageResources[(Integer)_data.get(pos).get("mode")]);
		viewHolder.mode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int mode = (Integer)_data.get(pos).get("mode");
				String name = (String)_data.get(pos).get("name");
//				if(mode++ == 5){
//					mode = 0;					
//				}
				 mode = ++mode == 5? 0:mode;
				db.updateWIFI(name, mode);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", name);
				map.put("mode", mode);
				_data.set(pos, map);
				Log.v("adapter", String.valueOf(mode));
				viewHolder.mode.setText(modeResources[mode]);				
				WifiActivity.adapter.notifyDataSetChanged();
				WifiActivity.lv.invalidateViews();
			}
		});
		return convertView;
	}


	public final class ViewHolder{
		public TextView wifi;
		public TextView mode;
	}

}
