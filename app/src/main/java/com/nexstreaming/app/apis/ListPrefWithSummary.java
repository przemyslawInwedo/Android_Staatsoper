package com.nexstreaming.app.apis;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

public class ListPrefWithSummary extends ListPreference {

	public ListPrefWithSummary(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		setSummary(getEntry());
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		setSummary(getEntry());
	}
	

}
