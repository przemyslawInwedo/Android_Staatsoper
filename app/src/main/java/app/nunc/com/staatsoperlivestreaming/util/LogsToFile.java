package app.nunc.com.staatsoperlivestreaming.util;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LogsToFile implements Runnable {
	private static final String LOG_TAG = "LogsToFile";
	private static final String TXT = ".txt";
	private Process mCleanProcess = null;
	private Process mLogProcess = null;
	private File mFileParentFolder = null;
	private String mFileNameBase =  "%s_%s_log";
	private int mFileIndex = 0;
	private File mOutputFile = null;
	private static final int MAX_LENGTH_OF_LOG_FILE = 50000000;
	public static final String PARENT_PATH_OF_LOG_FILE = Environment.getExternalStorageDirectory().getPath() + "/NexPlayerSample/Logs/";

	public LogsToFile() {
		mFileParentFolder = new File(PARENT_PATH_OF_LOG_FILE);
		if (!mFileParentFolder.exists()) {
			mFileParentFolder.mkdirs();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String currentDateAndTime = sdf.format(new Date());

		mFileNameBase = String.format(mFileNameBase, currentDateAndTime, Build.MODEL);
		mFileNameBase = mFileNameBase.replace(" ","_");
		mFileNameBase = mFileNameBase.replace("\n","_");
		mFileIndex = 0;

		mOutputFile = new File(mFileParentFolder.getAbsolutePath(), getFileName(mFileNameBase, mFileIndex));
		Log.d(LOG_TAG, "LogsToFile : " + mOutputFile.getAbsolutePath());
	}

	private String getFileName(String base, int index) {
		return index == 0 ? base + TXT : base + "_" + index + TXT;
	}

	@Override
	public void run() {
		clean();

		try {
			mLogProcess = Runtime.getRuntime().exec("logcat -f " + mOutputFile.getAbsolutePath());
			mTimer = new Timer();
			mTimer.schedule(new FileSizeCheckingTask(), 0, 2000);
	    }
		catch (IOException e) {
			Log.e(LOG_TAG, "File Create Fail");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		kill();
		super.finalize();
	}

	public void clean() {
		try {
			mCleanProcess = Runtime.getRuntime().exec("logcat -c ");
		}
		catch (IOException e) {
			Log.e("LogsToFile", "File Create Fail");
		}
	}

	public void kill() {
		if (mLogProcess != null) {
			mLogProcess.destroy();
		}

		if (mCleanProcess != null) {
			mCleanProcess.destroy();
		}

		if( mTimer != null ) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private Timer mTimer = null;
	private class FileSizeCheckingTask extends TimerTask {
		@Override
		public void run() {
			if( mOutputFile != null && mOutputFile.exists() ) {
				long length = mOutputFile.length();
				if( length > MAX_LENGTH_OF_LOG_FILE ) {
					mFileIndex++;
					mOutputFile = new File(mFileParentFolder.getAbsolutePath(), getFileName(mFileNameBase, mFileIndex));
					Log.d(LOG_TAG, "LogsToFile : " + mOutputFile.getAbsolutePath());
					kill();
					LogsToFile.this.run();
				}
			}
		}
	}
}
