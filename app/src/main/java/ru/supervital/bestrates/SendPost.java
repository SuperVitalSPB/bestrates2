package ru.supervital.bestrates;

import android.annotation.TargetApi;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SendPost extends AsyncTask<String, Void, Boolean> {
  public static final String TAG = "bestrates.SendPost";
	
  public String 			 Url;
  public List<NameValuePair> Params;
  public AppCompatActivity   mActivity;
  public String 			 sMessage;
  public LinearLayout        mLayoutProgressBar;
  public ProgressBar         mOperationProgressBar;
  
  public boolean 			 flShowProgressOnPost = false;
  
  public String              sCodeParam = "utf-8";
  public String              sCodeResp = "windows-1251";
  
  Boolean IsMyMessage = false; // признак того, что сообщение будет показано через Handle, 
                               // отдельное сообщение покащываит не надо.
  
  Message msg;
  //---
  public ApplicationBestRates myAppl;
  public Region CurRegion;
  public Handler hState;

  
  /** 
   * конструктор
   */
  public SendPost(String aUrl, List<NameValuePair> aParams, Boolean Result){
	  super();
	  Url = aUrl;
	  Params = aParams;
  }

  
  /** 
   * перед выполнением
   */
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    initValues();    
    showProgress(true);
  }

  /** 
   * в отдельном потоке
   */
  @Override
  protected Boolean doInBackground(String... aStrings) {
  	try {
  		sMessage = postData(Url, Params);
  		return true;
	} catch (Exception e) {
		sMessage = e.getMessage();
		e.printStackTrace();
		return false;
	}
	
  }

  /** 
   * после выполнения
   */
  @Override  
  protected void onPostExecute(final Boolean success) {
	  super.onPostExecute(success);
	  showProgress(flShowProgressOnPost);
	  
	  if (!success && !IsMyMessage) {
			String sMsg = "Сервер, подлец, не вернул данные!!!!\n";
			sMsg = sMsg + sMessage;
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Ошибка");
			builder.setMessage(sMsg);
			builder.setCancelable(true);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	dialog.cancel();
			    }
			});
			AlertDialog dialog = builder.create();
			showProgress(false);			
			dialog.show();
	  }
	  
  }
	  
  /** 
   * нажали отменить
   */
  @Override
  protected void onCancelled() {
	super.onCancelled();
	showProgress(false);
  }
	  
  /** 
   * моя ф-я выполнение запроса
   */  
	public String postData(String aUrl, List<NameValuePair> aParam ) throws Exception {
	    // Создадим HttpClient и PostHandler
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(aUrl); 
	    String sResp = "";
      
	    try {
			if (aParam != null)
				httppost.setEntity(new UrlEncodedFormEntity(aParam,sCodeParam));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
	        // ответ
	        HttpEntity responseEntity = response.getEntity();
	       
	        if(responseEntity != null) {
	        	sResp = EntityUtils.toString(responseEntity, sCodeResp);
	        }
	    } 
	    catch (NetworkOnMainThreadException e) {
	    	throw e;
	    } catch (Exception e) {
	    	throw e;
	    }
	    return sResp;
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		if (!show && IsMyMessage && mOperationProgressBar != null) {
			HideProgressBar();
		} else if (mLayoutProgressBar != null)
			mLayoutProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}
		
	void HideProgressBar() {
		mOperationProgressBar.setVisibility(View.GONE);
	}
	
	static public XmlPullParser prepareXpp(String sXML) throws XmlPullParserException {
	    // получаем фабрику
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    // включаем поддержку namespace (по умолчанию выключена)
	    factory.setNamespaceAware(true);
	    // создаем парсер
	    XmlPullParser xpp = factory.newPullParser();
	    // даем парсеру на вход Reader
	    xpp.setInput(new StringReader(sXML));
	    return xpp;
	}
	
	public Date getDate(String sDate, String aFormat){
		Date res = new Date();		
		SimpleDateFormat format = new SimpleDateFormat(aFormat, new Locale("ru"));
        try {
         	res = format.parse(sDate);
        } catch (Exception exception) {
        	Log.e(TAG, "Получено исключение в getDate()", exception);
        }
 	    return res;
 	}
	
	void initValues() {
		if (mActivity == null) return;
		myAppl = (ApplicationBestRates) mActivity.getApplication();
		CurRegion = (Region) myAppl.getCurrentRegion();  
	}
	
	
}	

