package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.util.ArrayList;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {
	public static final String TAG = "bestrates.MainActivity";
	String devID = "101736538"; //ID разработчика
	String appID = "201676715"; //ID приложения
	private StartAppAd startAppAd = new StartAppAd(this);

	public static final int FRAME_BUTTONS  = 0;
	public static final int FRAME_SETTING  = 1;
	public static final int FRAME_MANUAL   = 2;
	public static final int FRAME_FEEDBACK = 3;
	public static final int FRAME_RATE     = 4;
	public static final int FRAME_SUPPORT  = 5;
	public static final int FRAME_ABOUT    = 6;
	public static final int FRAME_EXIT     = 7;

	ApplicationBestRates myAppl;

	private DrawerLayout mDrawerLayout;
	private ListView lvDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	ArrayList<Drawer_ArrayAdapter.DrawerMenuItem> mDrawerItems = new ArrayList<Drawer_ArrayAdapter.DrawerMenuItem>();

	MenuItem action_refresh;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StartAppSDK.init(this, devID, appID, false);

		setContentView(R.layout.activity_main);
		try {

			myAppl = (ApplicationBestRates) getApplication();
			myAppl.isPaidVersion = getPackageName().contains(".paid");

			initView();

			// ActionBarDrawerToggle ties together the the proper interactions
			// between the sliding drawer and the action bar app icon
			mDrawerToggle = new ActionBarDrawerToggle(
					this,           // host AppCompatActivity
					mDrawerLayout,         // DrawerLayout object
					R.drawable.ic_drawer,  // nav drawer image to replace 'Up' caret
					R.string.drawer_open,  // "open drawer" description for accessibility
					R.string.drawer_close  // "close drawer" description for accessibility
			) {
				public void onDrawerClosed(View view) {
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}

				public void onDrawerOpened(View drawerView) {
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}
			};

			mDrawerLayout.setDrawerListener(mDrawerToggle);

			if (savedInstanceState == null) {
				selectItem(FRAME_BUTTONS); //FRAME_SETTING); //
			}
		} catch (Throwable e) {
			Log.e(TAG, "Error in onCreate ");
			String sMessage = e.getLocalizedMessage();
			e.printStackTrace();
		}


	} // onCreate


	@Override
	public void onResume(){
		super.onResume();
		startAppAd.onResume();
	}

	//Метод вызова рекламы при нажатии кнопки home
	@Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
	@Override
	public void onBackPressed() {
		//Метод вызова рекламы при нажатии кнопки back
		startAppAd.onBackPressed();

		if (myAppl.ActiveFragment != null && myAppl.ActiveFragment.ID != FRAME_BUTTONS)
			selectItem(FRAME_BUTTONS);
		else
			super.onBackPressed();
	}

	void initView() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lvDrawer = (ListView) findViewById(R.id.left_listview);
		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		initDrawerItems();
	}

	Drawer_ArrayAdapter drawerArrayAdapter;
	void initDrawerItems() {

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgMain),     getString(R.string.dmMain),     getString(R.string.dmDescrMain),
				FRAME_BUTTONS));
		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgSetting),  getString(R.string.dmSetting),  getString(R.string.dmDescrSetting),
				FRAME_SETTING ));
		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgManual),   getString(R.string.dmManual),   getString(R.string.dmDescrManual),
				FRAME_MANUAL));
		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgFeedback), getString(R.string.dmFeedback), getString(R.string.dmDescrFeedback),
				FRAME_FEEDBACK));
		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgRate),     getString(R.string.dmRate),     getString(R.string.dmDescrRate),
				FRAME_RATE));
		if (!myAppl.isPaidVersion)
			mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgSupport),  getString(R.string.dmSupport),  getString(R.string.dmDescrSupport),
					FRAME_SUPPORT));

		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgAbout),    getString(R.string.dmAbout),    getString(R.string.dmDescrAbout),
				FRAME_ABOUT ));
		mDrawerItems.add(new Drawer_ArrayAdapter.DrawerMenuItem(getString(R.string.dmImgExit),     getString(R.string.dmExit),     getString(R.string.dmDescrExit),
				FRAME_EXIT));

		drawerArrayAdapter = new Drawer_ArrayAdapter(this, mDrawerItems);
		lvDrawer.setAdapter(drawerArrayAdapter);
		lvDrawer.setOnItemClickListener(new DrawerItemClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (myAppl!=null && myAppl.ActiveFragment != null && myAppl.ActiveFragment.ID == FRAME_BUTTONS) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		action_refresh = menu.findItem(R.id.action_refresh);
		return super.onPrepareOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (myAppl.ActiveFragment != null)
			return myAppl.ActiveFragment.onOptionsItemSelected(item);
		else
			return true;
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	BestratesFragment getAppFragment(int aIndexFrame) {
		BestratesFragment res = null;
		for (BestratesFragment fra:myAppl.AppFrames) {
			if (fra.ID == aIndexFrame) {
				res = fra;
				break;
			}
		}
		return res;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void selectItem(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		BestratesFragment fra = getAppFragment(mDrawerItems.get(position).ID);
		switch (mDrawerItems.get(position).ID) {
			case FRAME_BUTTONS:
				if (fra == null)
					myAppl.ActiveFragment = new fraButtons();
				else {
					myAppl.ActiveFragment = fra;
				}
				break;
			case FRAME_SETTING:
				if (fra == null)
					myAppl.ActiveFragment = new fraSettings();
				else
					myAppl.ActiveFragment = fra;
				break;
			case FRAME_MANUAL:
				if (fra == null)
					myAppl.ActiveFragment = new fraManual();
				else
					myAppl.ActiveFragment = fra;
				break;
			case FRAME_FEEDBACK:
				myAppl.SendMail(this);
				return;
			case FRAME_RATE:
				RateProgramm(getPackageName().toString());
				return;
			case FRAME_SUPPORT:
				RateProgramm(getPackageName().toString()+ ".paid");
				return;
			case FRAME_ABOUT:
				if (fra == null)
					myAppl.ActiveFragment = new fraAbout();
				else
					myAppl.ActiveFragment = fra;
				break;
			case FRAME_EXIT:
				lvDrawer.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(lvDrawer);
				finish();
				return;
			default:
				return;
		}
		if (fra == null)
			myAppl.AppFrames.add(myAppl.ActiveFragment);

		fragmentManager.beginTransaction().replace(R.id.content_frame, myAppl.ActiveFragment).commit();

		drawerArrayAdapter.iSelectedItem = myAppl.ActiveFragment.ID;
		lvDrawer.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(lvDrawer);

		if (myAppl.ActiveFragment.ID == MainActivity.FRAME_BUTTONS)
			((fraButtons) myAppl.ActiveFragment).ShowProgress();

	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (mDrawerToggle!=null)
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerToggle!=null)
			mDrawerToggle.onConfigurationChanged(newConfig);
	}

	void RateProgramm(String sStr) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + sStr));
		startActivity(intent);
	}

}