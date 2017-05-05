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
import android.widget.Toast;

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
    private Context context;

    public GroupListAdapter(Context context, Cursor c, int flags, GroupActivity.EditListener editListener){
        super(context, c, flags);

        this.context = context;
        this.editListener = editListener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DB = DBHelper.getDBHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflater.inflate(R.layout.group_list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.group_list_row_name);
        nameTextView.setText(cursor.getString(1));

        TextView balanceTextField = (TextView) view.findViewById(R.id.group_list_row_account);
        double balance = cursor.getDouble(3);
        balanceTextField.setText(df.format(balance));

        if(balance==0) {

            Button editButton = (Button) view.findViewById(R.id.group_list_row_edit);
            editButton.setOnClickListener(
                    new EditButtonListener(cursor.getInt(0), cursor.getString(1)));
            editButton.setEnabled(true);

            Button deleteButton = (Button) view.findViewById(R.id.group_list_row_delete);
            deleteButton.setOnClickListener(new DeleteListener(cursor.getInt(0), cursor.getString(1)));
            deleteButton.setEnabled(true);
        }

        return;
    }

    private class EditButtonListener implements View.OnClickListener {

        int id;
        String name;

        public EditButtonListener(int id, String name){

            this.id = id;
            this.name = name;
        }

        @Override
        public void onClick(View v) {

            editListener.editGroup(id, name);
        }
    }

    private class DeleteListener implements View.OnClickListener {

        int id;
        String name;

        public DeleteListener(int id, String name){

            this.id = id;
            this.name = name;
        }

        @Override
        public void onClick(View v) {

            try {
                DB.deleteGroup(id);
                Toast.makeText(context, "Deleted group "+name, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            swapCursor(DB.getAllGroups());
            notifyDataSetChanged();
        }
    }
}
