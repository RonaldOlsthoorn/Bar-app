package nl.groover.bar.frame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import nl.groover.bar.R;
import nl.groover.bar.gui.GroupActivity;

/**
 * Created by Ronald Olsthoorn on 5/4/2017.
 */

public class GroupListAdapter extends CursorAdapter {

    private DBHelper DB;

    private LayoutInflater mInflater;
    private DecimalFormat df = new DecimalFormat("0.00");
    private GroupActivity.EditListener editListener;

    public GroupListAdapter(Context context, Cursor c, int flags, GroupActivity.EditListener editListener){

        super(context, c, flags);

        this.editListener = editListener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DB = DBHelper.getDBHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflater.inflate(R.layout.ledenlijstrow2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView nameTextField = (TextView) view.findViewById(R.group_list_row.name);
        nameTextField.setText(cursor.getString(1));

        TextView balanceTextField = (TextView) view.findViewById(R.group_list_row.account);
        double balance = cursor.getDouble(3);
        balanceTextField.setText(df.format(balance));

        if(balance!=0) {

            Button editButton = (Button) view.findViewById(R.group_list_row.edit);
            editButton.setOnClickListener(new EditButtonListener(cursor.getInt(0)));
            editButton.setEnabled(true);

            Button deleteButton = (Button) view.findViewById(R.group_list_row.delete);
            deleteButton.setOnClickListener(new DeleteListener(cursor.getInt(0)));
            deleteButton.setEnabled(true);
        }

        return;
    }

    private class EditButtonListener implements View.OnClickListener {

        int id;

        public EditButtonListener(int id){

            this.id = id;
        }

        @Override
        public void onClick(View v) {

            editListener.editGroup(id);
        }
    }

    private class DeleteListener implements View.OnClickListener {

        int id;

        public DeleteListener(int id){

            this.id = id;
        }

        @Override
        public void onClick(View v) {

            try {
                DB.deleteGroup(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }
    }
}
