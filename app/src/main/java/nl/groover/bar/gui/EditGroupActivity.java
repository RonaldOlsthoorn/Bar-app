package nl.groover.bar.gui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import nl.groover.bar.R;

public class EditGroupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
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
