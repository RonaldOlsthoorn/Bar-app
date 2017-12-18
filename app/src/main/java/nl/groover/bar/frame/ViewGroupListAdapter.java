package nl.groover.bar.frame;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import nl.groover.bar.R;

public class ViewGroupListAdapter extends CursorAdapter {

	private DecimalFormat df = new DecimalFormat("0.00");
	private LayoutInflater cursorInflater;

	public ViewGroupListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);

		cursorInflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		return cursorInflater.inflate(R.layout.ledenlijstrow, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView textViewAccount = (TextView) view.findViewById(R.id.ledenlijstrow_account);
		textViewAccount.setText(df.format(cursor.getDouble(3)));

		TextView textViewName = (TextView) view.findViewById(R.id.ledenlijstrow_name);
		textViewName.setText(cursor.getString(1));
	}
}