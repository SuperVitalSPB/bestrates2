package ru.supervital.bestrates;

import android.support.v7.app.AppCompatActivity;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class Filial {
	Integer  fil_id;
	String   NameBank;
	String   NameFilial;
	String   Adress;
	String   TimeWork;
	String   Contacts;
	String   Services;
	AppCompatActivity mActivity;
	GeoPoint mGeoPoint;
	Integer  index = -1;
	
	String sRegionName;
	ApplicationBestRates MyAppl;

	public Filial (Integer  afil_id, String aNameBank, String aNameFilial, String aAdress, String aTimeWork, String aContacts, String aServices) {
		super();
		fil_id      = afil_id;
		NameBank    = aNameBank;
		NameFilial  = (aNameFilial == null ? " " : aNameFilial);
		Adress		= aAdress;
		TimeWork    = aTimeWork;
		Contacts    = aContacts;
		Services    = aServices;
	}
	
	public String getAddressParam() {
		if (Adress.length() == 0) return "";
		String res = Adress.replace(" пр-т", " проспект ");
				res = res.replace(" бул.", " бульвар ")
						.replace(" пгт.", " поселок ")
						.replace(" c.", " село ")
						.replace(" г.", " город ")
						.replace(" ст.", " станция ")												
						.replace(" р-н,", " район,")						
						.replace(" пр.,", " проспект, ")						
//						.replace(" д.", " деревня ")	меняет номер дома "д.22" на "деревня"					
						.replace(" п.", " поселок ");						
		if (res.contains("Санкт-Петербург")) {		
	// исключение, не красиво....				
			String sStr = "поселок Кузнечное";
			if (res.contains(sStr))
					res = res.replace(sStr, "Выборгский район, " + sStr);
			sStr = "Средний";
			if (res.contains(sStr) && !(res.contains("ВО") || res.contains("Васильевский")))		
					res = res.replace(sStr, sStr + " ВО ");			
			sStr = "Череповец";
			if (res.contains(sStr))			
				res = res.replace("Санкт-Петербург,", "");							
			
		}
		
		int i = res.indexOf("пом.");
		if (i > 0)
			res = res.substring(0, i-1);
		
		res= res.replace("  ", " ")
				.replace(" ", "+")
				.trim();
		
		return res;
	}
	
	@Override
	public String toString() {
		return NameBank + NameFilial;
	}
		
} // main class