package ru.supervital.bestrates;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import ru.yandex.yandexmapkit.MapActivity;

import java.util.List;

@SuppressLint("NewApi")
public class FL_Activity extends AppCompatActivity {
	public static final String TAG = "bestrates.FL_Activity";
	
	public static final String LOAD_FOR_MAP    = "load_for_map";    // загружаем только данные, потом показывает карту
	public static final String SHOW_ONE_FILIAL = "show_one_filial"; //показывать инфу только по одному филиалу 
	public static final String FROM_FILIALS    = "from_filials";    // признак того, что будем показывать карту из филиалов,
																	// поэтому в балуне будет только одна валюта, согласно CurRegion
	
	
	
	
	
	Bank CurBank;
	ListView lvMain;
	
	BackgroundContainer mBackgroundContainer;
	TextView lblStateLB, lblNameBank, lblCountFilials;
	ImageView imgBankLogo;
	Handler hStateLF;
	LinearLayout mProgressBar;

	ApplicationBestRates myAppl;
	
	MenuItem action_refresh;
	String sOneFilial;			     // показывать описание только одного филиала с именем sOneFilial 
	Boolean flLoadForMap = false;    // после загрузки всего списка показывать карту с филиалами
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filials);
		myAppl = (ApplicationBestRates) getApplication();
		CurBank = (Bank) ((Region) myAppl.getCurrentRegion()).getCurrentBank();    
		
		initViews();
		initHandlers();
		
		
		
		String sStr = getIntent().getStringExtra(FL_Activity.LOAD_FOR_MAP);
		flLoadForMap = (sStr != null && sStr.contains("1"));
		
 		MakeBankName();
		imgBankLogo.setImageBitmap(CurBank.BitmapBank);
		
		sOneFilial = getIntent().getStringExtra(FL_Activity.SHOW_ONE_FILIAL);
		
		if (sOneFilial != null) {
			ShowOneFilial();
		} else {
			LoadFilials();
		}
		
	} // onCreate
	
	void ShowOneFilial() {
		if (CurBank.mFilialsArrayAdapter == null)  
			CurBank.mFilialsArrayAdapter = new FL_ArrayAdapter(this, CurBank.filials);

		CurBank.mFilialsArrayAdapter.getFilter().filter(sOneFilial); //Filter from my adapter
		CurBank.mFilialsArrayAdapter.flViewButtonOnMap = false;
		if (lvMain.getAdapter() != CurBank.mFilialsArrayAdapter)
					lvMain.setAdapter(CurBank.mFilialsArrayAdapter);

	}
	
	void MakeBankName() {
		CharSequence cs = Bank.getBoldNameBankFilNormNL(CurBank.NameBank);
		int i = CurBank.NameBank.indexOf(Bank.STR_FILIAL);
		if (mt != null && mt.needShowFF){
			String BL = "<font size=6><b>", EL = "</b></font>", aNameBank = CurBank.NameBank;

			cs = Html.fromHtml(BL + aNameBank.substring(0, i) +  EL + "<br>" + aNameBank.substring(i)
  							   + "<br>" + String.format(getString(R.string.lblFirstFilials)  + ": ", CurBank.filials.size()));
		}
		if (CurBank.filials != null && CurBank.filials.size() != 0 && i<0 ) {
			lblCountFilials.setVisibility(View.VISIBLE);
        	String sStr = getString(R.string.stTotalFilials) + CurBank.filials.size(); 
        	lblCountFilials.setText(sStr);
		}
		lblNameBank.setText(cs);		
	}
	
	void initViews(){
		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.lvBackground);
		lvMain       = (ListView) findViewById(R.id.lvMain);
		lvMain.setTextFilterEnabled(true);
		
		lblStateLB   = (TextView) findViewById(R.id.lblStateLB);
		mProgressBar = (LinearLayout) findViewById(R.id.ll_progressbar);
		
		lblNameBank  = (TextView) findViewById(R.id.lblNameBank);
		imgBankLogo  = (ImageView) findViewById(R.id.imgBankLogo);
		
		lblCountFilials = (TextView) findViewById(R.id.lblCountFilials);
	}
	
	public void initHandlers() {
	    hStateLF = new Handler() {
		      public void handleMessage(android.os.Message msg) {
		    	  
		    	  if (msg.what == ApplicationBestRates.HMSG_LOAD_STACK_FILIALS) {
		    		  	// Получаем %s пачку филиалов, всего пачек: 
	    	        	String sStr = String.format(getString(R.string.stLoadStackFilials), msg.arg1, msg.arg2); 
	    	        	lblStateLB.setText(sStr);
			    		return;
		    	  } else if(msg.what == ApplicationBestRates.HMSG_PARSE_STACK_FILIALS) {
		    		  	// Разбираем %s пачку филиалов, всего пачек: %s 
	    	        	String sStr = String.format(getString(R.string.stParseStackFilials), msg.arg1, msg.arg2); 
	    	        	lblStateLB.setText(sStr);
			    		return;
		    	  } else if(msg.what == ApplicationBestRates.HMSG_COUNT_FILIALS) {
		    			// всего филиалов %s
	    	        	String sStr = getString(R.string.stTotalFilials) + msg.arg1; 
	    	        	lblCountFilials.setText(sStr);
			    		return;
		    	  } else if (msg.what <= 0) return; 

  		         // обновляем TextView 
		    	 String sStr = getString(msg.what);
		    	
		     	 if (msg.arg1 != 0) {
		    		sStr = sStr + " " + msg.arg1;
//					    if (msg.arg1%5 == 0 && msg.arg1 < 5) //если убрать "if", то замедляется работа процесса разбора
//					    	CurBank.mFilialsArrayAdapter.notifyDataSetChanged();		    	
		    	 } 
		    	 if (msg.arg2 != 0) {
		    		lblStateLB.setTextColor(msg.arg2); 		    		
		    	 }
		    	lblStateLB.setText(sStr);
		     };
	    };				
	    
	} // initHandlers()

	public Boolean aResult = false;	
	//----
	void LoadFilials() {
		if (CurBank.mFilialsArrayAdapter == null)  
			CurBank.mFilialsArrayAdapter = new FL_ArrayAdapter(this, CurBank.filials);
		
		mt = new FilialsListPostInActivity(CurBank.getFilialsForBankConnStr(), null, aResult);
		mt.mActivity = this;
		mt.CurBank = CurBank;
		mt.mLayoutProgressBar = mProgressBar;
		mt.flShowProgressOnPost = flLoadForMap;
		mt.hState = hStateLF;
		mt.execute(mt.mUrl);
		CurBank.mFilialsArrayAdapter.flViewButtonOnMap = true;
		CurBank.mFilialsArrayAdapter.BankName = CurBank.NameBank;
		if (lvMain.getAdapter() != CurBank.mFilialsArrayAdapter) 
					lvMain.setAdapter(CurBank.mFilialsArrayAdapter);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		lvMain.setVisibility(View.VISIBLE);
	}

	void ShowAllFilials() {
		sOneFilial = null;
		CurBank.mFilialsArrayAdapter.getFilter().filter(null); 
		CurBank.mFilialsArrayAdapter.flViewButtonOnMap = true;
		if (lvMain.getAdapter() != CurBank.mFilialsArrayAdapter)		
					lvMain.setAdapter(CurBank.mFilialsArrayAdapter);
	}
	
    @Override
	protected void onPause() {
		super.onPause();
		if (sOneFilial != null) {
			lvMain.setVisibility(View.GONE);
			ShowAllFilials();
		}
	}

