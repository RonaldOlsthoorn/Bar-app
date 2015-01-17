package com.groover.bar.frame;

import java.text.DecimalFormat;
import com.groover.bar.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

public class ArticleAdapter extends BaseAdapter {

	public interface UpdateListener {
		public void delete(Article article);

		public void swap(int id1, int id2, int pos1, int pos2);

		public void edit(int pos, Article a);

		public void setEditable(int pos, Article a);
	}

	private Article[] articles;
	private DBHelper DB;
	private Context context;
	private DecimalFormat df = new DecimalFormat("0.00");
	private Cursor source;
	private UpdateListener notice;

	public ArticleAdapter(Context context, Cursor c, UpdateListener n) {
		this.context = context;

		source = c;
		notice = n;
		articles = new Article[c.getCount()];

		source.moveToFirst();
		while (source.getPosition() < source.getCount()) {
			articles[source.getPosition()] = new Article(source.getInt(0),
					source.getDouble(2), source.getString(1),
					source.getInt(3) < 1);
			source.moveToNext();
		}
	}

	@Override
	public int getCount() {
		return articles.length;
	}

	@Override
	public Object getItem(int position) {
		return articles[position];
	}

	@Override
	public long getItemId(int position) {
		return articles[position].getId();
	}

	private class ViewHolder {
		EditText txtName;
		EditText txtPrice;
		Button btDelete;
		Button btEdit;
		Button btUp;
		Button btDown;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Article a = (Article) getItem(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.article_row, null);
			holder = new ViewHolder();
			holder.txtName = (EditText) convertView
					.findViewById(R.articleRow.name);
			holder.txtPrice = (EditText) convertView
					.findViewById(R.articleRow.price);
			holder.btDelete = (Button) convertView
					.findViewById(R.articleRow.delete);
			holder.btEdit = (Button) convertView
					.findViewById(R.articleRow.edit);
			holder.btUp = (Button) convertView.findViewById(R.articleRow.up);
			holder.btDown = (Button) convertView
					.findViewById(R.articleRow.down);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtName.setText(a.getName());
		holder.txtPrice.setText(df.format(a.getPrice()));
		holder.btDelete.setOnClickListener(new deleteAdapter(a, notice));
		holder.btEdit.setOnClickListener(new editAdapter(position, holder.btUp,
				holder.btDown, holder.txtName, holder.txtPrice, a, notice));
		holder.btUp.setOnClickListener(new upAdapter(a, position, notice));
		holder.btDown.setOnClickListener(new downAdapter(a, position, notice));

		return convertView;
	}

	private class deleteAdapter implements OnClickListener {

		Article article;
		UpdateListener updateListener;

		public deleteAdapter(Article a, UpdateListener l) {
			updateListener = l;
			article = a;
		}

		@Override
		public void onClick(View arg0) {
			updateListener.delete(article);
		}
	}

	private class editAdapter implements OnClickListener {

		Article article;
		UpdateListener updateListener;
		EditText name;
		EditText price;
		Button up;
		Button down;
		int position;

		public editAdapter(int p, Button u, Button d, EditText txtName,
				EditText txtPrice, Article a, UpdateListener l) {
			updateListener = l;
			article = a;
			name = txtName;
			price = txtPrice;
			up = u;
			down = d;
			position = p;
		}

		@Override
		public void onClick(View button) {
			
			if(!article.getEditable()){
				updateListener.setEditable(position, article);
				return;
			}
			
			if (((Button) button).getText().toString().equals("Aanpassen")
					) {
							
				updateListener.setEditable(position, article);
				name.setEnabled(true);
				price.setEnabled(true);
				up.setEnabled(false);
				down.setEnabled(false);
				((Button) button).setText("Opslaan");
				return;
			}

			boolean checks = true;
			// CHECKS
			if (price.getText().toString() == null) {

				price.setError("Field must not be empty!");
				price.requestFocus();
				checks = false;
			} else {
				try {
					double prijs = Double.parseDouble(price.getText()
							.toString());
				} catch (NumberFormatException e) {
					price.setError("Field must be a number!");
					price.requestFocus();
					checks = false;
				}
			}

			if (name.getText().toString().trim().equals("")) {

				name.setError("Field must not be empty");
				name.requestFocus();
				checks = false;
			}

			if (checks) {
				article.setName(name.getText().toString());
				article.setPrice(Double.parseDouble(price.getText().toString()));
				updateListener.edit(article.getId(), article);
				((Button) button).setText("Aanpassen");
				up.setEnabled(true);
				down.setEnabled(true);
				name.setEnabled(false);
				price.setEnabled(false);
			}
		}
	}

	private class upAdapter implements OnClickListener {

		Article article;
		int position;
		UpdateListener updateListener;

		public upAdapter(Article a, int pos, UpdateListener l) {
			updateListener = l;
			article = a;
			position = pos;
		}

		@Override
		public void onClick(View arg0) {
			if (position != 0) {
				updateListener.swap(article.getId(),
						((Article) getItem(position - 1)).getId(), position,
						position - 1);
			}
		}
	}

	private class downAdapter implements OnClickListener {

		Article article;
		int position;
		UpdateListener updateListener;

		public downAdapter(Article a, int pos, UpdateListener l) {
			updateListener = l;
			article = a;
			position = pos;
		}

		@Override
		public void onClick(View arg0) {
			if (position != getCount() - 1) {
				updateListener.swap(article.getId(),
						((Article) getItem(position + 1)).getId(), position,
						position + 1);
			}
		}
	}

	public void swapCursor(Cursor c) {
		source = c;
		source.moveToFirst();
		articles = new Article[c.getCount()];

		while (c.getPosition() < c.getCount()) {
			articles[c.getPosition()] = new Article(c.getInt(0),
					c.getDouble(2), c.getString(1), c.getInt(3) < 1);
			c.moveToNext();
		}
	}
}
