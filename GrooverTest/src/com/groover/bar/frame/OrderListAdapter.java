package com.groover.bar.frame;

import java.text.DecimalFormat;
import com.groover.bar.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter{
	
	private Context context;
	private int layout;
	private Order source;
	private DecimalFormat df = new DecimalFormat("0.00");
	private UpdateListener notice;
	
	public interface UpdateListener{
		
		public void Update(Order o);
	}
	
	public OrderListAdapter(Context context, int layout, Order o, UpdateListener l){
		
		this.context = context;
		this.layout = layout;
		source = o;
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
		return source.getId(position);
	}
	
	private class ViewHolder {
        TextView txtName;
        TextView txtAmount;
        TextView txtPrice;
        TextView txtSub;
		Button btCancel;
		Button btAdd;
		Button btSubstr;
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
	    	holder.txtPrice = (TextView) convertView.findViewById(R.orderRow.price);
	    	holder.txtSub = (TextView) convertView.findViewById(R.orderRow.sub);
	    	holder.btCancel = (Button) convertView.findViewById(R.orderRow.cancel);
	    	holder.btAdd = (Button) convertView.findViewById(R.orderRow.addition);
	    	holder.btSubstr = (Button) convertView.findViewById(R.orderRow.substract);
		    convertView.setTag(holder);
	    }
	    else{
	    	holder = (ViewHolder) convertView.getTag();
	    	
	    }
	    
	    holder.txtName.setText(s.getArticle().getName());
	    holder.txtAmount.setText(s.getAmount()+"");
	    holder.txtPrice.setText(df.format(s.getArticle().getPrice()));
	    holder.txtSub.setText(df.format(s.getSubtotal()));
	    holder.btCancel.setOnClickListener(new deleteAdapter(position,notice));
	    holder.btAdd.setOnClickListener(new additionAdapter(position, notice));
	    holder.btSubstr.setOnClickListener(new substractionAdapter(position, notice));
	    return convertView;

	}
	
	private class deleteAdapter implements OnClickListener{

		int position;
		UpdateListener l;
		
		public deleteAdapter(int pos, UpdateListener l){
			this.position = pos;
			this.l=l;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			source.deleteOrder(position);
            OrderListAdapter.this.notifyDataSetChanged();
            l.Update(source);
		}		
	}
	
	private class additionAdapter implements OnClickListener{

		int position;
		UpdateListener l;
		
		public additionAdapter(int pos, UpdateListener l){
			this.position = pos;
			this.l=l;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			source.addAmmountToOrderUnit(position,1);
            OrderListAdapter.this.notifyDataSetChanged();
            l.Update(source);
		}		
	}
	
	private class substractionAdapter implements OnClickListener{

		int position;
		UpdateListener l;
		
		public substractionAdapter(int pos, UpdateListener l){
			this.position = pos;
			this.l=l;
		}
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			source.addAmmountToOrderUnit(position,-1);
            OrderListAdapter.this.notifyDataSetChanged();
            l.Update(source);
		}		
	}
	
}
