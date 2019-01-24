package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.util.LogsToFile;


public class CommonMenuItems {

	private static LogsToFile mLogsToFile = null;

	public static void addCommonMenuItem(Menu menu, String title, boolean enabled) {
		menu.add(title).setEnabled(enabled);
	}

	public static boolean onOptionsItemSelected(Context context, MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent intent = new Intent(context, NexPlayerPrefs.class);
				context.startActivity(intent);
				break;
			case R.id.action_about:
				Intent settingsActivity = new Intent(context, About.class);
				context.startActivity(settingsActivity);
				break;
			case R.id.action_log:
				if( item.getTitle().equals(context.getString(R.string.start_log)) ) {
					mLogsToFile = new LogsToFile();
					mLogsToFile.run();
					item.setTitle(context.getString(R.string.stop_log));
				} else {
					mLogsToFile.kill();
					mLogsToFile = null;
					item.setTitle(context.getString(R.string.start_log));
				}
				break;
			default:
				ret = false;
		}
		return ret;
	}
}
