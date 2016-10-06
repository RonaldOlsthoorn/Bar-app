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
	private int layout;
	private Cursor source;
	private DecimalFormat df = new DecimalFormat("0.00");
	private ListActionListener notice;
	
	public interface ListActionListener{
		
		public void edit(int id,int pos);
		public void delete(int id);
	}
	
	public void changeCursor(Cursor c){
		
		source = c;
	}
	
	public OrderListAdapter(Context context, int layout, Cursor c, ListActionListener l){
		
		this.context = context;
		this.layout = layout;
		source = c;
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
        TextView txtFirst;
        TextView txtLast;
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
	    	holder.txtFirst = (TextView) convertView.findViewById(R.orderOverViewRow.first);
	    	holder.txtLast = (TextView) convertView.findViewById(R.orderOverViewRow.last);
	    	holder.txtTotal = (TextView) convertView.findViewById(R.orderOverViewRow.subtotal);
	    	holder.txtDate = (TextView) convertView.findViewById(R.orderOverViewRow.date);
	    	holder.btDelete = (Button) convertView.findViewById(R.orderOverViewRow.delete);
	    	holder.btEdit = (Button) convertView.findViewById(R.orderOverViewRow.edit);
		    convertView.setTag(holder);
	    }
	    else{
	    	holder = (ViewHolder) convertView.getTag();    	
	    }
	    

	    holder.txtFirst.setText(source.getString(2));
	    holder.txtLast.setText(source.getString(3));
	    holder.txtTotal.setText(df.format(source.getDouble(5)));
	    holder.txtDate.setText(source.getString(6));
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
		    l.edit(id,pos);
            OrderListAdapter.this.notifyDataSetChanged();
		}		
	}
}