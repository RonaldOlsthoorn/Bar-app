package nl.groover.bar.gui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import nl.groover.bar.R;
import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.FilteredCursor;


public class EditGroupActivity extends FragmentActivity {

    DBHelper DB;
    FilteredCursor allMembers;
    FilteredCursor group;

    ListView listMembers;
    ListView listGroup;

    NameCursorAdapter adapterMembers;
    NameCursorAdapter adapterGroup;

    int groupId;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        DB = DBHelper.getDBHelper(this);

        allMembers = new FilteredCursor(DB.getMembers());
        allMembers.setAll();
        group = allMembers.mirrorCursor();

        listMembers = (ListView) findViewById(R.id.edit_group_activity_list_members);
        listGroup = (ListView) findViewById(R.id.edit_group_activity_list_group);

        adapterMembers  = new NameCursorAdapter(this, allMembers, NameCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapterGroup = new NameCursorAdapter(this, group, NameCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listMembers.setAdapter(adapterMembers);
        listGroup.setAdapter(adapterGroup);

        requestCode = getIntent().getIntExtra("request_code", 0);

        if(requestCode==GroupActivity.REQUEST_CODE_EDIT){
            groupId = getIntent().getIntExtra("group_id", 0);
            String groupName = getIntent().getStringExtra("group_name");
            ((EditText) findViewById(R.id.edit_group_activity_edit_name)).setText(groupName);
            Cursor g = DB.getGroupMembers(groupId);

            g.moveToFirst();

            while(g.getPosition()<g.getCount()){

                group.addId(g.getInt(0));
                allMembers.filterId(g.getInt(0));
                g.moveToNext();
            }
        }

        listMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                allMembers.filter(position);
                group.addId((int) id); // unsafe long to int cast. Assume id's do not get this big in this lifetime.

                adapterMembers.notifyDataSetChanged();
                adapterGroup.notifyDataSetChanged();
            }
        });

        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                group.filter(position);
                allMembers.addId((int) id); // unsafe long to int cast. Assume id's do not get this big in this lifetime.

                adapterMembers.notifyDataSetChanged();
                adapterGroup.notifyDataSetChanged();
            }
        });
    }

    public void save(View v){

        String name = ((EditText) findViewById(R.id.edit_group_activity_edit_name)).getText().toString();
        if(name==null ||name.equals("")){
            ((EditText) findViewById(R.id.edit_group_activity_edit_name)).setError("This field cannot be empty!!");
        }else if (group.getCount()==0) {

            BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("Select at least one member!");
            dialog.show(getSupportFragmentManager(), "empty group");

        }else {

            if (requestCode == GroupActivity.REQUEST_CODE_EDIT) {

                if (!DB.checkAllowedToUpdateGroup(groupId)) {
                    BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("Something went wrong. Check if group balance is zero!");
                    dialog.show(getSupportFragmentManager(), "error_db");
                }else {

                    DB.renameGroup(groupId, name);
                    DB.deleteGroupMembers(groupId);

                    group.moveToFirst();

                    while (group.getPosition() < group.getCount()) {

                        DB.addGroupMember(groupId, group.getInt(0));
                        group.moveToNext();
                    }

                    Intent intent = new Intent(this, GroupActivity.class);

                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, intent);
                    }

                    finish();
                }
            } else {

                groupId = (int) DB.createGroup(name);

                if(groupId==-1){

                    BasicAlertDialogFragment dialog = new BasicAlertDialogFragment("Group name already in use!");
                    dialog.show(getSupportFragmentManager(), "error_db");
                }else{
                    group.moveToFirst();

                    while (group.getPosition() < group.getCount()) {

                        DB.addGroupMember(groupId, group.getInt(0));
                        group.moveToNext();
                    }

                    Intent intent = new Intent(this, GroupActivity.class);

                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, intent);
                    }
                    finish();

                }

            }
        }
    }

    public void cancel(View v){

        Intent intent = new Intent(this, GroupActivity.class);

        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED, intent);
        } else {
            getParent().setResult(Activity.RESULT_CANCELED, intent);
        }
        finish();
    }
}