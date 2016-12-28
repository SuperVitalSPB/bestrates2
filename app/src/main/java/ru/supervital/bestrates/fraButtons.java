package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_VAL;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class fraButtons extends BestratesFragment {
	public static final String TAG = "bestrates.fraButtons";
	
    private LinearLayout mLayoutProgressBarRegions, mLayoutProgressBarCB, ll_rateCb, ll_spinner, ll_Global, ll_Develop;
    ProgressBar pbRegions, pb_main, pb_secondary;
		
	  String tmpBeforeLoadRegion;  // название региона, предыдущее значение, до загрузки всего списка.  
	  
	  Handler hStateCB, hStateRegs; 
	  
	  Button btnBuyEUR, btnBuyUSD, btnMap, btnAllValCB;
	  TextView lblRateCb, lblRateCbEur, lblRateCbUsd, lblStateCB, lblStateRegs, lblSelRegion;
	  ImageView imgChEur, imgChUsd, imgEur, imgUsd, imgCB;
	  Integer iTypeIconChValEUR, iTypeIconChValUSD; //тип иконки изменения курса рубля (растет|падает, 1|0)
	  String sDateRate;	  
	  Spinner spRegions;
	  
	  Rate rateUSD_CB, rateEUR_CB, rateButton;
	  public Region CurRegion;
	  
    public fraButtons () {
    	ID = MainActivity.FRAME_BUTTONS;
    }	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_buttons, container, false);

	    initVal();
  	    initViews(rootView);
  	    initHandlers();
  	    initRateCBOnClickListener();
  	
  	    checkOnline();
  	
  	    setEnabledAll(false);
  	
  	    if (isOnline()) {
	        ManageRegions();
		    LastRate();
	  	}

        return rootView;
    }

	private void initViews(View rootView) {
		  lblSelRegion  = (TextView) rootView.findViewById(R.id.lblSelRegion);
		  lblRateCb     = (TextView) rootView.findViewById(R.id.lblRateCb);
		  lblRateCbEur  = (TextView) rootView.findViewById(R.id.lblRateCbEur);
		  lblRateCbUsd  = (TextView) rootView.findViewById(R.id.lblRateCbUsd);
		  lblStateCB    = (TextView) rootView.findViewById(R.id.lblStateCB);
		  lblStateRegs  = (TextView) rootView.findViewById(R.id.lblStateRegs);
		  
		  
		  btnBuyEUR   = (Button) rootView.findViewById(R.id.btnBuyEUR);
		  btnBuyUSD   = (Button) rootView.findViewById(R.id.btnBuyUSD);	  	  
		  btnMap      = (Button) rootView.findViewById(R.id.btnMap);
		  btnAllValCB = (Button) rootView.findViewById(R.id.btnAllValCB);
		  
		  
		  imgChEur = (ImageView) rootView.findViewById(R.id.imgChEur);
		  imgChUsd = (ImageView) rootView.findViewById(R.id.imgChUsd);
		  imgEur   = (ImageView) rootView.findViewById(R.id.imgEur);
		  imgUsd   = (ImageView) rootView.findViewById(R.id.imgUsd);
		  imgCB    = (ImageView) rootView.findViewById(R.id.imgCB);
		
		  ll_Develop = (LinearLayout) rootView.findViewById(R.id.ll_Develop);
		  ll_Develop.setVisibility(myAppl.isDevelopMode ? View.VISIBLE : View.GONE);						  
		  
		  ll_Global  = (LinearLayout) rootView.findViewById(R.id.ll_Global);
		  ll_rateCb  = (LinearLayout) rootView.findViewById(R.id.ll_rateCb);
		  ll_spinner = (LinearLayout) rootView.findViewById(R.id.ll_spinner);
		  
		  mLayoutProgressBarRegions = (LinearLayout) rootView.findViewById(R.id.ll_progressbarregions);
 		  pbRegions                 = (ProgressBar) rootView.findViewById(R.id.pbRegions);
 		  
		  mLayoutProgressBarCB      = (LinearLayout) rootView.findViewById(R.id.ll_progressbarCB);
		
		  spRegions = (Spinner) rootView.findViewById(R.id.spRegions);
		  
		  pb_main      = (ProgressBar) rootView.findViewById(R.id.pb_main);
		  pb_secondary = (ProgressBar) rootView.findViewById(R.id.pb_secondary);
		  
		  initButtons();
		  initOnTouchLabels();
	}

	OnClickListener btnBuyEUR_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			rateButton = rateEUR_CB;
			myAppl.getCurrentRegion().type_val = TYPE_VAL.tvEUR;
            Intent intent = new Intent(mActivity, BL_Activity.class)
                  .putExtra(BL_Activity.RATE_CB, lblRateCbEur.getText())
                  .putExtra(BL_Activity.TOAST_RATE_CB_STR, getToastStr(myAppl.rateEUR_CB))
                  .putExtra(BL_Activity.TYPE_CH_VAL, iTypeIconChValEUR);
            startActivity(intent);
		}
	};  
	
	OnClickListener btnBuyUSD_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			rateButton = rateUSD_CB;
			myAppl.getCurrentRegion().type_val = TYPE_VAL.tvUSD;
            Intent intent = new Intent(mActivity, BL_Activity.class)
              .putExtra(BL_Activity.RATE_CB, lblRateCbUsd.getText())
              .putExtra(BL_Activity.TOAST_RATE_CB_STR, getToastStr(myAppl.rateUSD_CB))
              .putExtra(BL_Activity.TYPE_CH_VAL, iTypeIconChValUSD);
            startActivity(intent);
		}
	};

	OnClickListener btnMap_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Region CurReg = myAppl.getCurrentRegion();
			if (CurReg == null) return;
			CurReg.hState = hStateRegs;
			CurReg.mLayoutProgressBar = mLayoutProgressBarRegions;
			CurReg.pbRegions = pbRegions;
			CurReg.mActivity = mActivity;
			CurReg.myAppl = myAppl;
			CurReg.LoadBankList();		
		}
	};

	OnClickListener btnAllValCB_OnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Region CurReg = myAppl.getCurrentRegion();
			if (CurReg == null) return;
			CurReg.hState = hStateRegs;
			CurReg.mLayoutProgressBar = mLayoutProgressBarRegions;
			CurReg.pbRegions = pbRegions;
			CurReg.mActivity = mActivity;
			CurReg.myAppl = myAppl;
			CurReg.LoadBankList();		
		}
		
	};

	public void setBankLogo(Bank bank) throws IOException {
        int ident = mActivity.getResources().getIdentifier(bank.getLogonRes(), 
	        												"drawable", 
	        												mActivity.getPackageName().toString());
        if (ident != 0) {  
        	bank.BitmapBank = BitmapFactory.decodeResource(mActivity.getResources(), ident);
        } else
        	bank.BitmapBank = downloadImage(bank.HrefLogon);
	}
	public Bitmap downloadImage(String iUrl) throws IOException {
        try{
            HttpURLConnection connection = (HttpURLConnection)new URL(iUrl).openConnection();
            connection.connect();
            InputStream input= connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (Exception f) {
            f.printStackTrace();
        }
	    return null;			 
	}
	
	
	int iCountClickImgCB = 0, iCountLbl = 0;
	void initOnTouchLabels() {
		
		   OnTouchListener mValOnTouch = new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					iCountClickImgCB = 0;
					iCountLbl = 0;
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						v.setTop(v.getTop() + 10);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						v.setTop(v.getTop() - 10);
						v.performClick();
					}
					return true;
				}
		  }; 
		  
		  lblRateCbEur.setOnTouchListener(mValOnTouch);
		  lblRateCbUsd.setOnTouchListener(mValOnTouch);
		  imgChEur.setOnTouchListener(mValOnTouch);
		  imgChUsd.setOnTouchListener(mValOnTouch);
		  imgEur.setOnTouchListener(mValOnTouch);
		  imgUsd.setOnTouchListener(mValOnTouch);
		  
		  imgCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iCountClickImgCB ++;
