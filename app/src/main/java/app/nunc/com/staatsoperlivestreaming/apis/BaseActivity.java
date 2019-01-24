package app.nunc.com.staatsoperlivestreaming.apis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import app.nunc.com.staatsoperlivestreaming.R;
import app.nunc.com.staatsoperlivestreaming.nexplayerengine.NexSystemInfo;
import app.nunc.com.staatsoperlivestreaming.util.PermissionManager;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	protected FragmentTabHost mTabHost = null;
	private static final String LOG_TAG = "BaseActivity";
	private SharedPreferences mPref;
	private PermissionManager mPermissionManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 ) {
			finish();
			return;
		}

		mPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		setContentView();
		Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		if(NexSystemInfo.getPlatformInfo() >= NexSystemInfo.NEX_SUPPORT_PLATFORM_MARSHMALLOW) {
			mPermissionManager = new PermissionManager(this);
			mPermissionManager.setPermissionFlags(
					PermissionManager.REQUEST_STORAGE |
							PermissionManager.REQUEST_LOCATION |
							PermissionManager.REQUEST_PHONE_STATE);
			mPermissionManager.requestPermissions();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

		if( navigationView != null ) {
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
					this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
			drawer.setDrawerListener(toggle);
			toggle.syncState();
			navigationView.setNavigationItemSelectedListener(this);

			MenuItem defaultMenu = navigationView.getMenu().getItem(0).getSubMenu().getItem(0);
			navigationView.setCheckedItem(defaultMenu.getItemId());
			setSdkMode(defaultMenu.getTitle().toString());
		}

		PlaybackHistory.checkTableAndReCreateDB(getBaseContext());		// Check streaming history DB and recreate if needed

		setupTabHost();
	}

	protected void setContentView() {
		setContentView(R.layout.base_activity);
	}

	private void setSdkMode(String sdkMode) {
		Log.d(LOG_TAG, "setSdkMode sdkMode : " + sdkMode);
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString(getString(R.string.pref_sdk_mode_key), sdkMode);
		editor.apply();
	}

	private MenuItem getCheckedMenuItem() {
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		Menu menu = navigationView.getMenu();
		MenuItem ret = null;
		if( menu.size() > 0 ) {
			MenuItem item = menu.findItem(R.id.nav_module);
			menu = item.getSubMenu();
			if( menu.size() > 0 ) {
				int i = 0;
				while (i < menu.size()) {
					if (menu.getItem(i).isChecked()) {
						ret = menu.getItem(i);
						break;
					}
					i++;
				}
			}
		}
		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.base_activity_action, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		CommonMenuItems.onOptionsItemSelected(this, item);
		return super.onOptionsItemSelected(item);
	}

	protected SharedPreferences getSharedPreferences() {
		return mPref;
	}

	protected IActivityLauncherPlugin getActivityLauncherPlugin() {
		return ActivityLauncherPlugin.getPlugin(this);
	}

	private int getTabChildCount(String sdkMode) {
		int num = 2;
		if( supportOfflinePlayback(sdkMode) )
			num++;
		return num;
	}

	/**
	 * set the base Listview
	 */
	protected void setupTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
		String sdkMode = mPref.getString(getString(R.string.pref_sdk_mode_key), getString(R.string.app_list_default_item));
		int count = mTabHost.getTabWidget().getTabCount();
		Log.d(LOG_TAG, "setupTabHost count : " + count);

		if( count == 0){

			// create Tab0  Local Setting
			mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_host_item_Local)).
					setIndicator(getResources().getString(R.string.tab_host_item_Local),
							getDrawable(this, android.R.drawable.ic_menu_save)), TabLocalContainer.class, null);
			// create Tab1  Streaming Setting
			mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_host_item_Streaming)).
					setIndicator(getResources().getString(R.string.tab_host_item_Streaming),
							getDrawable(this, android.R.drawable.ic_menu_recent_history)), TabStreaming.class, null);

			if( supportOfflinePlayback(sdkMode) ) {
				// create Tab2  OfflinePlayback Setting
				mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_host_item_OfflinePlayback)).
						setIndicator(getResources().getString(R.string.tab_host_item_OfflinePlayback),
								getDrawable(this, android.R.drawable.ic_menu_save)), TabOfflinePlayback.class, null);
			}

		}else if(count != getTabChildCount(sdkMode)){
			if(mTabHost.getTabWidget().getChildTabViewAt(3) != null) {
				if (!supportOfflinePlayback(sdkMode)) {
					mTabHost.getTabWidget().removeViewAt(3);
				}
			}
			else{
				if( supportOfflinePlayback(sdkMode) ) {
					// create Tab2  OfflinePlayback Setting
					mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_host_item_OfflinePlayback)).
							setIndicator(getResources().getString(R.string.tab_host_item_OfflinePlayback),
									getDrawable(this, android.R.drawable.ic_menu_save)), TabOfflinePlayback.class, null);
				}
			}
		}
	}

	protected Drawable getDrawable(Context context, int id) {
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
			return context.getDrawable(id);
		} else {
			return context.getResources().getDrawable(id);
		}
	}

	@Override
	public void onBackPressed() {
		boolean isPop = false;

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if ( drawer != null && drawer.isDrawerOpen(GravityCompat.START) ) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			String currentTabTag = mTabHost.getCurrentTabTag();
			if( currentTabTag.equals(getResources().getString(R.string.tab_host_item_Local)) ) {
				isPop = ((TabLocalContainer)getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.tab_host_item_Local))).popFragmentOrFolder();
			}

			if( !isPop ) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if( mPermissionManager != null )
			mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}


	private boolean supportOfflinePlayback(String sdkMode) {
		return supportOfflinePlayback(this, sdkMode);
	}

	public static boolean supportOfflinePlayback(Context context, String sdkMode) {
		boolean ret = false;
		if( sdkMode.equals(context.getString(R.string.app_list_default_item))
				|| sdkMode.equals(context.getString(R.string.app_list_video_view_item))
				// NexMediaDrm start
				|| sdkMode.equals(context.getString(R.string.app_list_video_view_media_drm_item))
				// NexMediaDrm end
				)
			ret = true;
		return ret;
	}

	private Class findClass(String className) {
		Class incClass = null;
		try {
			incClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			Log.e(LOG_TAG, "Can't find " + className + ".");
			Toast toast = Toast.makeText(getApplicationContext(),"Can't find " + className + " You can't use this demo app.", Toast.LENGTH_LONG);
			toast.show();
		}
		return incClass;
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();
		String sdkMode = item.getTitle().toString();

		Log.d(LOG_TAG, "onNavigationItemSelected title : " + sdkMode);
		setSdkMode(sdkMode);


		Class incClass = null;
		String className = "";



		if( getString(R.string.app_list_fd_item).equals(sdkMode) ) {
			incClass = NexFDSample.class;
		}


		if( incClass != null ) {
			Intent intent = new Intent(BaseActivity.this, incClass);
			startActivity(intent);
			return false;
		} else {
			setupTabHost();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

}
