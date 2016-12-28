package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.*;
import org.apache.http.NameValuePair;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_BANK_OPER;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_VAL;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class BL_Activity extends AppCompatActivity {
	public static final String TAG = "bestrates.BL_Activity";

	private final int MENU_BANK_FILIASLS    	   = 1;
	private final int MENU_FILIAL_INFO       	   = 2;
	private final int MENU_MAP_BANK_FILIALS  	   = 3;
	private final int MENU_BANK_DOP_INFO           = 4;	
	

	public static final String RATE_CB           = "rate_cb";           // курс валюты по ÷Ѕ, строка
	public static final String TOAST_RATE_CB_STR = "toast_rate_cb_str"; // строка подсказки
	public static final String TYPE_CH_VAL       = "type_ch_val";       // тип иконки, растет или падает курс
	
	
//	BackgroundContainer mBackgroundContainer;
	
	Intent intent;		
	Region CurRegion;
	
	Handler hStateLB;
	ListView lvMain;
	TextView lblStateLB, lblNameBank, lblVbuy, lblVsell, lblRateCB, lblCapReg, lblCapCountB;
	LinearLayout mLayoutProgressBar;
	ProgressBar progressbar;
	
	ImageView imgCh, imgSortName, imgSortBuy, imgSortSell, imgCB;
	
	int mTypeSortName = 1, mTypeSortVbuy = 1, mTypeSortVsell = 0;   

	MenuItem action_refresh;
	
	ApplicationBestRates myAppl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blist);
		myAppl = (ApplicationBestRates) getApplication();
		CurRegion = (Region) myAppl.getCurrentRegion();  
		
		initViews();
		initHandlers();
		initSortTochLabels();
		initCBLabel();
		
		
		intent = getIntent();
		    
		// иконка изменени¤ курса по ÷Ѕ
		Integer iTypeChVal = intent.getIntExtra(TYPE_CH_VAL, 0);
        if (iTypeChVal == 0) {
        	imgCh.setImageResource(R.drawable.vdown);
        	lblRateCB.setTextColor(getResources().getColor(R.color.holo_green_main));	        	        	
        } else if (iTypeChVal == 1) { 
        	imgCh.setImageResource(R.drawable.vup);
        	lblRateCB.setTextColor(Color.RED);	
        }

		lblCapReg.setText(String.format(getString(R.string.BLCapReg), CurRegion.Name)); // —писок банков в регионе
		lblRateCB.setText(getString(R.string.slblRateCB) + ": " + intent.getStringExtra(RATE_CB)); //  урс ÷Ѕ
		
		LoadBankList();
	} // onCreate
	
	View.OnClickListener mValClickShowToast = new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														String sStr = intent.getStringExtra(TOAST_RATE_CB_STR);
														Toast.makeText(getApplicationContext(), sStr, Toast.LENGTH_SHORT).show();
													}
												};
	
	public void initCBLabel(){
		imgCB.setOnClickListener(mValClickShowToast);
		lblRateCB.setOnClickListener(mValClickShowToast);
		imgCh.setOnClickListener(mValClickShowToast);
		imgCB.setOnTouchListener(mValOnTouch);
		lblRateCB.setOnTouchListener(mValOnTouch);
		imgCh.setOnTouchListener(mValOnTouch);
	}
	
	public void initSortTochLabels() {
		lblNameBank  = (TextView) findViewById(R.id.lblSortNameBank);
		lblNameBank.setClickable(true);
		
		lblVbuy      = (TextView) findViewById(R.id.lblSortVbuy);
		lblVbuy.setGravity(Gravity.RIGHT);
		lblVbuy.setClickable(true);
		
		lblVsell     = (TextView) findViewById(R.id.lblSortVsell);
		lblVsell.setGravity(Gravity.RIGHT);
		lblVsell.setClickable(true);
		
		imgSortName  = (ImageView) findViewById(R.id.imgSortName);
		imgSortBuy   = (ImageView) findViewById(R.id.imgSortBuy);
		imgSortSell  = (ImageView) findViewById(R.id.imgSortSell);
		
		lblNameBank.setOnClickListener(mSortValOnClick);
		lblVbuy.setOnClickListener(mSortValOnClick);
		lblVsell.setOnClickListener(mSortValOnClick);
		imgSortName.setOnClickListener(mSortValOnClick);
		imgSortBuy.setOnClickListener(mSortValOnClick);
		imgSortSell.setOnClickListener(mSortValOnClick);
		
		lblNameBank.setOnTouchListener(mValOnTouch);
		lblVbuy.setOnTouchListener(mValOnTouch);
		lblVsell.setOnTouchListener(mValOnTouch);
		imgSortName.setOnTouchListener(mValOnTouch);
		imgSortBuy.setOnTouchListener(mValOnTouch);
		imgSortSell.setOnTouchListener(mValOnTouch);
	}
	
	View.OnTouchListener mValOnTouch = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setTop(v.getTop() + 10);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setTop(v.getTop() - 10);
				v.performClick(); // то, по чему помазали по тому и  сортируем
			}
			return true;
		}
	}; 
	
	View.OnClickListener mSortValOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// если нажали на label или на картинку, то показать соответствующую кртинку
			imgSortName.setVisibility((v.getId() == R.id.lblSortNameBank || v.getId() == R.id.imgSortName) ? View.VISIBLE : View.INVISIBLE);
			imgSortBuy.setVisibility((v.getId() == R.id.lblSortVbuy || v.getId() == R.id.imgSortBuy)? View.VISIBLE : View.INVISIBLE);
			imgSortSell.setVisibility((v.getId() == R.id.lblSortVsell || v.getId() == R.id.imgSortSell) ? View.VISIBLE : View.INVISIBLE);

			// рисуем картинка типа сортировки
			// мен¤ем значение типа сортирвки на противоположное
			if (imgSortName.getVisibility() == View.VISIBLE) {
				if (mTypeSortName == 1) {
				    imgSortName.setImageResource(R.drawable.sort_az);
				    mTypeSortName = 0;
				} else { 
					imgSortName.setImageResource(R.drawable.sort_za);
					mTypeSortName = 1;
				}
			} else if (imgSortBuy.getVisibility() == View.VISIBLE) {
				if (mTypeSortVbuy == 1) {
				    imgSortBuy.setImageResource(R.drawable.sort_down);
				    mTypeSortVbuy = 0;
				} else { 
					imgSortBuy.setImageResource(R.drawable.sort_up);
					mTypeSortVbuy = 1;
				}
			} else if (imgSortSell.getVisibility() == View.VISIBLE) {
				if (mTypeSortVsell == 1) {
					imgSortSell.setImageResource(R.drawable.sort_down);
					mTypeSortVsell = 0;
				} else { 
					imgSortSell.setImageResource(R.drawable.sort_up);
					mTypeSortVsell = 1;
				}
			}
			
			final View vSort = v;
			
			Collections.sort(CurRegion.mBankListArrayAdapter.banks, new Comparator<Bank>() {
				  @Override
				  public int compare(Bank o1, Bank o2) {
					  
					int res = 0;
					if (vSort.getId() == R.id.lblSortNameBank || vSort.getId() == R.id.imgSortName) {
							res = o1.NameBank.compareToIgnoreCase(o2.NameBank) * (mTypeSortName == 1 ? -1 : 1);
					} else if (vSort.getId() == R.id.lblSortVbuy || vSort.getId() == R.id.imgSortBuy) {
							if (CurRegion.type_val == TYPE_VAL.tvUSD)
								res = Double.valueOf(o1.USDbuy).compareTo(o2.USDbuy) * (mTypeSortVbuy == 1 ? 1 : -1);
							else 
								res = Double.valueOf(o1.EURbuy).compareTo(o2.EURbuy) * (mTypeSortVbuy == 1 ? 1 : -1);
					} else if (vSort.getId() == R.id.lblSortVsell || vSort.getId() == R.id.imgSortSell) { 
							if (CurRegion.type_val == TYPE_VAL.tvUSD)
								res = Double.valueOf(o1.USDsell).compareTo(o2.USDsell) * (mTypeSortVsell == 1 ? 1 : -1);
							else
								res = Double.valueOf(o1.EURsell).compareTo(o2.EURsell) * (mTypeSortVsell == 1 ? 1 : -1);
					}
					return res;
				  }
				});
			
			CurRegion.mBankListArrayAdapter.notifyDataSetChanged();
		}
	}; 	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		switch (v.getId()) {
			case R.id.lvMain: 
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
				Bank bank = ((Bank) CurRegion.mBankListArrayAdapter.banks.get(info.position));
				String NameBank = bank.NameBank; 
				
//				if (NameBank.contains(Bank.STR_FILIAL))
					menu.add(0, MENU_BANK_FILIASLS, 0, R.string.smBankFilials);
//				else menu.add(0, MENU_FILIAL_INFO, 0, R.string.smFilialInfo);
				
				menu.add(0, MENU_MAP_BANK_FILIALS, 0, R.string.smMap);
/* 
 * пункт меню "ƒополнительно" енаблем только в платной версии				
*/
				String sStr = getString(R.string.smDopInfo) + getString(R.string.TriPoint);
				menu.add(0, MENU_BANK_DOP_INFO, 0, sStr)
				  	.setEnabled(myAppl.isPaidVersion);
				  	
				
			break;
		}
	}	
		
	 @Override
    public boolean onContextItemSelected(MenuItem item) {
	   AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
	   CurRegion.CurBankPosition = acmi.position;
	   Intent intent;
       switch (item.getItemId()) {
       // пункты меню дл¤ lvMain
       	case MENU_FILIAL_INFO:
	    case MENU_BANK_FILIASLS:
            intent = new Intent(BL_Activity.this, FL_Activity.class);
            startActivity(intent);
	    	break;
	    case MENU_MAP_BANK_FILIALS:
	    	intent = new Intent(BL_Activity.this, FL_Activity.class);
			intent.putExtra(FL_Activity.LOAD_FOR_MAP, "1");
            startActivity(intent);
	    	break;
	    case MENU_BANK_DOP_INFO:
			if (CurRegion.DopInfos == null || CurRegion.DopInfos.size() == 0) {		
				mt = new BankListPostInActivity(myAppl.getDopInfoConnStr(), null, aResult);
				mt.flDopInfoLoad = true;
				mt.mActivity = this; 			
				mt.mLayoutProgressBar = mLayoutProgressBar;
				mt.mOperationProgressBar = progressbar;
				mt.hState = hStateLB;
				mt.execute(mt.mUrl);
				mt.showProgress(true);
			} else {
				ShowDopInfo();
			}
	    	break;	    	
       }
       return super.onContextItemSelected(item);
    }	

	void ShowDopInfo() {
  		Bank bank = CurRegion.banks.get(CurRegion.CurBankPosition);
  		bank.DopInfo = CurRegion.getDopInfoByID(bank.bank_id);
    	intent = new Intent(BL_Activity.this, ShowMemoActivity.class);
	               	    	// ƒополнительна¤ информаци¤
		CaptionString cs = new CaptionString(getString(R.string.smDopInfo) + getString(R.string.Colon), 
								bank.DopInfo, "");
		cs.Title = bank.NameBank;
		intent.putExtra(ShowMemoActivity.MEMOTEXT, cs);
		
		startActivity(intent);
	}
	 
	public void initHandlers() {
	    hStateLB = new Handler() {
		      public void handleMessage(android.os.Message msg) {
		    	  	if (msg.what <= 0) return;
		    	   
			        // обновл¤ем TextView
			    	String sStr = getString(msg.what);
			    	
			    	if (msg.arg1 != 0) {
			    		sStr = sStr + " " + msg.arg1;
			    		//если убрать "if", то замедл¤етс¤ работа процесса разбора
//					    if (msg.arg1%9 == 0 && msg.arg1 < 20 )
//					    	 CurRegion.mBankListArrayAdapter.notifyDataSetChanged();		    	
			    	}
			    	if (msg.arg2 != 0) {
			    		lblStateLB.setTextColor(msg.arg2); 		    		
			    	}
			    	lblStateLB.setText(sStr);
		      };
		 };				
	}
	
	void initViews(){
//		mBackgroundContainer = (BackgroundContainer) findViewById(R.id.lvBackground);
		
		lvMain       = (ListView) findViewById(R.id.lvMain);
		registerForContextMenu(lvMain);
		lvMain.requestFocusFromTouch();
		
		lblStateLB   = (TextView) findViewById(R.id.lblStateLB);
		mLayoutProgressBar = (LinearLayout) findViewById(R.id.ll_progressbar);
		progressbar  = (ProgressBar) findViewById(R.id.progressbar);

		lblCapReg	 =  (TextView) findViewById(R.id.lblCapReg);
		lblCapCountB =  (TextView) findViewById(R.id.lblCapCountB);
		
		lblRateCB   =  (TextView) findViewById(R.id.txtRateCB);		
		imgCh = (ImageView) findViewById(R.id.imgCh);
		imgCB = (ImageView) findViewById(R.id.imgCB);
	}
	
	public Boolean aResult = false;	
	void LoadBankList() {
		initTypeSort(); 
		if (CurRegion.mBankListArrayAdapter == null)
			CurRegion.mBankListArrayAdapter = new BL_ArrayAdapter(this, CurRegion.banks);
		if (CurRegion.banks.size() == 0) {		
			mt = new BankListPostInActivity(myAppl.getRatesForRegionConnStr(), null, aResult);
			mt.mActivity = this; 			
			mt.mLayoutProgressBar = mLayoutProgressBar;
			mt.mOperationProgressBar = progressbar;
			mt.hState = hStateLB;
			mt.execute(mt.mUrl);
			mt.showProgress(true);
		} else {
			MakeCapBankReg();
			sortBySetting(); // lblVsell.performClick();
		}

		CurRegion.mBankListArrayAdapter.type_val = CurRegion.type_val;
		lvMain.setAdapter(CurRegion.mBankListArrayAdapter);
	}

	// устанавливает Enabled дл¤ тех, кто принимает участие в потоке
	public void setEnabledAll(Boolean aVal){
		lblNameBank.setEnabled(aVal);
		lblVbuy.setEnabled(aVal);
		lblVsell.setEnabled(aVal);
		lvMain.setEnabled(aVal);
		if (aVal) {
			lblNameBank.setTextColor(Color.BLUE);
			lblVbuy.setTextColor(Color.BLUE);
			lblVsell.setTextColor(Color.BLUE);
		} else {
			lblNameBank.setTextColor(getResources().getColor(R.color.disable_color_button));
			lblVbuy.setTextColor(getResources().getColor(R.color.disable_color_button));
			lblVsell.setTextColor(getResources().getColor(R.color.disable_color_button));
		}
		
		if (action_refresh != null) 
			action_refresh.setEnabled(aVal);

	}
	
	