//				if (iCountLbl == 5 && iCountClickImgCB > 10) {
//					myAppl.setSettingDevelopMode(true);
//				}
//				if (iCountLbl == 3 && iCountClickImgCB > 10) {
//					myAppl.setSettingDevelopMode(false);
//				}
//				ll_Develop.setVisibility(myAppl.isDevelopMode ? View.VISIBLE : View.GONE);				

			}
		  });
		  lblRateCb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					iCountLbl ++;
				}
		  });
		  
		  
	}
		 

	
	@SuppressLint("HandlerLeak")
	public void initHandlers() {
		
		hStateRegs = new Handler() {
		      public void handleMessage(Message msg) {
		    	  
  		    	    if (myAppl.ActiveFragment.ID != MainActivity.FRAME_BUTTONS)	
    		    		  return;
  		    		  
	    	        if (msg.what == ApplicationBestRates.HMSG_SHOW_PB_VERTICAL_FOR_BANKS) {
	    	        	pb_main.setVisibility(View.VISIBLE);
	    	        	pb_main.setMax(msg.arg1);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_INCREMENT_PROGRESS_VERT_MAIN) {
	    	        	pb_main.setProgress(msg.arg1);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_INCREMENT_PROGRESS_VERT_SEC) {
	    	        	if (pb_secondary.getMax() != msg.arg2)
	    	        		pb_secondary.setMax(msg.arg2);
	    	        	if (msg.arg1 >= pb_secondary.getMax()) {
	    	        		pb_secondary.setVisibility(View.GONE);
	    	        		return;
	    	        	} 
	    	        	pb_secondary.setVisibility(View.VISIBLE);
	    	        	pb_secondary.setProgress(msg.arg1);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_SHOW_PB_REGION) {
	    	        	mLayoutProgressBarRegions.setVisibility(msg.arg1 == 1 ? View.VISIBLE : View.GONE);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_SHOW_PB_MAIN) {
	    	        	pb_main.setVisibility(msg.arg1 == 1 ? View.VISIBLE : View.GONE);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_SHOW_PB_SECONDARY) {
	    	        	pb_secondary.setVisibility(msg.arg1 == 1 ? View.VISIBLE : View.GONE);
	    	        	return;
	    	        } if (msg.what == ApplicationBestRates.HMSG_RUN_PRE_EXECUTED) {
	    	        	//Region reg = myAppl.getCurrentRegion();
	    	        	//if (reg != null && reg.mt != null && reg.mt.getStatus() == Status.RUNNING)
	    	        	//myAppl.getCurrentRegion().mt.onPreExecute();
	    	        	mLayoutProgressBarRegions.setVisibility(View.VISIBLE);	    	        	
	    	        	lblStateRegs.setVisibility(View.VISIBLE);	    	        	
   		    	    } else if(msg.what == ApplicationBestRates.HMSG_PARSE_STACK_FILIALS) {
			    		  	// Разбираем %s пачку филиалов, всего пачек: %s 
		    	        	String sStr = String.format(getString(R.string.stParseStackFilials), String.valueOf(msg.arg1), String.valueOf(msg.arg2));
							if (msg.obj != null)
								sStr = (String) msg.obj + sStr; 
		    	        	lblStateRegs.setText(sStr);
		    	        	lblStateRegs.setVisibility(View.VISIBLE);	    	        			    	        	
				    		return;
			    	} else if (msg.what <= 0) return; 
	    	        
				    // обновляем TextView
					String sStr = getString(msg.what);

					if (msg.obj != null)
						sStr = (String) msg.obj + sStr; 
					
					if (msg.arg1 != 0) {
						sStr = sStr + " " + msg.arg1;
			    	}
					
			    	if (msg.arg2 != 0) {
			    		lblStateRegs.setTextColor(msg.arg2); 		    		
			    	}
			    	
			    	lblStateRegs.setText(sStr);
		      };
			};				
		    hStateCB = new Handler() {
			      public void handleMessage(Message msg) {
						        // обновляем TextView
						String sStr = getString(msg.what);
						
						if (msg.arg1 != 0) {
							sStr = sStr + " " + msg.arg1; 		    		
				    	}
				    	if (msg.arg2 != 0) {
				    		lblStateCB.setTextColor(msg.arg2); 		    		
				    	}
				    	
				    	lblStateCB.setText(sStr);
				  };
		    };
		
			
	} // initHandlers
			
	
	public void initButtons(){
	  	  btnBuyEUR.setOnClickListener(btnBuyEUR_OnClickListener);
    	  btnBuyUSD.setOnClickListener(btnBuyUSD_OnClickListener);
	  	  btnMap.setOnClickListener(btnMap_OnClickListener);
	  	  //btnAllValCB.setOnClickListener(btnAllValCB_OnClickListener);

	};
	
	public void initRateCBOnClickListener() {
		OnClickListener ShowToastEUR = new OnClickListener() {
											@Override
											public void onClick(View v) {
												ShowToastChangeRate(rateEUR_CB);
											}
										};
		
		OnClickListener ShowToastUSD = new OnClickListener() {
											@Override
											public void onClick(View v) {
												ShowToastChangeRate(rateUSD_CB);
											}
										};
										
	  imgEur.setOnClickListener(ShowToastEUR);
	  lblRateCbEur.setOnClickListener(ShowToastEUR);
	  imgChEur.setOnClickListener(ShowToastEUR);
	  imgUsd.setOnClickListener(ShowToastUSD);
	  lblRateCbUsd.setOnClickListener(ShowToastUSD);
	  imgChUsd.setOnClickListener(ShowToastUSD);	
	}
	
	public void setImageChange(){
		if (rateEUR_CB != null)
	        if (rateEUR_CB.Rate > rateEUR_CB.RatePrev) {
	        	imgChEur.setImageResource(R.drawable.vup);
	        	iTypeIconChValEUR = 1;
	        	lblRateCbEur.setTextColor(Color.RED);
	        } else if (rateEUR_CB.Rate < rateEUR_CB.RatePrev) { 
	        	imgChEur.setImageResource(R.drawable.vdown);
	        	iTypeIconChValEUR = 0;
	        	lblRateCbEur.setTextColor(getResources().getColor(R.color.holo_green_main));
	        }
		
		if (rateUSD_CB != null)	        
	        if (rateUSD_CB.Rate > rateUSD_CB.RatePrev) { 
	        	imgChUsd.setImageResource(R.drawable.vup);
	        	iTypeIconChValUSD = 1;
	        	lblRateCbUsd.setTextColor(Color.RED);
	        } else if (rateUSD_CB.Rate < rateUSD_CB.RatePrev) { 
	        	imgChUsd.setImageResource(R.drawable.vdown);
	        	iTypeIconChValUSD = 0;
	        	lblRateCbUsd.setTextColor(getResources().getColor(R.color.holo_green_main));
	        }
	}
	
	public void setDateRate(String sStr) {
		  myAppl.sDateRate_CB = sStr;
		  sDateRate = sStr;
		  setDateLabel(sDateRate);
	}
	
	public Date getDateRate(){
		Date res = new Date();		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
	try {
	 	res = format.parse(sDateRate);
	} catch (Exception exception) {
		Log.e(TAG, "Получено исключение в getDateRate()", exception);
	    }
	     return res;
	}
	
	public void setDateLabel(String aDateRate){
		if (aDateRate.length() == 0) return;
		String sStr = "";		
		sStr = String.format(getString(R.string.ratecb), aDateRate);
		CharSequence styledString = Html.fromHtml(sStr);
		lblRateCb.setText(styledString);
		return;
	}
	
	void ShowGlobMessage(String sStr){
		lblSelRegion.setText(sStr);
		lblSelRegion.setTextColor(Color.RED);
	}

	void initStart(){
		ll_rateCb.setVisibility(View.VISIBLE);
		lblSelRegion.setText(getString(R.string.Region));
		lblSelRegion.setGravity(Gravity.LEFT);
		setEnabledAll(true);
	}
	
	public void checkOnline(){
		if (isOnline()) {
			initStart();
		} else {
			setEnabledAll(false);
			ll_rateCb.setVisibility(View.INVISIBLE);
			lblSelRegion.setGravity(Gravity.CENTER_HORIZONTAL);
			ShowGlobMessage(getString(R.string.sNotLogin));
		}
	}
	
	public boolean isOnline() {
		  String cs = Context.CONNECTIVITY_SERVICE;
		  ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(cs);
		  if (cm.getActiveNetworkInfo() == null) {
		    return false;
		  }
		  return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	public void LastRate(){
		if (myAppl.rateEUR_CB != null & myAppl.rateUSD_CB != null 
				&& myAppl.rateEUR_CB.Rate + myAppl.rateUSD_CB.Rate != 0) {
			rateEUR_CB = myAppl.rateEUR_CB;
			rateUSD_CB = myAppl.rateUSD_CB;
			setLabelRate_CB();
			setImageChange();
			setEnabledAll(true);
		} else {
			Calendar c = Calendar.getInstance(); 
			c.add(Calendar.DATE, 1);
			LoadRate(c.getTime());
		}
	}
	
	public Boolean aResult = false;
	public void LoadRate(Date aDateRate){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		String sCurrDate = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru")).format(aDateRate);
		nameValuePairs.add(new BasicNameValuePair("date_req", sCurrDate)); 
		myAppl.mt = new RateCBPost("http://www.cbr.ru/scripts/XML_daily.asp", nameValuePairs, aResult);
		myAppl.mt.isRatePrev = false;
		myAppl.mt.mActivity = mActivity; 			
		myAppl.mt.mLayoutProgressBar = mLayoutProgressBarCB;
		myAppl.mt.execute();
	}
	
	public void LoadRatePrev() {
		Calendar c = Calendar.getInstance();
	  	c.setTime(getDateRate());
		c.add(Calendar.DATE, -1);
		String sDate = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru")).format(c.getTime());
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("date_req", sDate)); 
		
		myAppl.mtP = new RateCBPost("http://www.cbr.ru/scripts/XML_daily.asp", nameValuePairs, aResult);			
		myAppl.mtP.isRatePrev = true;
		myAppl.mtP.mActivity = mActivity; 			
		myAppl.mtP.mLayoutProgressBar = mLayoutProgressBarCB;
		myAppl.mtP.execute();
		myAppl.mtP.showProgress(true);
	}
	
	public String getToastStr(Rate val){
	    String sStr = val.NameVal + "\n"+ getString(R.string.sPred) + " " + val.RatePrev;
	double vCh = val.Rate - val.RatePrev;
	sStr = sStr + "\n"+ getString(R.string.sChange)  + " " +  (vCh > 0 ? "+": "") + String.format("%.2g%n", vCh);
		return sStr;  
	}
	
	public void ShowToastChangeRate(Rate val){
		if (val == null) 
			val = rateButton;
		
		String sStr = getToastStr(val);
	    Toast.makeText(mActivity.getApplicationContext(), sStr, Toast.LENGTH_SHORT).show();
	}
	
	public void LoadRegionFromAssets() throws JSONException, IOException {
		if (myAppl.regions.size() <= 1) {
			myAppl.regions.clear();
            JSONArray jSONArray = new JSONArray(this.readAssets("json/cities.json"));
            for (int i = 0; i < jSONArray.length(); ++i) {
                Region reg = new Region(jSONArray.optJSONObject(i), myAppl.regions.size());
                reg.mActivity = mActivity;
                reg.myAppl = myAppl;
                myAppl.regions.add(reg);
            }
            AfterLoadRegs();
		}
	}
	
    public String readAssets(String string) throws IOException {
        return StringUtils.convertStreamToString(mActivity.getAssets().open(string));
    }
	
	@SuppressLint("ClickableViewAccessibility")
	public void ManageRegions() {
	    String sRegionName    = mActivity.getSharedPreferences(ApplicationBestRates.av_APP_PREFERENCES, Context.MODE_PRIVATE).getString(ApplicationBestRates.av_LAST_REGION_NAME, "Санкт-Петербург"),
			   sRegionCode    = mActivity.getSharedPreferences(ApplicationBestRates.av_APP_PREFERENCES, Context.MODE_PRIVATE).getString(ApplicationBestRates.av_LAST_REGION_CODE, "spb");
	    Integer iCity_id      = mActivity.getSharedPreferences(ApplicationBestRates.av_APP_PREFERENCES, Context.MODE_PRIVATE).getInt(ApplicationBestRates.av_LAST_REGION_ID, 3);
	    int SelectedPos = 0; 
	    
		if (myAppl.mAdapterRegions == null) {
			myAppl.mAdapterRegions = new RegionArrayAdapter(mActivity, R.layout.spinner_item, myAppl.regions);
			myAppl.mAdapterRegions.setDropDownViewResource(R.layout.spinner_dropdown_item);
		}
		spRegions.setAdapter(myAppl.mAdapterRegions);
	    
		if (myAppl.regions.size() == 0) {
			Region region = new Region(sRegionName, sRegionCode, iCity_id, 0); // последнее значение
			region.mActivity = mActivity;
			myAppl.regions.add(0, region);
		} else {
			SelectedPos = myAppl.mAdapterRegions.getPosition(sRegionName);
		}
		myAppl.mAdapterRegions.notifyDataSetChanged();		
		spRegions.setSelection(SelectedPos);

		//---------
		// устанавливаем обработчик 
		spRegions.setOnTouchListener(new OnTouchListener() {
			@Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if (event.getAction() == MotionEvent.ACTION_UP) {
		        	if (myAppl.regions.size() <= 1) {
						tmpBeforeLoadRegion = spRegions.getSelectedItem().toString();
						try {
							LoadRegionFromAssets();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
		        	}
		        }
		        return (myAppl.regions.size() <= 1); // true - не показывать
	        }
	    });
	    
		// устанавливаем обработчик нажатия
		spRegions.setOnItemSelectedListener(new OnItemSelectedListener() {
			      @Override
			      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			    	myAppl.CurRegPosition = position;
			        SaveRegion();
			      }
			      @Override
			      public void onNothingSelected(AdapterView<?> arg0) {
			      }
		});
	}; // ManageRegions
	
	public void SaveRegion(){
		Region CurReg = myAppl.getCurrentRegion();
		
		if (CurReg == null) return;
		
		SharedPreferences mSettings = mActivity.getSharedPreferences(ApplicationBestRates.av_APP_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = mSettings.edit();
		editor.putString(ApplicationBestRates.av_LAST_REGION_NAME, CurReg.Name);
		editor.putString(ApplicationBestRates.av_LAST_REGION_CODE, CurReg.SubDomain);
		editor.putInt(ApplicationBestRates.av_LAST_REGION_ID, CurReg.city_id);
		
		editor.apply();
	}
	
//==============================================================================================================	  
	public class RateCBPost extends SendPost {
		
		String sRateList = "";
		boolean isRatePrev = false;
		
		public RateCBPost(String Url, List<NameValuePair> Params, Boolean Result) {
			super(Url, Params, Result);
			hStateCB.sendEmptyMessage(R.string.stConnCB);
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			super.onPostExecute(success);	
			String sMsg = "Сервер ЦБ, подлец, не вернул данные!!!!\n";
		    sRateList = sMessage;
		
			showProgress(true);
			if (success) {
				hStateCB.sendEmptyMessage(R.string.stParseRates);
				String tmpCharCode, tmpValue, tmpNominal, sCourTag, tmpName, tmpID, tmpNumCode;		
				tmpCharCode = tmpValue = tmpNominal = sCourTag = tmpName = tmpID = tmpNumCode = "";
				try {
			      XmlPullParser xpp = prepareXpp(sRateList);
			      while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) { // 1
			
			        switch (xpp.getEventType()) {
				        // начало тэга
			        	case XmlPullParser.START_TAG: // 2
				          sCourTag = xpp.getName();
				          if (sCourTag.equals("ValCurs") & !isRatePrev) {
					          for (int i = 0; i < xpp.getAttributeCount(); i++) {
						            if (xpp.getAttributeName(i).equals("Date")){
						            	setDateRate(xpp.getAttributeValue(i));
						            }
						          }
				          } else if (sCourTag.equals("Valute")) {
					          for (int i = 0; i < xpp.getAttributeCount(); i++) {
						            if (xpp.getAttributeName(i).equals("ID"))
						            	tmpID = xpp.getAttributeValue(i);
						          }
				          } 
			          
				          break;
				        // конец тэга
				        case XmlPullParser.END_TAG: // 3
				          sCourTag = "";
				          break;
				        // содержимое тэга
				        case XmlPullParser.TEXT: // 4
				          if (sCourTag.equals("CharCode")) {
				        	  tmpCharCode = xpp.getText();
				          } else if (sCourTag.equals("Value")) {
				        	  tmpValue = xpp.getText();
				          } else if (sCourTag.equals("Nominal")){
				        	  tmpNominal = xpp.getText();
				          } else if (sCourTag.equals("Name")){
				        	  tmpName = xpp.getText();
				          } else if (sCourTag.equals("NumCode")){
				        	  tmpNumCode = xpp.getText();
				          } 				          
				          break;
				          
				        default:
				          break;
			        } // case
			        
			        if (tmpCharCode.length() != 0 && tmpValue.length() != 0 
			        	&& tmpNominal.length() != 0 && tmpName.length() != 0
			        	&& tmpID.length() != 0 && tmpNumCode.length() != 0) {
			        	
			        	if  (tmpNumCode.contains("840") || tmpNumCode.contains("978")) {
			        	
				        	double ValRate = Double.valueOf(tmpValue.trim().replace(',','.')).doubleValue();
					        					        	
				        	if (tmpNumCode.contains("840")) { // tvUSD 
					        	if (!isRatePrev) {
						        	rateUSD_CB = new Rate(tmpCharCode, tmpNominal, tmpName);
						        	rateUSD_CB.ID = tmpID;
						        	rateUSD_CB.Rate = ValRate; 
						        	rateUSD_CB.NumCode = tmpNumCode;
					        	} else {
					        		rateUSD_CB.RatePrev = ValRate;
					        		rateUSD_CB.isPrevLoaded = true;
					        	}
				            } else { // tvEUR tmpNumCode.contains("978"))
					        	if (!isRatePrev) {
						        	rateEUR_CB = new Rate(tmpCharCode, tmpNominal, tmpName);
						        	rateEUR_CB.ID = tmpID;
						        	rateEUR_CB.Rate = ValRate; 
						        	rateEUR_CB.NumCode = tmpNumCode;
					        	} else {
					        		rateEUR_CB.RatePrev = ValRate;
					        		rateEUR_CB.isPrevLoaded = true;
					        	}
				        		
				            }
			        	}
			        	tmpCharCode = tmpValue = tmpNominal = sCourTag = tmpName = tmpID = tmpNumCode = "";
				        }
			        	xpp.next();// следующий элемент
				    } // while
				} catch (Exception exception) {
					Log.e(TAG, "Получено исключение", exception);
				}			      
				if (!isRatePrev) {
					myAppl.rateEUR_CB = rateEUR_CB;
					myAppl.rateUSD_CB = rateUSD_CB;
					setLabelRate_CB();
					LoadRatePrev();
				} else {
					setImageChange();					
				}
					
				
				setEnabledAll(true);
					
			} else { // success
				sMsg = sMsg + sRateList;
				ShowGlobMessage(sMsg);			
			}
			showProgress(false);
		}
		
		@Override
	    protected void onPreExecute() {
		    super.onPreExecute();
		    setEnabledAll(false);
		}
		
		@Override
		public void showProgress(final boolean show) {
			super.showProgress(show);
			ll_rateCb.setVisibility((show ?  View.INVISIBLE : View.VISIBLE));
		}
	} // class RateCBPost
