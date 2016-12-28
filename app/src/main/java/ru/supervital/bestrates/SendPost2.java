package ru.supervital.bestrates;
// немного текста

import android.annotation.TargetApi;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SendPost2 extends AsyncTask<String, Void, Boolean> {
  public static final String TAG = "bestrates.SendPost2";
	
  public String 			 mUrl;
  public List<NameValuePair> Params;
  public AppCompatActivity   mActivity;
  public String 			 sMessage;
  public LinearLayout        mLayoutProgressBar;
  public ProgressBar         mOperationProgressBar;
  
  public boolean 			 flShowProgressOnPost = false;
  
  public String              sCodeParam = "utf-8";
  public String              sCodeResp = "windows-1251";
  
  Boolean IsMyMessage = false; // признак того, что сообщение будет показано через Handle, 
                               // отдельное сообщение показывать не надо.
  
  Message msg;
  //---
  public ApplicationBestRates myAppl;
  public Region CurRegion;
  public Handler hState;

  
  /** 
   * конструктор
   */
  public SendPost2(String aUrl, List<NameValuePair> aParams, Boolean Result){
	  super();
	  mUrl = aUrl;
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
 * @throws IOException 

   */
  @Override
  protected Boolean doInBackground(String... aStrings) {
      Log.d(TAG, "Request: " + this.mUrl);
      DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(this.mUrl);
      httpGet.addHeader("Authorization", "b6ad1622-2681-44f7-9abe-ff16f7fd0dca");
      try {
    	  sMessage = EntityUtils.toString((HttpEntity)defaultHttpClient.execute((HttpUriRequest)httpGet).getEntity());
          return true;
      }
      catch (Throwable e) {
          Log.e(TAG, "Request failed: " + this.mUrl);
          sMessage = e.getLocalizedMessage();
          e.printStackTrace();
          return false;
      }
  }
	  
  /** 
   * после выполнения
   */
  @Override  
  protected void onPostExecute(final Boolean success) {	  
      if (!this.isCancelled()) {
          super.onPostExecute(success);
      }
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

