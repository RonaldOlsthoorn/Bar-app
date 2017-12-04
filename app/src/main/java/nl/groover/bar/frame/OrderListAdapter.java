package nl.groover.bar.frame;

import java.text.DecimalFormat;

import nl.groover.bar.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter{
	
	private Context context;
	private Cursor source;
	private DecimalFormat df = new DecimalFormat("0.00");
	private ListActionListener notice;

	private int layout = R.layout.order_overview_row;

	public interface ListActionListener{
		
		void edit(int id,int pos);
		void delete(int id);
	}
	
	public void changeCursor(Cursor c){
		
		source = c;
	}
	
	public OrderListAdapter(Context context, Cursor cursor,  ListActionListener l){
		
		this.context = context;
		source = cursor;
		notice = l;
	}

	@Override
	public int getCount() {
		return source.getCount();
	}

	@Override
	public Object getItem(int pos) {
		source.moveToPosition(pos);
		return source;
	}

	@Override
	public long getItemId(int position) {
		
		source.moveToPosition(position);
		return (long) source.getInt(0);
	}
	
	private class ViewHolder {
        TextView txtName;
        TextView txtTotal;
        TextView txtDate;
		Button btDelete;
		Button btEdit;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		source.moveToPosition(position);
		LayoutInflater mInflater = (LayoutInflater)
        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		int id = source.getInt(0);

		ViewHolder holder;
		
	    if (convertView == null) {
	    	convertView = mInflater.inflate(layout, null);
	    	holder = new ViewHolder();
	    	holder.txtName = (TextView) convertView.findViewById(R.id.orderOverViewRow_name);
	    	holder.txtTotal = (TextView) convertView.findViewById(R.id.orderOverViewRow_subtotal);
	    	holder.txtDate = (TextView) convertView.findViewById(R.id.orderOverViewRow_date);
	    	holder.btDelete = (Button) convertView.findViewById(R.id.orderOverViewRow_delete);
	    	holder.btEdit = (Button) convertView.findViewById(R.id.orderOverViewRow_edit);
		    convertView.setTag(holder);
	    }
	    else{
	    	holder = (ViewHolder) convertView.getTag();    	
	    }

		holder.txtName.setText(source.getString(1));
	    holder.txtTotal.setText(df.format(source.getDouble(3)));
	    holder.txtDate.setText(source.getString(4));
	    holder.btDelete.setOnClickListener(new deleteAdapter(id, notice));
	    holder.btEdit.setOnClickListener(new editAdapter(id, position, notice));
	    return convertView;
	}
	
	private class deleteAdapter implements OnClickListener{

		int id;
		ListActionListener l;
		
		public deleteAdapter(int id, ListActionListener l){
			this.id = id;
			this.l=l;
		}
		
		@Override
		public void onClick(View arg0) {
			l.delete(id);
			OrderListAdapter.this.notifyDataSetChanged();     
		}		
	}
	
	private class editAdapter implements OnClickListener{

		int id;
		int pos;
		ListActionListener l;
		
		public editAdapter(int id, int pos, ListActionListener l){
			this.id = id;
			this.l=l;
			this.pos = pos;
		}
		
		@Override
		public void onClick(View arg0) {
		    l.edit(id, pos);
            OrderListAdapter.this.notifyDataSetChanged();
		}		
	}
}