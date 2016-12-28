package ru.supervital.bestrates;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_BANK_OPER;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_VAL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class BL_Post2 extends SendPost2 {
    public static final String TAG = "bestrates.BL_Post2";

	Boolean flDopInfoLoad = false; //флаг, что мы грузим дополнительную информацию для банка
	
    final String repl1 = "Список отделений банка с телефонами";
    final String repl2 = "Отделения банка на карте города";
    
	public BL_Post2(String Url, List<NameValuePair> Params, Boolean Result) {
		super(Url, Params, Result);
		sCodeResp = "utf-8";
	}
	
	// Метод выполняющий запрос в фоне, в версиях выше 4 андроида, запросы в главном потоке выполнять
	// нельзя, поэтому все что вам нужно выполнять - выносите в отдельный тред
	@Override
	protected Boolean doInBackground(String... arg) {
		if (CurRegion.banks.size() == 0) {
			try {
													// Получение списка банков
	        	msg = hState.obtainMessage(R.string.stGetBanks, 0, Color.BLUE);
	        	hState.sendMessage(msg);
				super.doInBackground(arg);
													// Разбор списка банков
				msg = hState.obtainMessage(R.string.stParseBanks, 0, Color.BLUE);
	        	hState.sendMessage(msg);
	        	
	        	Integer bank_id;
				String BName = "", 
					   HrefLogon = "";
				
				Double dUSDbuy, dUSDsell, 
					   dEURbuy, dEURsell; 


                JSONObject rootobj = new JSONObject(sMessage);
                JSONArray array = rootobj.getJSONArray("main");
                for( int i = 0; i < array.length(); i++){
                    JSONObject o = array.getJSONObject(i);

                    bank_id = o.getInt("bank_id");
// DEVELOP                    
// if (!(bank_id >= 66 && bank_id<= 70)) continue; 
//DEVELOP
                    
                    // o.get("bank_id") + ": " + 
		    		BName = o.getString("bank_name");
		    		
		    		JSONArray arrVal = o.getJSONArray("sell");
                    JSONObject oVal = arrVal.getJSONObject(0);
		    		dUSDsell   = oVal.getDouble("value");
		    		oVal = arrVal.getJSONObject(1);
		    		dEURsell   = oVal.getDouble("value");
		    		
		    		arrVal = o.getJSONArray("buy");
                    oVal = arrVal.getJSONObject(0);
		    		dUSDbuy   = oVal.getDouble("value");
		    		oVal = arrVal.getJSONObject(1);
		    		dEURbuy   = oVal.getDouble("value");
		    		
		        	HrefLogon = o.getString("logo");
			        Bank bank = new Bank(bank_id, BName, dUSDbuy, dUSDsell, dEURbuy, dEURsell, HrefLogon);

			        setBankLogo(bank);
			        bank.index = i;
			        bank.CurRegion = CurRegion;
			        
			        CurRegion.banks.add(CurRegion.banks.size(), bank);
			        
			        if (hState == null)	Log.d(TAG, "hState == null"); else Log.d(TAG, "hState != null");
					// Разбор списка банков
			        msg = hState.obtainMessage(R.string.stParseBanks, i, Color.BLUE);
			        hState.sendMessage(msg);
                }	                            
			} catch (Exception e) {
				sMessage = e.getMessage();
				e.printStackTrace();
				return false;
            }
		} else  if (myAppl.isPaidVersion && flDopInfoLoad) {
 										// Получение дополнительной информации
			msg = hState.obtainMessage(R.string.stGetDopInfo, 0, Color.BLUE);
			hState.sendMessage(msg);
			mUrl = myAppl.getDopInfoConnStr();
			
			super.doInBackground(arg);
			
			try {
												// Разбор дополнительной информации
				msg = hState.obtainMessage(R.string.stParseDopInfo, 0, Color.BLUE);
				hState.sendMessage(msg);
				
				JSONObject rootobj = new JSONObject(sMessage);
				JSONArray array = rootobj.getJSONArray("excgange_info");
				CurRegion.DopInfos = new ArrayList<Bank>(CurRegion.banks.size());
				
				for (int i=0; i<array.length(); i++){
					JSONObject o = array.getJSONObject(i);
					String sDopInfo = o.getString("info") + "\n" + 
										mActivity.getString(R.string.sPhoneCC) + "\n" + 
										o.getString("phone");
					
					Bank bank = new Bank(o.getInt("bank_id"), sDopInfo);
					CurRegion.DopInfos.add(bank);
				}	      
				
			} catch (Throwable e) {
				sMessage = e.getMessage();
				e.printStackTrace();
				return false;
			}

		}
		return true;
	} // doInBackground
	
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
	
	void SortByIndex() {
		Collections.sort(CurRegion.banks, new Comparator<Bank>() {
			  @Override
			  public int compare(Bank o1, Bank o2) {
				return Integer.valueOf(o1.index).compareTo(o2.index);
			  }
		});		
	}
	
	  /** 
	   * после выполнения
	   */
	  @Override  
	  protected void onPostExecute(final Boolean success) {
		  super.onPostExecute(success);
		  CurRegion.iTotalCountBanks = CurRegion.banks.size();
	  }
	
	  void sortBySettings() {
			Collections.sort(CurRegion.banks, new Comparator<Bank>() {
				  @Override
				  public int compare(Bank o1, Bank o2) {
						int res = 0;
						if (myAppl.settingValute == TYPE_VAL.tvEUR) {
							if (myAppl.settingOperation == TYPE_BANK_OPER.boBuy) {
								res = Double.valueOf(o1.EURbuy).compareTo(o2.EURbuy) * -1;
							} else {
								res = Double.valueOf(o1.EURsell).compareTo(o2.EURsell);
							}
						} else {
							if (myAppl.settingOperation == TYPE_BANK_OPER.boBuy) {
								res = Double.valueOf(o1.USDbuy).compareTo(o2.USDbuy) * -1;
							} else {
								res = Double.valueOf(o1.USDsell).compareTo(o2.USDsell);
							}
						}
							
						return res;
				  }
			});		
	  }
	
	  void deleteByTop() {
		  if (!myAppl.settingNeedTop)
			  		return;		
		  
		  for (int i=CurRegion.banks.size()-1; myAppl.settingTopBanks < CurRegion.banks.size(); i--) {
			  CurRegion.banks.remove(i);
		  }
	  }
	  
	void setIndexBySort() {
	 // проставляем Index после удаления левых и остатков только Топ`ов
	 // это надо для правильной работы загрузки филиалов, отображения хода и остановки
		  int i = 0;
		  for (Bank bank : CurRegion.banks) {
			  bank.index = i;
			  i++;
		  }
	}
	
	public void GlobalTopSortSetting() {
		// эти три процедуры должны идти вместе, иначе подумать хорошо
    	sortBySettings();
		deleteByTop();
		setIndexBySort();
	}
	  
	  
	  
} // main class 
