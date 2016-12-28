package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.widget.LinearLayout;
import org.apache.http.NameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Bank  implements Parcelable {
	public static final String TAG = "bestrates.Bank";
	static final String STR_FILIAL = " Филиалов";
	
	Integer bank_id;
	Bitmap BitmapBank;
	String NameBank;
	double USDbuy;    // курс покупки tvUSD
	double USDsell;   // курс продажи tvUSD
	double EURbuy;    // курс покупки tvEUR
	double EURsell;   // курс продажи tvEUR
	String HrefLogon; // адрес к логотипу банка
	String DateRate = "";
	String DopInfo = "";
	
	ArrayList<Filial> filials = new ArrayList<Filial>();
	FL_ArrayAdapter mFilialsArrayAdapter;
	
	Region            CurRegion;
	AppCompatActivity mActivity;
	LinearLayout      mProgressBar;
	public Handler    hState;

	int index = -1;
	
	public Bank(Integer bank_id, String NameBank, double USDbuy, double USDsell, double EURbuy, double EURsell, String HrefLogon) {
		super();
		this.bank_id   = bank_id;
		this.NameBank  = NameBank; 
		this.USDbuy    = USDbuy;
		this.USDsell   = USDsell;
		this.EURbuy    = EURbuy;
		this.EURsell   = EURsell;
		this.HrefLogon = HrefLogon;
	}

	public Bank(Integer aBank_id, String aDopInfo) {
		super();
		bank_id = aBank_id;
		DopInfo = aDopInfo;
	}
	
	
	public String getLogonRes() {
		if (HrefLogon.length() == 0) 
			return "";
		String res = new File(HrefLogon).getName();
		res = (res.substring(0, res.length()-4)).replace("-", "_");
		res = res.replaceAll("2014_07_22_1524501", "logob");
		return res;
	}
	
	static public String getNameBankFilialNL(String aNameBank) {
		String res = aNameBank.replace(STR_FILIAL, "\n" + STR_FILIAL.substring(1)); 
	  return res;
	}
	
	static public CharSequence getBoldNameBankFilNormNL(String aNameBank) {
		int i = aNameBank.indexOf(STR_FILIAL);
		String BL = "<font size=6><b>", EL = "</b></font>";
		if (i == -1) {
			CharSequence styledString = Html.fromHtml(BL + aNameBank + EL);
			return styledString;
		}
		CharSequence res = Html.fromHtml(BL + aNameBank.substring(0, i) +  EL + "<br>" + aNameBank.substring(i)); 
    	return res;
    }
	  
	/**
	 * Функция возвращает окончание для множественного числа слова на основании числа и массива окончаний
	 * @param  iNumber Integer Число на основе которого нужно сформировать окончание
	 * @return String
	 */
	static public String getNumEnding(int iNumber) {
		String[] aEndings = {"банк", "банков", "банка"};
		String sEnding;
	    int i;
	    iNumber = iNumber % 100;
	    if (iNumber>=11 && iNumber<=19) {
	        sEnding=aEndings[2];
	    }
	    else {
	        i = iNumber % 10;
	        switch (i)
	        {
	            case (1): sEnding = aEndings[0]; break;
	            case (2):
	            case (3):
	            case (4): sEnding = aEndings[1]; break;
	            default: sEnding = aEndings[2];
	        }
	    }
	    return sEnding;
	}	
	
    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {getStringFromBitmap(BitmapBank), NameBank, 
											String.valueOf(USDbuy), String.valueOf(USDsell), 
											String.valueOf(EURbuy), String.valueOf(EURsell),
											HrefLogon, 
											String.valueOf(index),
											DopInfo});
	}

	public Bank(Parcel in) {
		String[] data = new String[9];
		in.readStringArray(data);
		
		BitmapBank     = getBitmapFromString(data[0]);
		NameBank      = data[1];
		
		USDbuy        = Double.valueOf(data[2]);
		USDsell       = Double.valueOf(data[3]);
		EURbuy        = Double.valueOf(data[4]);
		EURsell       = Double.valueOf(data[5]);
		
		HrefLogon     = data[6];
		index         = Integer.valueOf(data[7]);
		
		DopInfo = data[8];
	}	
	
	@SuppressLint("NewApi")
	private Bitmap getBitmapFromString(String aStringBitmap) {
		if (aStringBitmap == null) return null;

	    byte[] decodedString = Base64.decode(aStringBitmap, Base64.DEFAULT);
	    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	    return decodedByte;
	}
	
	@SuppressLint("NewApi")
	private static String getStringFromBitmap(Bitmap bitmapPicture) {
		  	if (bitmapPicture == null) return "";

	        final int COMPRESSION_QUALITY = 100;
	        String encodedImage;
	        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
	        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, byteArrayBitmapStream);
	        byte[] b = byteArrayBitmapStream.toByteArray();
	        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
	        return encodedImage;
	    }	
	
	public static final Creator<Bank> CREATOR = new Creator<Bank>() {

		@Override
		public Bank createFromParcel(Parcel source) {
			return new Bank(source);
		}

		@Override
		public Bank[] newArray(int size) {
			return new Bank[size];
		}
	};
	
	boolean aResult;
	void LoadFilialList() {
		String sStr = getFilialsForBankConnStr();
		mt = new FilialsListPostInBank(sStr, null, aResult);
		mt.mActivity = mActivity;
		mt.CurBank = this;
		mt.mLayoutProgressBar = mProgressBar;
		mt.flShowProgressOnPost = true;
		mt.hState = hState;
		mt.execute(mt.mUrl);
	}
	
    public String getFilialsForBankConnStr() {
		return String.format("http://www.bmsk.ru/bmskapi/?cid=%d&currColor=%s&bid=%d", 
														new Object[]{CurRegion.city_id, "", bank_id});
    }
    
	
	
//==============================================================================================================	
    public FilialsListPostInBank mt;
    class FilialsListPostInBank extends FL_Post2 {
    	
    	public FilialsListPostInBank(String Url, List<NameValuePair> Params, Boolean Result) {
    		super(Url, Params, Result);
    	}
    	
		@Override
		protected void onPreExecute() {
		   super.onPreExecute();
		}
		   
		@Override
		protected Boolean doInBackground(String... arg) {
			super.doInBackground(arg);
			msg = hState.obtainMessage(ApplicationBestRates.HMSG_INCREMENT_PROGRESS_VERT_MAIN, index+1, 0);
			hState.sendMessage(msg);
			return true;
		}
		   
    	
    	@Override
    	protected void onPostExecute(final Boolean success) {
    		super.onPostExecute(success);
    		if (success) {
        		if (CurBank.index == CurRegion.banks.size()-1) {
        			msg = hState.obtainMessage(R.string.stLoadMap, 0, Color.BLUE);
                	hState.sendMessage(msg);
                	CurRegion.ShowAllOnMap();
        		}
    		}
    		
    	}
    }
//==============================================================================================================
	

} // main class