//==============================================================================================================	
public BankListPostInActivity mt;
class BankListPostInActivity extends BL_Post2 {
	
	public BankListPostInActivity(String Url, List<NameValuePair> Params, Boolean Result) {
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
			hState.sendEmptyMessage(R.string.stAllOk);
			setEnabledAll(true);						
			if (!flDopInfoLoad){
				GlobalTopSortSetting();
				MakeCapBankReg();			
				CurRegion.mBankListArrayAdapter.notifyDataSetChanged();			
			} else {
				flDopInfoLoad = false;
				ShowDopInfo();
			}
		}
	}
}
//==============================================================================================================
	
	void MakeCapBankReg() {
		String sStr, sCapReg;
									// —писок банков в регионе
		sCapReg = String.format(getString(R.string.BLCapReg), CurRegion.Name);
		
		if (CurRegion.iTotalCountBanks != 0) {
			sCapReg = sCapReg + ", ";
			sStr = getString(R.string.BLTotalCountB, String.valueOf(CurRegion.iTotalCountBanks))
					+ getString(R.string.BLShowCountB, String.valueOf(CurRegion.mBankListArrayAdapter.banks.size()));
			lblCapCountB.setText(sStr);
			lblCapCountB.setVisibility(View.VISIBLE);
		} else {
			lblCapCountB.setVisibility(View.GONE);			
		}
		
		lblCapReg.setText(sCapReg);
	}
		
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     // Inflate the menu items for use in the action bar
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.banks_list, menu);
	     return true;
	 }	 
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case R.id.action_refresh:
	        	 if (mt==null || mt.getStatus() != Status.RUNNING) {
	        		 CurRegion.iTotalCountBanks = 0;
	        		 MakeCapBankReg();
	        		 initTypeSort();
	        		 CurRegion.banks.clear();
	        		 if (CurRegion.DopInfos != null)
	        			 CurRegion.DopInfos.clear();
	        		 CurRegion.mBankListArrayAdapter = null;
	        		 LoadBankList();
	        	 }
	             return true;
		     case android.R.id.home:
		    	 finish();
		         return true;		             
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }	 
	 
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
	 {
	     action_refresh = menu.findItem(R.id.action_refresh);
	     return super.onPrepareOptionsMenu(menu);
	 }	 
	 
	 public void initTypeSort(){
		 mTypeSortName = 1;
		 mTypeSortVbuy = 1; 
		 mTypeSortVsell = 0;
	 }
	 
	 void sortBySetting() {
		if (myAppl.settingOperation == TYPE_BANK_OPER.boSale) {
			mTypeSortVsell = 0;			
			// банк продает
			lblVsell.performClick();
		} else {
			 mTypeSortVbuy = 1;
			// банк покупает					
			lblVbuy.performClick();						
		}
	 }
	 
}  // main class