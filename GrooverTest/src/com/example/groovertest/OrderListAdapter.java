package com.example.groovertest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter{
	
	private Context context;
	private int layout;
	private Order source;
	private DecimalFormat df ;
	private PropertyChangeListener notice;
	
	public OrderListAdapter(Context context, int layout, Order o, PropertyChangeListener l){
		
		this.context = context;
		this.layout = layout;
		source = o;
		df = new DecimalFormat("0.00");
		notice = l;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return source.getCount();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return source.getUnit(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return source.getId(position);
	}
	
	private class ViewHolder {
        TextView txtName;
        TextView txtAmount;
        TextView txtSub;
		Button btCancel;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
			
		OrderUnit s = source.getUnit(position);
		LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		ViewHolder holder;
		
	    if (convertView == null) {
	    	convertView = mInflater.inflate(layout, null);
	    	holder = new ViewHolder();
	    	holder.txtName = (TextView) convertView.findViewById(R.orderRow.article);
	    	holder.txtAmount = (TextView) convertView.findViewById(R.orderRow.amount);
	    	holder.txtSub = (TextView) convertView.findViewById(R.orderRow.sub);
	    	holder.btCancel = (Button) convertView.findViewById(R.orderRow.cancel);
		    convertView.setTag(holder);
	    }
	    else{
	    	holder = (ViewHolder) convertView.getTag();
	    	
	    }
	    holder.txtName.setText(s.getArticle().getName());
	    holder.txtAmount.setText(s.getAmount()+"");
		s.getSubtotal();
	    holder.txtSub.setText(df.format(s.getSubtotal()));
	    holder.btCancel.setOnClickListener(new deleteAdapter(s.getArticle().getId(),notice));
	    return convertView;

	}
	
	private class deleteAdapter implements OnClickListener{

		int id;
		PropertyChangeListener l;
		
		public deleteAdapter(int id, PropertyChangeListener l){
			this.id = id;
			this.l=l;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			source.deleteOrderKey(id);
            OrderListAdapter.this.notifyDataSetChanged();
            PropertyChangeEvent e = new PropertyChangeEvent(arg0, "Type", "unclicked", "clicked");
            l.propertyChange(e);
		}		
	}
}
