package nl.groover.bar.gui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.EditGroupListAdapter;

public class GroupActivity extends Activity {

    public static final int REQUEST_CODE_EDIT=1234;
    public static final int REQUEST_CODE_NEW=12345;
    private EditGroupListAdapter adapter;
    private Cursor cursor;
    private DBHelper DB;

    private EditListener editListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        DB = DBHelper.getDBHelper(this);

        editListener = new EditListener() {
            @Override
            public void editGroup(int id, String name) {
                Intent intent = new Intent(GroupActivity.this, EditGroupActivity.class);
                intent.putExtra("request_code", REQUEST_CODE_EDIT);
                intent.putExtra("group_id", id);
                intent.putExtra("group_name", name);

                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        };

        cursor = DB.getAllGroups();
        adapter = new EditGroupListAdapter(this, cursor, EditGroupListAdapter.FLAG_REGISTER_CONTENT_OBSERVER, editListener);

        ListView groupList = (ListView) findViewById(R.group_activity.group_list);
        groupList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (resultCode == RESULT_OK) {
                cursor = DB.getAllGroups();
                adapter.swapCursor(cursor);
                adapter.notifyDataSetChanged();
            }
    }

    public interface EditListener{

        void editGroup(int id, String name);
    }

    public void newGroup(View v){

        Intent intent = new Intent(GroupActivity.this, EditGroupActivity.class);
        intent.putExtra("request_code", REQUEST_CODE_NEW);

        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }
}
