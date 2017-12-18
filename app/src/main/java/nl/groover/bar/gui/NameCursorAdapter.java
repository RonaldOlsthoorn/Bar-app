package nl.groover.bar.gui;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import nl.groover.bar.R;

/**
 * Created by Ronald Olsthoorn on 4/30/2017.
 */

public class NameCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public NameCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflater.inflate(R.layout.ledenlijstrow2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextField = (TextView) view.findViewById(R.id.ledenlijstrow2_naam);

        String firstName = cursor.getString(1);
        String prefix = cursor.getString(2);
        String lastName = cursor.getString(3);

        String name;

        if (prefix.equals("") || prefix==null) {
            name = firstName + " " + lastName;
        } else {
            name = firstName + " " + prefix + " " + lastName;
        }

        nameTextField.setText(name);

        return;
    }
}
