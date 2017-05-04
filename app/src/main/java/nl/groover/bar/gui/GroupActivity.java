package nl.groover.bar.gui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import java.security.acl.Group;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.GroupListAdapter;

public class GroupActivity extends Activity {

    private int REQUEST_CODE=1234;
    private GroupListAdapter adapter;
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
            public void editGroup(int id) {
                Intent intent = new Intent(GroupActivity.this, EditGroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };

        cursor = DB.getAllGroups();
        adapter = new GroupListAdapter(this, cursor, GroupListAdapter.FLAG_REGISTER_CONTENT_OBSERVER, editListener);

        ListView groupList = (ListView) findViewById(R.group_activity.group_list);
        groupList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {

                adapter.notifyDataSetChanged();
            }
        }
    }

    public interface EditListener{

        void editGroup(int id);
    }

}
