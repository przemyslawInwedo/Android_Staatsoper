package com.nexstreaming.app.apis;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import app.nunc.com.staatsoperlivestreaming.R;
import com.nexstreaming.nexplayerengine.NexStatisticsMonitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.nexstreaming.nexplayerengine.NexStatisticsMonitor.HttpStatisticsMetric;
import static com.nexstreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_GENERAL;
import static com.nexstreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_HTTP;
import static com.nexstreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_INITIAL;
import static com.nexstreaming.nexplayerengine.NexStatisticsMonitor.STATISTICS_SYSTEM;

public class StatisticsDialog {
	public static final Handler mHandler = new Handler();

	private Object[][][] mArrayHttpStatistic;
	private TabbedStatisticsDialog mDlg;

	private final int INVALID_ARRAY_INDEX = -1;

	public StatisticsDialog(Activity activity) {
		mDlg = new TabbedStatisticsDialog(activity);
		mDlg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		mArrayHttpStatistic = new Object[NexStatisticsMonitor.HttpStatisticsMetric.values().length][NexStatisticsMonitor.HttpStatisticsParamKey.values().length][1];

		for (int i = 0; i< NexStatisticsMonitor.HttpStatisticsMetric.values().length; i++) {
			for(int j = 0; j< NexStatisticsMonitor.HttpStatisticsParamKey.values().length; j++) {
				mArrayHttpStatistic[i][j][0] = null;
			}
		}
	}

	private int getArrIndex(NexStatisticsMonitor.HttpStatisticsMetric metric) {
		switch( metric ) {
			case DOWN_START :       return 0;
			case CONNECT :          return 1;
			case CONNECTED :        return 2;
			case HEADER_RECEIVED :  return 3;
			case DATA_RECEIVED :    return 4;
			case DOWN_END :         return 5;
			case ERROR :            return 6;
		}
		return INVALID_ARRAY_INDEX;
	}

	private int getArrIndex(NexStatisticsMonitor.HttpStatisticsParamKey param) {
		switch(param) {
			case RESOURCE_URL :         return 0;
			case FILE_TYPE :            return 1;
			case SEG_NO :               return 2;
			case SEG_DURATION :         return 3;
			case TRACK_BW :             return 4;
			case MEDIA_COMPOSITION :    return 5;
			case BYTE_RECEIVED :        return 6;
			case CONTENT_LENGTH :       return 7;
			case ERROR_CODE :           return 8;
		}
		return INVALID_ARRAY_INDEX;
	}

	private String getMetricTypeString(int arrIndex) {
		switch( arrIndex ) {
			case 0: return HttpStatisticsMetric.DOWN_START.toString();
			case 1: return HttpStatisticsMetric.CONNECT.toString();
			case 2: return HttpStatisticsMetric.CONNECTED.toString();
			case 3: return HttpStatisticsMetric.HEADER_RECEIVED.toString();
			case 4: return HttpStatisticsMetric.DATA_RECEIVED.toString();
			case 5: return HttpStatisticsMetric.DOWN_END.toString();
			case 6: return HttpStatisticsMetric.ERROR.toString();
		}
		return null;
	}

	private String getHttpStatisticParamString(int arrIndex) {
		switch( arrIndex ) {
			case 0 : return NexStatisticsMonitor.HttpStatisticsParamKey.RESOURCE_URL.toString();
			case 1 : return NexStatisticsMonitor.HttpStatisticsParamKey.FILE_TYPE.toString();
			case 2 : return NexStatisticsMonitor.HttpStatisticsParamKey.SEG_NO.toString();
			case 3 : return NexStatisticsMonitor.HttpStatisticsParamKey.SEG_DURATION.toString();
			case 4 : return NexStatisticsMonitor.HttpStatisticsParamKey.TRACK_BW.toString();
			case 5 : return NexStatisticsMonitor.HttpStatisticsParamKey.MEDIA_COMPOSITION.toString();
			case 6 : return NexStatisticsMonitor.HttpStatisticsParamKey.BYTE_RECEIVED.toString();
			case 7 : return NexStatisticsMonitor.HttpStatisticsParamKey.CONTENT_LENGTH.toString();
			case 8 : return NexStatisticsMonitor.HttpStatisticsParamKey.ERROR_CODE.toString();
		}
		return null;
	}

	public void onUpdated(int statisticType, HashMap<NexStatisticsMonitor.IStatistics, Object> map) {
		if( statisticType == STATISTICS_GENERAL) {
			printGeneralStatistic( map );
		}
		else if( statisticType == STATISTICS_INITIAL) {
			printInitialStatistic( map );
		}
		else if( statisticType == STATISTICS_HTTP) {
			printHttpStatistic( map );
		}
		else if( statisticType == STATISTICS_SYSTEM) {
			printSystemStatistic(map);
		}

	}

