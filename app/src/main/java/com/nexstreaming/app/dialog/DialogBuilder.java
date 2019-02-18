package com.nexstreaming.app.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

public class DialogBuilder {

	public enum DialogMode {
		SET_VIEW, SET_ITEMS, SET_MESSAGE
	}

	public static AlertDialog makeDialog(Context context, int title, DialogMode mode, int res, DialogInterface.OnClickListener l, WindowManager.LayoutParams layoutParams) {
		return makeDialog(context, context.getString(title), mode, res, l, layoutParams);
	}

	public static AlertDialog makeDialog(Context context, String title, DialogMode mode, int res, DialogInterface.OnClickListener l, WindowManager.LayoutParams layoutParams) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		switch (mode) {
			case SET_VIEW:
				builder.setView(res);
				break;
			case SET_ITEMS:
				builder.setItems(res, l);
				break;
			case SET_MESSAGE:
				builder.setMessage(res);
				break;
		}
		AlertDialog dialog = builder.create();

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		if( layoutParams != null ) {
			params = layoutParams;
		} else {
			params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		}
		dialog.getWindow().setAttributes(params);
		return dialog;
	}

	public static AlertDialog makeMessageDialog(Context context, int title, String msg, WindowManager.LayoutParams layoutParams) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		AlertDialog dialog = builder.create();

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		if( layoutParams != null ) {
			params = layoutParams;
		} else {
			params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		}
		dialog.getWindow().setAttributes(params);
		return dialog;
	}
}