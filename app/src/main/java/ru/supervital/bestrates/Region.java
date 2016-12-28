package ru.supervital.bestrates;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import org.apache.http.NameValuePair;
import org.json.JSONObject;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import java.util.ArrayList;
import java.util.List;


public class Region implements Parcelable {
    public static final String TAG = "bestrates.Region";
	
	public static final String LOAD_ALL_BANKS_ON_MAP = "load_all_on_map";
	
	
	String   Name;
	String   SubDomain;
	Integer  city_id;
	Integer  Index;
	
	Double Latitude;
	Double Longitude;
	String Name_rp;
	String Name_pp;
	String StrRegion;
	
	
	ApplicationBestRates.TYPE_VAL type_val;

	ApplicationBestRates myAppl;
	AppCompatActivity mActivity;
	LinearLayout      mLayoutProgressBar;
	ProgressBar       pbRegions;
	
	
	public Handler hState;
	
	GeoPoint mGeoPoint;
	
	boolean flLoadFilialListAfterBankList = true;
	int CurBankPosition;
	
	int iTotalCountBanks; // сколько всего банков
	ArrayList<Bank> banks = new ArrayList<Bank>();
	ArrayList<Bank> DopInfos = null;

	public BL_ArrayAdapter mBankListArrayAdapter;
	
    public Region(String NameCity, String Code, Integer city_id, Integer Index){
    	super();
    	this.Name = NameCity;
    	this.SubDomain = Code;
    	if (city_id != null)
    		this.city_id = city_id;
    	this.Index      = Index;
    }	
    
    
    public Region(JSONObject jSONObject, Integer Index){
    	super();
        this.Name      = jSONObject.optString("name");
        this.city_id   = jSONObject.optInt("cityid");
        this.SubDomain = jSONObject.optString("subdomain");
        this.Longitude = jSONObject.optDouble("longitude");
        this.Latitude  = jSONObject.optDouble("latitude");
        this.Name_rp   = jSONObject.optString("name_gr");
        this.Name_pp   = jSONObject.optString("name_pp");
        this.StrRegion = jSONObject.optString("region");
    	this.Index     = Index;        
    }
    
	
	public Bank getCurrentBank() {
		if (banks.size() > 0)
			return (Bank) banks.get(CurBankPosition);
		return null;
	}

    
    @Override
    public String toString() {
        return Name;            // What to display in the Spinner list.
    }
    
    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {Name, SubDomain, city_id.toString(), Index.toString(), type_val.toString()});
	}

	public Region(Parcel in) {
		String[] data = new String[5];
		in.readStringArray(data);
		Name        = data[0];
		SubDomain   = data[1];
		city_id     = Integer.getInteger(data[2]);
		Index       = Integer.getInteger(data[3]);
		if (data[4].toString().contains(ApplicationBestRates.TYPE_VAL.tvALL.toString()))
			type_val   = ApplicationBestRates.TYPE_VAL.tvALL;
		else  
			type_val   = (data[4].toString().contains(ApplicationBestRates.TYPE_VAL.tvEUR.toString()) ? 
																ApplicationBestRates.TYPE_VAL.tvEUR : 
																ApplicationBestRates.TYPE_VAL.tvUSD);
		
	}	
	
	public static final Creator<Region> CREATOR = new Creator<Region>() {

		@Override
		public Region createFromParcel(Parcel source) {
			return new Region(source);
		}

		@Override
		public Region[] newArray(int size) {
			return new Region[size];
		}
	};

// грузить филиалы для банка с нидексом aBankindex, если "-1" - все банки
    void LoadFilialList(int aBankindex) {
    	Message msg = hState.obtainMessage(ApplicationBestRates.HMSG_SHOW_PB_VERTICAL_FOR_BANKS, banks.size(), 0);
    	hState.sendMessage(msg);
    	
    	for (int i=0; i<banks.size(); i++) {
    		if (i == aBankindex || aBankindex == -1) {
    			Bank bank = (Bank) banks.get(i);
    			bank.mActivity = mActivity; 			
    			bank.mProgressBar = mLayoutProgressBar;
    			bank.hState = hState;
    			bank.LoadFilialList();
    			if (aBankindex != -1) break;
    		}
    	}
    }

    void LoadBankList() {
		boolean aResult = false;
			mt = new BankListPostInRegion(myAppl.getRatesForRegionConnStr(), null, aResult);
			mt.mActivity = mActivity;
		    mt.mLayoutProgressBar = mLayoutProgressBar;
		    mt.mOperationProgressBar = pbRegions;
		    mt.hState = hState;
		    mt.flShowProgressOnPost = true;		    
		    mt.execute(mt.mUrl);
    }
//==============================================================================================================	
    public BankListPostInRegion mt;
    class BankListPostInRegion extends BL_Post2 {
    	
    	public BankListPostInRegion(String Url, List<NameValuePair> Params, Boolean Result) {
    		super(Url, Params, Result);
    	}
 
    	@Override
    	protected Boolean doInBackground(String... arg) {
    		Boolean success = super.doInBackground(arg);
	   		if (success) {
	   			//для текущего региона, если он не задан
	   			if (mGeoPoint == null)
	   				mGeoPoint = myAppl.LoadGeoPoint(Name);
	   												// Всего банков: 
				msg = hState.obtainMessage(R.string.stTotalBanks, banks.size(), Color.BLUE);
	        	hState.sendMessage(msg);
	   			
	        	GlobalTopSortSetting();
	        										// Всего банков: 	        	
				msg = hState.obtainMessage(R.string.stTotalBanks , banks.size(), Color.BLUE);
	        	hState.sendMessage(msg);
	        	
	        	if (flLoadFilialListAfterBankList) {
	        			LoadFilialList(-1); // 
	        	}
			}
	   		return success;
    	}

    	/**
    	 * после выполнения запроса
    	 * 
    	 */    	
    	@Override
    	protected void onPostExecute(final Boolean success) {
    		super.onPostExecute(success);
    	}
		
		/**
		 * перед выполнением запроса
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    showProgress(true);
		    SortByIndex();
		}
    } // BankListPostInRegion
//==============================================================================================================

	void ShowAllOnMap() {
	    Intent intent = new Intent(mActivity, MapActivity.class)
	    			 .putExtra(Region.LOAD_ALL_BANKS_ON_MAP, -1); // если -1 то все банки, иначе индекс банка
	    mActivity.startActivity(intent);
	}
	
	public void LoadGeoPointFromReestr() {
		if (mGeoPoint != null) return;
		if (Name.length()==0 || mActivity == null) return;
		String sGeoPoint = mActivity.getSharedPreferences(ApplicationBestRates.av_APP_PREFERENCES, Context.MODE_PRIVATE).getString(Name, "");
		if (sGeoPoint.length() != 0)
			mGeoPoint = new GeoPoint(Double.valueOf(sGeoPoint.split(" ")[0]), 
									 Double.valueOf(sGeoPoint.split(" ")[1]));
	}
	
	public void ParseXMLforGeoPoint(String sXML) {
		if (sXML.length() == 0) return;
		mGeoPoint = myAppl.ParseXMLforGeoPoint(sXML, Name);
	}
	
	public String getDopInfoByID(int bank_id) {
		String res= mActivity.getString(R.string.sForBankNotFoundDopInfo);
		if (DopInfos != null) {
			for (Bank bank : DopInfos) {
				if (bank.bank_id == bank_id) {
					res = bank.DopInfo;
					break;
				}
			}
		}
		return res;
	}
	
	
} // main class
