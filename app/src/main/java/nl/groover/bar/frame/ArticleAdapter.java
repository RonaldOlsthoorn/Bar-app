package nl.groover.bar.frame;

import java.text.DecimalFormat;
import java.util.ArrayList;

import nl.groover.bar.R;
import nl.groover.bar.gui.ArticleActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ArticleAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private int layout = R.layout.article_row;

	private DecimalFormat df = new DecimalFormat("0.00");
	private ArrayList<Article> source;

	private ArticleActivity.MoveUpListener moveUpListener;
	private ArticleActivity.MoveDownListener moveDownListener;
	private ArticleActivity.EditArticleListener editArticleListener;
	private ArticleActivity.RemoveArticleListener removeArticleListener;

	public ArticleAdapter(Context context, ArrayList<Article> aList,
						  ArticleActivity.MoveUpListener moveUp,
						  ArticleActivity.MoveDownListener moveDown,
						  ArticleActivity.EditArticleListener edit,
						  ArticleActivity.RemoveArticleListener remove) {

		source = aList;
		moveUpListener = moveUp;
		moveDownListener = moveDown;
		editArticleListener = edit;
		removeArticleListener = remove;

		mInflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return source.size();
	}

	@Override
	public Object getItem(int position) {
		return source.get(position);
	}

	@Override
	public long getItemId(int position) {
		return source.get(position).getId();
	}

	private class ViewHolder {

		TextView txtName;
		TextView txtPrice;
		Button btUp;
		Button btDown;
		Button btDelete;
		Button btEdit;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(layout, null);
			holder = new ViewHolder();
			holder.txtName = (TextView) convertView.findViewById(R.id.article_row_name);
			holder.txtPrice = (TextView) convertView.findViewById(R.id.article_row_price);
			holder.btUp = (Button) convertView.findViewById(R.id.article_row_up);
			holder.btDown = (Button) convertView.findViewById(R.id.article_row_down);
			holder.btEdit = (Button) convertView.findViewById(R.id.article_row_edit);
			holder.btDelete = (Button) convertView.findViewById(R.id.article_row_delete);

			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setBackgroundColor(source.get(position).getColor());
		holder.txtName.setText(source.get(position).getName());
		holder.txtPrice.setText(df.format(source.get(position).getPrice()));

		if(position!=0){
			holder.btUp.setOnClickListener(new MoveUpAdapter(position,
					moveUpListener));
		}

		if(position != (source.size()-1)){
			holder.btDown.setOnClickListener(new MoveDownAdapter(position,
					moveDownListener));
		}

		holder.btEdit.setOnClickListener(new EditAdapter(position,
				editArticleListener));

        holder.btDelete.setOnClickListener(new RemoveAdapter(position,
                removeArticleListener));

		return convertView;
	}

	private class RemoveAdapter implements OnClickListener {

		ArticleActivity.RemoveArticleListener removeListener;
		int position;

		public RemoveAdapter(int pos, ArticleActivity.RemoveArticleListener l) {
			removeListener = l;
			position = pos;
		}

		@Override
		public void onClick(View arg0) {
			removeListener.removeArticle(position);
		}
	}

	private class EditAdapter implements OnClickListener {

		ArticleActivity.EditArticleListener editListener;
		int position;

		public EditAdapter(int pos,  ArticleActivity.EditArticleListener l) {
			editListener = l;
			position = pos;
		}

		@Override
		public void onClick(View button) {

			editListener.editArticle(position);
		}
	}

	private class MoveUpAdapter implements OnClickListener {

		int position;
		ArticleActivity.MoveUpListener moveUpListener;

		public MoveUpAdapter(int pos, ArticleActivity.MoveUpListener l) {
			moveUpListener = l;
			position = pos;
		}

		@Override
		public void onClick(View arg0) {
			moveUpListener.moveUp(position);
		}
	}

	private class MoveDownAdapter implements OnClickListener {

		int position;
		ArticleActivity.MoveDownListener moveDownListener;

		public MoveDownAdapter(int pos, ArticleActivity.MoveDownListener l) {
			moveDownListener = l;
			position = pos;
		}

		@Override
		public void onClick(View arg0) {
			moveDownListener.moveDown(position);
		}
	}

	public void swapArrayList(ArrayList<Article> l) {
		source = l;

	}
}
