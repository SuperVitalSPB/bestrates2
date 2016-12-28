package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// главный класс приложения
@SuppressLint("DefaultLocale")
public class ApplicationBestRates extends Application {
	enum TYPE_VAL  {tvEUR, tvUSD, tvALL};  // тип валюты
	enum TYPE_BANK_OPER {boBuy, boSale};   // тип операции, который совершает банк продает||покупает


	// имя файла настроек
    public static final String av_APP_PREFERENCES = "supervital.bestrates.settings";
	// av - application value
    public static final String av_NEED_TOP_BANKS      = "NEED_TOP_BANKS";        
    public static final String av_TOP_BANKS           = "TOP_BANKS";
    public static final String av_TYPE_VALUTE         = "TYPE_VALUTE";    
    public static final String av_TYPE_BANK_OPERATION = "TYPE_BANK_OPERATION";    
    //
    public static final String av_DEVELOP_MODE = "develop_mode";    
    //
    // запущенно ли приложение в режиме тестирования для разработки
    // по умолчанию нет
    public Boolean isDevelopMode = false;
    public Integer iCountThErr = 0; // для отладки    
    public Boolean isPaidVersion = false; //
    
	public static final String av_LAST_REGION_NAME = "lastregion_name";
	public static final String av_LAST_REGION_CODE = "lastregion_code";
	public static final String av_LAST_REGION_ID   = "lastregion_reg_id";
	
	public static final String CONN_IDEAL = "http://%s.bmsk.ru";	
	
	public static final Integer HMSG_SHOW_PB_VERTICAL_FOR_BANKS   = -10000;
	public static final Integer HMSG_INCREMENT_PROGRESS_VERT_MAIN = -10001;
	public static final Integer HMSG_INCREMENT_PROGRESS_VERT_SEC  = -10002;
	public static final Integer HMSG_SHOW_PB_REGION               = -10003;
	public static final Integer HMSG_SHOW_PB_MAIN                 = -10004;
	public static final Integer HMSG_SHOW_PB_SECONDARY            = -10005;
	// получение пачки филипалов из большого списка	
	public static final Integer HMSG_LOAD_STACK_FILIALS           = -10006;
	// запустить процедуру ту, которая выполняется перед выполнением запроса
	public static final Integer HMSG_RUN_PRE_EXECUTED			  = -10007;
	// разбор пачки филиалов %s из %s
	public static final Integer HMSG_PARSE_STACK_FILIALS          = -10008;	
	// всего филиалов %s
	public static final Integer HMSG_COUNT_FILIALS                = -10009;	
	

	BestratesFragment ActiveFragment;
//------------	
	fraButtons.RateCBPost mt, mtP;
//---
	  
//---	  
	String sDateRate_CB;
	Rate rateUSD_CB, rateEUR_CB;

	ArrayList<Region> regions = new ArrayList<Region>();
	public RegionArrayAdapter mAdapterRegions;

	ArrayList<BestratesFragment> AppFrames = new ArrayList<BestratesFragment>();
			
	
	Integer CurRegPosition;
	
	boolean flListFilialsAfterOneF = false; // показать список всех филиалов после показа одного филиала, из списка, на карте.

	Boolean   settingNeedTop;        // Глобальная настройка: использовать ТОП банков, true - да, false - показывать весь список 	
	Integer   settingTopBanks;       // Глобальная настройка: какой ТОП банков брать, 0 - не брать ничего
	TYPE_VAL  settingValute;         // Глобальная настройка: какую валюту использовать: enum TYPE_VAL
	TYPE_BANK_OPER settingOperation; // Глобальная настройка: какая операция банка нам интересна: enum TYPE_BANK_OPER
	
	
	@Override
    public void onCreate() {
        super.onCreate();
        LoadAppSettings();
    }

