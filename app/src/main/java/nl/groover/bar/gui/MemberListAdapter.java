package nl.groover.bar.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import nl.groover.bar.R;

public class MemberListAdapter extends CursorAdapter {

	private DecimalFormat df = new DecimalFormat("0.00");
	private LayoutInflater cursorInflater;

	public MemberListAdapter(Context context, Cursor c, int flags) {
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

		TextView textViewAccount = (TextView) view.findViewById(R.ledenlijstrow.account);
		textViewAccount.setText(df.format(cursor.getDouble(5)));

		TextView textViewName = (TextView) view.findViewById(R.ledenlijstrow.name);
		String name = cursor.getString(1);

		String prefix = cursor.getString(2);

		if( prefix != null){
			name = name + " " + prefix;
		}

		name = name + " "+ cursor.getString(3);

		textViewName.setText(name);

	}
}