	private void printSystemStatistic( HashMap<NexStatisticsMonitor.IStatistics, Object> map ) {
		String str = "\n";
		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();

			NexStatisticsMonitor.SystemStatisticsMetric key = (NexStatisticsMonitor.SystemStatisticsMetric)entry.getKey();
			Object value = entry.getValue();

			if( key == NexStatisticsMonitor.SystemStatisticsMetric.CPU_USAGE ) {
				str += ( key.toString() + " : " + getString( new Double( ( (Double)value * 100 ) ).intValue() ) + " %\n\n" );
			}
			else if( key == NexStatisticsMonitor.SystemStatisticsMetric.FREE_MEMORY_KB ) {
				str += ( key.toString() + " : " + getString(value) + " KB\n\n" );
			}
		}
		mDlg.printSystemStatistic(str);
	}

	private void printGeneralStatistic( HashMap<NexStatisticsMonitor.IStatistics, Object> map ) {

		String str = "\n";
		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();

			NexStatisticsMonitor.GeneralStatisticsMetric key = (NexStatisticsMonitor.GeneralStatisticsMetric)entry.getKey();
			Object value = entry.getValue();

			str += ( key.toString() + " : " + getString(value) + "\n\n" );
		}
		mDlg.printGeneralStatistic(str);
	}

	private void printInitialStatistic( HashMap<NexStatisticsMonitor.IStatistics, Object> map ) {

		String str = "\n";
		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();

			NexStatisticsMonitor.InitialStatisticsMetric key = (NexStatisticsMonitor.InitialStatisticsMetric)entry.getKey();
			Object value = entry.getValue();

			str += ( key.toString() + "\n" + getString(value) + "\n\n" );
		}
		mDlg.printInitialStatistic(str);
	}

	private void printHttpStatistic( HashMap<NexStatisticsMonitor.IStatistics, Object> map ) {

		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();

			HttpStatisticsMetric key = (HttpStatisticsMetric)entry.getKey();
			Object value = entry.getValue();

			Iterator it2 = ((HashMap<NexStatisticsMonitor.HttpStatisticsParamKey, Object>)value).entrySet().iterator();

			while (it2.hasNext()) {
				Map.Entry entry2 = (Map.Entry) it2.next();

				NexStatisticsMonitor.HttpStatisticsParamKey paramKey = (NexStatisticsMonitor.HttpStatisticsParamKey) entry2.getKey();
				Object paramVal = entry2.getValue();

				int keyIdx = getArrIndex(key);
				int paramKeyIdx = getArrIndex(paramKey);

				if( keyIdx != INVALID_ARRAY_INDEX && paramKeyIdx != INVALID_ARRAY_INDEX ) {
					mArrayHttpStatistic[keyIdx][paramKeyIdx][0] = paramVal;
				}
			}
			mDlg.printHttpStatistic( getHttpStatisticString() );
		}
	}

	private String getString(Object value ) {
		if( (value instanceof Integer) == true ) {
			return Integer.toString((Integer) value);
		}
		else if( (value instanceof Long) == true ) {
			return Long.toString ((Long)value );
		}
		else if ( (value instanceof String) == true ) {
			return (String)value;
		}
		else if ( (value instanceof Double) == true ) {
			return Double.toString((Double)value);
		}
		else if ( (value instanceof NexStatisticsMonitor.FileType) == true ) {
			return Integer.toString(((NexStatisticsMonitor.FileType) value).getCode());
		}

		return null;
	}

	private String getHttpStatisticString() {
		String str = "";

		for(int i = 0; i< HttpStatisticsMetric.values().length; i++) {
			str += "\n" + getMetricTypeString(i) + "\n";
			for(int j = 0; j< NexStatisticsMonitor.HttpStatisticsParamKey.values().length; j++) {
				if( mArrayHttpStatistic[i][j][0] != null ) {
					str += getHttpStatisticParamString(j) + " : " + getString(mArrayHttpStatistic[i][j][0]) + "\n";
				}
			}
		}
		return ( (str=="") ? null : str );
	}

	public void show() {
		if( !mDlg.isShowing() ) {
			mDlg.show();
		}
	}

	public void dismiss() {
		if( mDlg.isShowing() ) {
			mDlg.dismiss();
		}
	}

	private class TabbedStatisticsDialog extends Dialog {

		private TextView mInitialStatisticView;
		private TextView mGeneralStatisticView;
		private TextView mHttpStatisticView;
		private TextView mSystemStatisticView;

		TabbedStatisticsDialog(Activity activity) {
			super( activity );
			setTitle("Statistics");
			setContentView(R.layout.statistic_tab_layout);

			TabHost tabHost = (TabHost)findViewById(R.id.TabHost01);
			tabHost.setup();

			TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
			spec1.setIndicator("Initial");

			mInitialStatisticView = (TextView) findViewById(R.id.TextView01);
			spec1.setContent( mInitialStatisticView.getId() );
			tabHost.addTab(spec1);

			TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
			spec2.setIndicator("General");

			mGeneralStatisticView = (TextView) findViewById(R.id.TextView02);
			spec2.setContent( mGeneralStatisticView.getId() );
			tabHost.addTab(spec2);

			TabHost.TabSpec spec3 = tabHost.newTabSpec("tab3");
			spec3.setIndicator("Http");

			mHttpStatisticView = (TextView) findViewById(R.id.TextView03);
			spec3.setContent( mHttpStatisticView.getId() );
			tabHost.addTab(spec3);

			TabHost.TabSpec spec4 = tabHost.newTabSpec("tab4");
			spec4.setIndicator("System");

			mSystemStatisticView = (TextView) findViewById(R.id.TextView04);
			spec4.setContent( mSystemStatisticView.getId() );
			tabHost.addTab(spec4);
		}

		private void printHttpStatistic(final String str) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mHttpStatisticView.setText(str);
				}
			});
		}

		private void printGeneralStatistic(final String str) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mGeneralStatisticView.setText(str);
				}
			});
		}

		private void printInitialStatistic(final String str) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mInitialStatisticView.setText(str);
				}
			});
		}

		private void printSystemStatistic(final String str) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mSystemStatisticView.setText(str);
				}
			});
		}
	}
}