	void LoadAppSettings() {
	    // запущенно ли приложение в режиме тестирования для разработки, по умолчанию нет
		// isDevelopMode = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getBoolean(av_DEVELOP_MODE, false);        				
		//
		// использовать ТОП банков, 1-да, иначе нет 	
		settingNeedTop  = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getBoolean(av_NEED_TOP_BANKS, true);        				
		// какой ТОП банков брать, 0 - не брать ничего 
		settingTopBanks = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getInt(av_TOP_BANKS, 5);        				
		// какую валюту использовать: enum TYPE_VAL
		int iVal = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getInt(av_TYPE_VALUTE, 0);
		settingValute   = (iVal==0 ? TYPE_VAL.tvEUR : TYPE_VAL.tvUSD);  
		// какая операция банка нам интересна: enum TYPE_BANK_OPER
		int iOper = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getInt(av_TYPE_BANK_OPERATION, 1);
		settingOperation = (iOper==0 ? TYPE_BANK_OPER.boBuy : TYPE_BANK_OPER.boSale);  
	}
	
	public Region getCurrentRegion() {
		if (regions.size() <= 1 && CurRegPosition == null) return null;
		return (Region) regions.get(CurRegPosition);
	}

	public String getCurrentRegionName() {
		Region region = getCurrentRegion();
		if (regions == null) return null;
		return region.Name;
	}

	
	
	public String getStringFromAssetFile(String FileName)
	{
		byte[] buffer = null;
		InputStream is;
		try {
		    is = getAssets().open(FileName);
		    int size = is.available();
		    buffer = new byte[size];
		    is.read(buffer);
		    is.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}

		String str_data = new String(buffer);
		return str_data;		
	}
	
