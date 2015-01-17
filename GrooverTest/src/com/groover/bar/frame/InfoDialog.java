package com.groover.bar.frame;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class InfoDialog extends AlertDialog {

	private String message;

	protected InfoDialog(Context context) {
		super(context);
	}

	public InfoDialog(Context context, String m) {
		super(context);
		message = m;
	}

	protected Dialog onCreateDialog(int id) {
		Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(message);
		AlertDialog dialog = builder.create();
		return builder.create();
	}

}