//==============================================================================================================
	
	void AfterLoadRegs() {
		
		if (mLayoutProgressBarRegions.getVisibility()==View.VISIBLE) 
			hStateRegs.sendEmptyMessage(R.string.stAllOk);

		myAppl.mAdapterRegions.notifyDataSetChanged();				
	    setEnabledAll(true);
		spRegions.setSelection(myAppl.mAdapterRegions.getPosition(tmpBeforeLoadRegion), true);
		myAppl.CurRegPosition = spRegions.getSelectedItemPosition();
		spRegions.performClick();				
	}
			
	
	// устанавливает Enabled для тех, кто принимает участие в потоке
	void setEnabledAll(Boolean aVal){
	  lblSelRegion.setEnabled(aVal);
	  spRegions.setEnabled(aVal);
	  
	  btnBuyUSD.setEnabled(aVal);
	  btnBuyEUR.setEnabled(aVal);
	  btnMap.setEnabled(aVal);
	    	  
//	  if (action_refresh != null) 
//		  action_refresh.setEnabled(aVal);
	  
	}
		
	public static String getStrFromDate(Date aDate, String aFormat){
	String res = "";
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat(aFormat, new Locale("ru"));
	    res = sdf.format(aDate);
	} catch (Exception e) {
	    e.printStackTrace();
	}    	
	 	return res;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case R.id.action_refresh:
	        	 Region CurReg = myAppl.getCurrentRegion();
	        	 if ((CurReg == null)
        			 || (CurReg.mt != null && CurReg.mt.getStatus() == Status.RUNNING) 
        			 || (myAppl.mt != null && myAppl.mt.getStatus() == Status.RUNNING && myAppl.mtP.getStatus() == Status.RUNNING))
	        		 								return true; 
	        	 checkOnline();
       		     if (isOnline()) 
       		    	if (rateEUR_CB == null)
       		    		rateEUR_CB = myAppl.rateEUR_CB;
       		    	else
     					myAppl.rateEUR_CB = rateEUR_CB;
       		    		
    		    	if (rateUSD_CB == null)
    		    		rateUSD_CB = myAppl.rateUSD_CB;
    		    	else 
     					myAppl.rateUSD_CB = rateUSD_CB;

    		    	rateEUR_CB.Rate = 0;
  					rateUSD_CB.Rate = 0;

     		    	LastRate();
	             return true;
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }	 
	
	void setLabelRate_CB() {
		String sStr = String.format(getString(R.string.lblRateCb), myAppl.rateUSD_CB.Code  + "  -  " + myAppl.rateUSD_CB.Rate);   
		lblRateCbUsd.setText(sStr);
		sStr = String.format(getString(R.string.lblRateCb), myAppl.rateEUR_CB.Code + "  -  " + myAppl.rateEUR_CB.Rate);   
		lblRateCbEur.setText(sStr);
		setDateLabel(myAppl.sDateRate_CB);
	}
	
	void ShowProgress() {
		if (myAppl == null)
							return;
		
		Region reg = myAppl.getCurrentRegion();
		if (reg != null && reg.mt != null && reg.mt.getStatus() == Status.RUNNING) {
			Message msg = reg.mt.hState.obtainMessage(ApplicationBestRates.HMSG_RUN_PRE_EXECUTED);
			reg.mt.hState.sendMessage(msg);
		}
			
	}
    
}