//==============================================================================================================	
public FilialsListPostInActivity mt;
class FilialsListPostInActivity extends FL_Post2 {
	
	public FilialsListPostInActivity(String Url, List<NameValuePair> Params, Boolean Result) {
		super(Url, Params, Result);
	}
	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    setEnabledAll(false);		    
	}
	
	@Override
	protected void onPostExecute(final Boolean success) {
		super.onPostExecute(success);
		if (success) {
			if (flLoadForMap) {
				msg = hStateLF.obtainMessage(R.string.stLoadMap, 0, Color.BLUE);
			} else {
				msg = hStateLF.obtainMessage(R.string.stAllOk, 0, Color.BLUE);
			}
			hStateLF.sendMessage(msg);
			
			setEnabledAll(true);
			MakeBankName();
			CurBank.mFilialsArrayAdapter.MakeOriginal();
			
			if (flLoadForMap)
				ShowMap();
			else 
				ShowAllFilials();
		}
	}	
	
} // FilialsListPostInActivity
//==============================================================================================================
		
	void ShowMap() {
        Intent intent = new Intent(this, MapActivity.class)
        		     .putExtra(FL_Activity.FROM_FILIALS, "1");
        CurBank.mFilialsArrayAdapter.flViewButtonOnMap = false;        		     
        startActivity(intent);
        finish();
	} // ShowMap
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	     // Inflate the menu items for use in the action bar
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.filials_list, menu);
	     return true;
	 }	 
	 
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case R.id.action_refresh:
	        	 // когда один филиал, то обновлть не надо
	        	 if (sOneFilial != null && sOneFilial.length() > 0) return true;
	        	 if (mt==null || mt.getStatus() != Status.RUNNING) {
	        		 CurBank.filials.clear();
	        		 CurBank.mFilialsArrayAdapter = null;
	        		 LoadFilials();
	        	 }
	             return true;
	             
	         case android.R.id.home:
	     		finish();
	             return true;		             
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }	 

	// устанавливает Enabled для тех, кто принимает участие в потоке
	public void setEnabledAll(Boolean aVal){
		if (action_refresh != null) 
			action_refresh.setEnabled(aVal);

	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu)
	 {
	     action_refresh = menu.findItem(R.id.action_refresh);
	     return super.onPrepareOptionsMenu(menu);
	 }	 
		 
		 
		 
}
