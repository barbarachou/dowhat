package com.barbara.message;

import java.util.List;
import java.util.Map;

import com.barbara.dowhat.R;
import com.barbara.dowhat.utility.MyFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BlackAdapter extends ArrayAdapter<Map<String, String>>{
	private final Context context;

	  public BlackAdapter(Context context, List<Map<String, String>> records) {
	    super(context, R.layout.black_list_item, records);
	    this.context = context;
	  }
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowV = inflater.inflate(R.layout.black_list_item, parent, false);
	    
	    TextView fromV = (TextView)rowV.findViewById(R.id.black_from);
	    TextView timeV = (TextView)rowV.findViewById(R.id.black_time);
	    TextView textV = (TextView)rowV.findViewById(R.id.black_text);
	    //textV.setMaxLines(2);
	    //textV.setSingleLine();
	    //textV.setEllipsize(TextUtils.TruncateAt.END);
	    
	    Map<String, String> record = getItem(position);

	    fromV.setText(record.get(com.barbara.dowhat.constant.Constant.Message.FROM));
	    timeV.setText(record.get(com.barbara.dowhat.constant.Constant.Message.SENT_AT));
	    textV.setText(record.get(com.barbara.dowhat.constant.Constant.Message.TEXT));
	    
	    return rowV;
	  }
}
