package ru.supervital.bestrates;

import android.graphics.Color;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import java.util.List;

public class FL_Post2 extends SendPost2 {
	Bank CurBank;
	Boolean needShowFF = false; // показывать ли сколько филиалов загрузили из большой кучи
	int iCountFilials = 0;
	
	public FL_Post2(String Url, List<NameValuePair> Params, Boolean Result) {
		super(Url, Params, Result);
	}
	
	@Override
	protected Boolean doInBackground(String... arg) {
		if (CurBank.filials.size() == 0) {
			try {
						// Получение списка филиалов
	        	msg = hState.obtainMessage(R.string.stGetFilials, 0, Color.BLUE, CurBank.index + 1 + ". " + CurBank.NameBank + "\n");
	        	hState.sendMessage(msg);
	        	super.doInBackground(arg);

	        			// Разбор списка филиалов
				msg = hState.obtainMessage(R.string.stParseFilials, 0, Color.BLUE, CurBank.index + 1 + ". " + CurBank.NameBank + "\n");
				hState.sendMessage(msg);
	        	
	        	Integer fil_id;
            	Double latitude, longitude;
            	
                String NameBank, NameFilial, 
                		Adress, TimeWork, 
                		Contacts="", Services="";		
                
                JSONObject rootobj = new JSONObject(sMessage);
                JSONArray array = rootobj.getJSONArray("markers");

                iCountFilials = array.length();
				msg = hState.obtainMessage(ApplicationBestRates.HMSG_COUNT_FILIALS, iCountFilials, 0);
				hState.sendMessage(msg);
                
                
                for( int i = 0; i < array.length(); i++){
                    JSONObject o = array.getJSONObject(i);                	

                    latitude  = o.getDouble("latitude");
                    longitude = o.getDouble("longitude");
                    
                    JSONArray arrVal = o.getJSONArray("banks");
                    JSONObject ob = arrVal.getJSONObject(0);
                    		
				    fil_id     = ob.getInt("id");
 			    	NameBank   = ob.getString("bankname");
            		NameFilial = ob.getString("name");
            		Adress     = ob.getString("address");
		    		TimeWork   = ob.getString("schedule").replaceAll("&mdash;", "-");
		    		Contacts   = ob.getString("phone");
		    		Services   = ob.getString("description");

            	
				    Filial filial = new Filial(fil_id, NameBank, NameFilial, Adress, TimeWork, Contacts, Services);
            				    		
				    filial.mGeoPoint = new GeoPoint(latitude, longitude);
            		
				    filial.index = i+1; 
            				    		
            		CurBank.filials.add(CurBank.filials.size(), filial);

    		        msg = hState.obtainMessage(ApplicationBestRates.HMSG_INCREMENT_PROGRESS_VERT_SEC, i, iCountFilials);
    		        hState.sendMessage(msg);
                }	                            
			} catch (Exception e) {
				sMessage = e.getMessage();
				e.printStackTrace();
				return false;
	        }
		}
		return true;
	}
	
	
} // main class
