package com.groover.bar.frame;

import java.text.DecimalFormat;

import com.groover.bar.R;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {

	private Context context;
	private int layout;
	private Order source;
	private DecimalFormat df = new DecimalFormat("0.00");
	private UpdateListener notice;
	private LayoutParams layoutParams;

	public interface UpdateListener {

		public void Update(Order o);
	}

	public OrderAdapter(Context context, int layout, Order o, UpdateListener l) {

		this.context = context;
		this.layout = layout;
		source = o;
		notice = l;
		
		final float scale = context.getResources().getDisplayMetrics().density;
		int width = (int) (100 * scale + 0.5f);
		
		layoutParams = new LayoutParams(width,LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
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
		TextView txtPrice;
		TextView txtSub;
		LinearLayout linLayout;
		Button btCancel;
		Button btAdd;
		Button btSubstr;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		OrderUnit s = source.getUnit(position);
		ViewHolder holder;

		// Log.d("adapter",holder.txtAdapter.toString()+" "+watcher.toString());

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(layout, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView
					.findViewById(R.orderRow.article);
			holder.txtPrice = (TextView) convertView
					.findViewById(R.orderRow.price);
			holder.txtSub = (TextView) convertView.findViewById(R.orderRow.sub);
			holder.btCancel = (Button) convertView
					.findViewById(R.orderRow.cancel);
			holder.btAdd = (Button) convertView
					.findViewById(R.orderRow.addition);
			holder.btSubstr = (Button) convertView
					.findViewById(R.orderRow.substract);
			holder.linLayout = (LinearLayout) convertView.findViewById(R.orderRow.lin_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		EditText et = new EditText(context);
		et.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		et.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		et.setLayoutParams(layoutParams);
		et.setHint("NumPad");
		
		holder.linLayout.removeViewAt(1);
		holder.linLayout.addView(et, 1);
		
		holder.txtName.setText(s.getArticle().getName());
		holder.txtPrice.setText(df.format(s.getArticle().getPrice()));
		holder.txtSub.setText(df.format(s.getSubtotal()));
		holder.btCancel.setOnClickListener(new deleteAdapter(et));
		holder.btAdd.setOnClickListener(new additionAdapter(position, et));
		holder.btSubstr
				.setOnClickListener(new substractionAdapter(position, et));

		TextAdapter watcher = new TextAdapter(position, holder.txtSub, notice);

		et.addTextChangedListener(watcher);

		Log.d("getView", "Position: " + position);

		return convertView;

	}

	private class deleteAdapter implements OnClickListener {

		EditText amount;

		public deleteAdapter(EditText txtAmount) {
			amount = txtAmount;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			amount.setText("0");
		}
	}

	private class additionAdapter implements OnClickListener {

		int position;
		EditText amount;

		public additionAdapter(int pos, EditText txtAmount) {
			amount = txtAmount;
			this.position = pos;

		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			amount.setText((source.getUnit(position).getAmount() + 1) + "");
		}
	}

	private class substractionAdapter implements OnClickListener {

		int position;
		EditText amount;

		public substractionAdapter(int pos, EditText txtAmount) {
			amount = txtAmount;
			this.position = pos;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			amount.setText((source.getUnit(position).getAmount() - 1) + "");
		}
	}

	private class TextAdapter implements TextWatcher {

		int position;
		UpdateListener l;
		private TextView subTotal;

		public TextAdapter(int pos, TextView subTotal, UpdateListener l) {
			this.subTotal = subTotal;
			this.position = pos;
			this.l = l;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			int amount = 0;
			try {
				amount = Integer.parseInt(s.toString());
			} catch (NumberFormatException e) {
			}
			if (amount != source.getUnit(position).getAmount()) {
				source.setAmount(position, amount);
				subTotal.setText(df.format(source.getUnit(position)
						.getSubtotal()));
				l.Update(source);
			}

			Log.d("onText", "pos: " + position + " s: " + s.toString()
					+ " amount: " + amount);

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	}
}