package ru.supervital.bestrates;
// немного текста

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class ShowMemoActivity extends AppCompatActivity {
	
	public static final String TAG = "bestrates.ShowMemoActivity";
	public static final String MEMOTEXT             = "memotext";
	
	TextView lblCaption, txtMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_memo);
		
		lblCaption = (TextView) findViewById(R.id.lblCaption);
		txtMain    = (TextView) findViewById(R.id.txtMain);

		
		CaptionString cs = getIntent().getParcelableExtra(MEMOTEXT);
		
		if (cs.Title != null)
			setTitle(cs.Title);
		
		lblCaption.setText(Html.fromHtml("<b>" + cs.Caption + "</b><br>" + cs.DopStr));
		txtMain.setText(Html.fromHtml(cs.BigString));
		
	}

	 @SuppressLint("NewApi")
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case android.R.id.home:
/*	             Intent upIntent = NavUtils.getParentActivityIntent(this);
	              if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	                  TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
	               } else {
	                  upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	                  startActivity(upIntent);
	               }
*/
	        	 finish();
	             return true;		             
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }	 
}