	public GeoPoint ParseXMLforGeoPoint(String sXML, String sAdressParam) {
        String sRes = null;
		try {
          String tmpGeoPoint = "", sCourTag = "";						
          XmlPullParser xpp = SendPost.prepareXpp(sXML);
	      while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) { // 1
	
	        switch (xpp.getEventType()) {
		        // начало тэга
	        	case XmlPullParser.START_TAG: // 2
	        		sCourTag = xpp.getName();
		          break;
		        // конец тэга
		        case XmlPullParser.END_TAG: // 3
		        	sCourTag = "";
		          break;
		        case XmlPullParser.TEXT: // 4
		          if (sCourTag.equals("pos")) {
		        	  tmpGeoPoint = xpp.getText();
		          } 				          
		          break;
		        default:
		          break;
	        } // case
	        
	        if (tmpGeoPoint.length() != 0) {
	        	sRes = tmpGeoPoint.split(" ")[1] + " " + tmpGeoPoint.split(" ")[0];

				AppCompatActivity mActivity = getCurrentRegion().mActivity;
	        	if (mActivity != null) {
					SharedPreferences mSettings = mActivity.getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
					Editor editor = mSettings.edit();
					editor.putString(sAdressParam, sRes);
					editor.apply();
	        	}
	        	break;
	        } 
	        	
        	xpp.next();// следующий элемент
		    } // while
		} catch (Exception exception) {
			Log.e("ParseXMLforGeoPoint", "Получено исключение: " + exception.getMessage(), exception);
		}		
		return getGeoPoint(sRes);
	}

	public GeoPoint LoadGeoPointFromReestr(String sAdressParam) {
		if (sAdressParam.length()==0) return null;
		AppCompatActivity mActivity = getCurrentRegion().mActivity;
		if (mActivity == null) return null;
		
		String sGeoPoint = mActivity.getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE).getString(sAdressParam, "");
		GeoPoint res = null;
		if (sGeoPoint.length() != 0) 
			res = getGeoPoint(sGeoPoint);
		
		return res;
	}
	
	public GeoPoint getGeoPoint(String sStr) {
		if (sStr == null) 
			return null;
		return new GeoPoint(Double.valueOf(sStr.split(" ")[0]), 
				            Double.valueOf(sStr.split(" ")[1]));		
	}
	
	// не нашли в реестре, поэтому загрузим в ЭТОМ потоке с Яндекса
	public GeoPoint LoadGeoPoint(String sAdressParam) {
		GeoPoint res = LoadGeoPointFromReestr(sAdressParam);
		if (res == null) {
			try {
	    	    // Создадим HttpClient и PostHandler
	    	    HttpClient httpclient = new DefaultHttpClient();
	    	    HttpPost httppost = new HttpPost("http://geocode-maps.yandex.ru/1.x/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("geocode", sAdressParam));
				nameValuePairs.add(new BasicNameValuePair("results", "1"));

				HttpEntity paramsEnty = new UrlEncodedFormEntity(nameValuePairs);
				httppost.setEntity(paramsEnty);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));					
				HttpResponse response = httpclient.execute(httppost);
		        // ответ
		        HttpEntity responseEntity = response.getEntity();
		       
		        if(responseEntity != null) {
		        	String sResp = EntityUtils.toString(responseEntity, "UTF-8");
		        	res = ParseXMLforGeoPoint(sResp, sAdressParam);
		        }
	    		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public void setSettingDevelopMode(Boolean aVal) {
		isDevelopMode = aVal;
		// Глобальная настройка: isDevelopMode
    	SharedPreferences mSettings = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = mSettings.edit();
    	editor.putBoolean(av_DEVELOP_MODE, isDevelopMode);
    	editor.apply(); 
	}
	
	public void setSettingNeedTop(Boolean aVal) {
		// Глобальная настройка: использовать ТОП банков, 1-да, иначе нет 	
		settingNeedTop = aVal;
    	SharedPreferences mSettings = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = mSettings.edit();
    	editor.putBoolean(av_NEED_TOP_BANKS, settingNeedTop);
    	editor.apply(); 
	}
	
	public void setSettingTopBanks(Integer aVal) {  
		// Глобальная настройка: какой ТОП банков брать, 0 - не брать ничего
		settingTopBanks = aVal;
    	SharedPreferences mSettings = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = mSettings.edit();
    	editor.putInt(av_TOP_BANKS, settingTopBanks);
    	editor.apply();    	   
	}
	
	public void setSettingValute(TYPE_VAL aVal) {  
		// Глобальная настройка: какую валюту использовать: enum TYPE_VAL
		settingValute = aVal;
		setSettingValute(aVal == TYPE_VAL.tvEUR ? 0 : (aVal == TYPE_VAL.tvEUR ? 1 : 2));
	}
	
	public void setSettingValute(Integer aVal) {  
		// Глобальная настройка: какую валюту использовать: enum TYPE_VAL
		settingValute = (aVal == 0 ? TYPE_VAL.tvEUR : (aVal == 1 ? TYPE_VAL.tvUSD : TYPE_VAL.tvALL));
		
    	SharedPreferences mSettings = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = mSettings.edit();
    	editor.putInt(av_TYPE_VALUTE, aVal);
    	editor.apply();    	   
	}

	public void setSettingOperation(TYPE_BANK_OPER aVal) {  
		// Глобальная настройка: какая операция банка нам интересна: enum TYPE_BANK_OPER
		settingOperation = aVal;
		setSettingOperation(aVal==TYPE_BANK_OPER.boBuy ? 0 : 1);
	}
	
	public void setSettingOperation(Integer aVal) {  
		// Глобальная настройка: какая операция банка нам интересна: enum TYPE_BANK_OPER
		settingOperation = (aVal == 0 ? TYPE_BANK_OPER.boBuy : TYPE_BANK_OPER.boSale);
		
    	SharedPreferences mSettings = getSharedPreferences(av_APP_PREFERENCES, Context.MODE_PRIVATE);
    	Editor editor = mSettings.edit();
    	editor.putInt(av_TYPE_BANK_OPERATION, aVal);
    	editor.apply();    	   
	}
	
    void SendMail (AppCompatActivity mActivity) {
    	final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
        		new String[] { getString(R.string.myemail) });
        // Тема
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
        		getString(R.string.subj_email)  + " " + getString(R.string.app_name));
        // Поехали!
        mActivity.startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));    	
    }
    
    public String getRatesForRegionConnStr() {
        return String.format("http://www.bmsk.ru/bmskapi/?currency&cid=%d", getCurrentRegion().city_id);
    }

    public String getDopInfoConnStr() {
        return String.format("http://www.bmsk.ru/bmskapi/?cid=%d&currency&exchangeinfo", getCurrentRegion().city_id);
    }
    
}
