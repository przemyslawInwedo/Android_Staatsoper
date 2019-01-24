package app.nunc.com.staatsoperlivestreaming.apis;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexALFactory;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexPlayer.NexProperty;

public class About extends AppCompatActivity {
	private static final String LOG_TAG = "About";
	
	//player
	private NexPlayer mTempPlayer;
	private NexALFactory mTempALFactory;
	private WebView mWebView;
	
	private String mAboutHTML;
	private String mVersion;
	private String mModelName;
	private String mBuildVersion;
	
	private RelativeLayout mParentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		setupParentLayout();
		setTempPlayer();
		setSDKInfo();
	}

	private void setSDKInfo() {
		setVersion();
		setPlayerInfo();
		setWebView();
		setContentView(mParentLayout);
	}

	private void setupParentLayout() {
		mParentLayout = new RelativeLayout(this);
		mParentLayout.setBackgroundColor(Color.WHITE);
	}
	
	private void setWebView() {
		mWebView = new WebView(this);
		mParentLayout.addView(mWebView);
		Log.e(LOG_TAG, "5 " +mAboutHTML );
		mWebView.loadData(mAboutHTML, "text/html", "utf-9");
		mWebView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
	}

	private void setPlayerInfo() {
        mTempPlayer.setLicenseFile("/sdcard/test_lic.xml");
		if(mTempPlayer.init(this, 0))
		{			
			String sdkname = mTempPlayer.getSDKName();
			String strLockStart = mTempPlayer.getStringProperty(NexProperty.LOCK_START_DATE);
			String strLockEnd = mTempPlayer.getStringProperty(NexProperty.LOCK_END_DATE);
			Log.v("2", "2" + sdkname +" = " + strLockStart + " - " +strLockEnd);
						
			mAboutHTML = new String();
			
			mAboutHTML = mAboutHTML + "<html><head>  </head><body>";
			mAboutHTML = mAboutHTML + "<p style='margin-top:16pt; text-align:center; font-size:14pt; font-weight: bold;'>NexPlayer&trade;</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>NexPlayer Version: " + getString(R.string.app_name) + "</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>SDK Version: " + mVersion + "</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>SDK Name: " + sdkname + " </p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>Model Name: " + mModelName + " </p>";
			if(strLockStart.compareTo("0") != 0)
				mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>License Start Date: " + strLockStart + " </p>";
			if(strLockEnd.compareTo("0") != 0)
				mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>License End Date: " + strLockEnd + " </p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>Platform Version: " + mBuildVersion + " </p>";
			

			mAboutHTML = mAboutHTML + "</body></html>";
			mTempPlayer.release();
		}
		else
		{
			mAboutHTML = mAboutHTML + "<html><head></head><body>";
			
			mAboutHTML = mAboutHTML + "<p style='margin-top:16pt; text-align:center; font-size:14pt; font-weight: bold;'>NexPlayer&trade;</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>NexPlayer Version: " + getString(R.string.app_name) + "</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>SDK Version: " + mVersion + "</p>";
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>Model Name: " + mModelName + " </p>";			
			mAboutHTML = mAboutHTML + "<p style='text-align:center; font-size:12pt; '>Platform Version: " + mBuildVersion + " </p>";
			mAboutHTML = mAboutHTML + "<br><p style='text-align:center; font-size:14pt; color:red'>Warning!  SDK is not initialized. </p>";
			mAboutHTML = mAboutHTML + "</body></html>";			
		}
		    mTempALFactory.release();
	}

	private void setVersion() {
		 mVersion = mTempPlayer.getVersion(0) + "." +
				 	mTempPlayer.getVersion(1) + "." +
				 	mTempPlayer.getVersion(2) + "." +
				 	mTempPlayer.getVersion(3);
		 
		 Log.i(LOG_TAG, "1" + mVersion);
		 
		 mModelName = android.os.Build.MODEL;
		 mBuildVersion = android.os.Build.VERSION.RELEASE;
	}

	private void setTempPlayer() {
		mTempPlayer = new NexPlayer();
		mTempALFactory  = new NexALFactory();
		
		mTempALFactory.init(this, null, null, 0, 4);
		mTempPlayer.setNexALFactory(mTempALFactory);
	}
	
}

