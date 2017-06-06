package nl.groover.bar.frame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class CustomListview extends LinearLayout {

	private BaseAdapter adapter;
	private Context context;

	public CustomListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CustomListview(Context context) {
		super(context);
		this.context = context;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;

		removeAllViews();

		for (int i = 0; i < adapter.getCount(); i++) {

			View convertview = new View(context);
			convertview = adapter.getView(i, null, null);
			addView(convertview);
		}
	}
}