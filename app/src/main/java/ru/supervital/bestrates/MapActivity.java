package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_VAL;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class MapActivity extends AppCompatActivity implements OnMyLocationListener, OnBalloonListener {
	public static final String TAG = "bestrates.MapActivity";
	public static final String SHOW_MAP_BANK_FILIALS_LIST = "show_map_bank_filials_list";
	public static final String SHOW_ONE_FILIAL = "show_one_filial";

    MapController mMapController;
    OverlayManager mOverlayManager;
    MapView mapView;
    Overlay mOverlay;
    GeoPoint geoPoint;
    
    BalloonItem balloonItem;        

    Boolean flLoadFromFilials;
    
    ApplicationBestRates myAppl;
    Region CurRegion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		myAppl = (ApplicationBestRates) getApplication();
		CurRegion = myAppl.getCurrentRegion();
		
		initMap();
		Resources res = getResources();
		

		
		int notSetLoadAll = -1000;
		
		Intent intenet = getIntent();
		String NameOneFilial = intenet.getStringExtra(SHOW_ONE_FILIAL);		
		int iNumBankPos = intenet.getIntExtra(Region.LOAD_ALL_BANKS_ON_MAP, notSetLoadAll);
		
		if (iNumBankPos != notSetLoadAll)
			if (iNumBankPos != -1)
				CurRegion.CurBankPosition = iNumBankPos;
		
		String sStr = getIntent().getStringExtra(FL_Activity.FROM_FILIALS);
		flLoadFromFilials = (sStr != null && sStr.contains("1"));
		
		
		
		mOverlay.clearOverlayItems();
		
		int iCurBankPosition = 0, iCurFilialPosition = 1;  
		for (Bank bank : CurRegion.banks) {
			if (iCurBankPosition == CurRegion.CurBankPosition || iNumBankPos == -1) {
				for (Filial filial : bank.filials) {
				      if (NameOneFilial == null || filial.NameFilial.contains(NameOneFilial)) {
Log.d(TAG, filial.NameBank + " - " + filial.NameFilial + " begin ");
Log.d(TAG, "iCurFilialPosition = " + String.valueOf(iCurFilialPosition));
				    	  
				    	  if (filial.mGeoPoint != null) {
Log.d(TAG, filial.mGeoPoint.toString());

							final OverlayItem overlayFilial;

							overlayFilial = new OverlayItem(filial.mGeoPoint, res.getDrawable(R.drawable.shop));

					      	MyBalloonItem balloonFilial = new MyBalloonItem(this, filial.mGeoPoint);
					      	balloonFilial.setText(filial.NameBank + "\n" + filial.NameFilial + getBalloonText(bank));
					      	balloonFilial.setOnBalloonListener(this);
					      	balloonFilial.filial = filial;
Log.d(TAG, "BankPosition " + String.valueOf(iCurBankPosition));					      
							balloonFilial.BankPosition = iCurBankPosition;
							overlayFilial.setBalloonItem(balloonFilial);
							mOverlay.addOverlayItem(overlayFilial);
Log.d(TAG, filial.NameBank + " - " + filial.NameFilial + " end ");
				    	  } else {
				    		  
Log.e(TAG, "mGeoPoint == null");
Log.e(TAG, filial.getAddressParam());

				    	  } // else if(filial.mGeoPoint != null)
					      if (NameOneFilial != null && filial.NameFilial.contains(NameOneFilial))
					    		  		break;
				   } // if (NameOneFilial == null || filial.NameFilial.contains(NameOneFilial))
				   iCurFilialPosition++;
				} // for filials
			}
			iCurBankPosition++;
		} // for banks
		
		if (CurRegion.hState != null) {
			Message msg = CurRegion.hState.obtainMessage(ApplicationBestRates.HMSG_SHOW_PB_REGION, 0, 0);
			CurRegion.hState.sendMessage(msg);
			msg = CurRegion.hState.obtainMessage(ApplicationBestRates.HMSG_SHOW_PB_MAIN, 0, 0);
			CurRegion.hState.sendMessage(msg);
			msg = CurRegion.hState.obtainMessage(ApplicationBestRates.HMSG_SHOW_PB_SECONDARY, 0, 0);
			CurRegion.hState.sendMessage(msg);			
		}

        if (mOverlay != null)
    		mOverlayManager.addOverlay(mOverlay);
	
        // грузим это в отдельном потоке, так может выполнять запрос - поиска геокоординат для региона
        final Thread thread = new Thread()
        {
            @Override
            public void run() {
            	setZoomSpan(mOverlay.getOverlayItems().size());    	
            }
        };
        thread.start();        
        
        
		
	} // OnCreate
	
	String getBalloonText(Bank aBank) {
	      TYPE_VAL type_val;
	      if (flLoadFromFilials) {
	    	  type_val = CurRegion.type_val;
	      } else {
	    	  type_val = myAppl.settingValute;
	      }
	      
	      String res = "\n";
		  if (type_val == TYPE_VAL.tvEUR) {
			  res = res + "EUR: " +  String.format(getString(R.string.lblBalloonStrVal), 
					  									String.valueOf(aBank.EURbuy),
					  									"\n           ",		  
					  									String.valueOf(aBank.EURsell));

		  } else {
			  res = res + "USD: " +  String.format(getString(R.string.lblBalloonStrVal),  
					  									String.valueOf(aBank.USDbuy),
						  								"\n           ",
					  									String.valueOf(aBank.USDsell));
		  }
	      
		  return res;
	}
	
	void initMap() {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.showZoomButtons(true);
        mapView.showFindMeButton(true);
        mapView.showJamsButton(false);
        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        mOverlayManager.getMyLocation().setEnabled(true);
	    // Create a layer of objects for the map
		mOverlay = new Overlay(mMapController);
	}
		
	@Override
    public void onBalloonViewClick(BalloonItem balloonItem, View view)  {
//        OverlayItem item = balloonItem.getOverlayItem();
        MyBalloonItem balloon = (MyBalloonItem) balloonItem;
        Intent intent = new Intent(MapActivity.this, FL_Activity.class);
        CurRegion.CurBankPosition = balloon.BankPosition;
        intent.putExtra(FL_Activity.SHOW_ONE_FILIAL, balloon.filial.NameBank + balloon.filial.NameFilial);
        startActivity(intent);
	}

	@Override
	public void onMyLocationChange(MyLocationItem arg0) {}
	@Override
	public void onBalloonAnimationEnd(BalloonItem arg0) {}
	@Override
	public void onBalloonAnimationStart(BalloonItem arg0) {}
	@Override
	public void onBalloonHide(BalloonItem arg0) {}
	@Override
	public void onBalloonShow(BalloonItem arg0) {}


	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case android.R.id.home:
	        	 finish();
	             return true;		             
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }	 
	
    private void setZoomSpan(int count) {
    	if (count == 0)
    		return; 
    	
        List<OverlayItem> list = mOverlay.getOverlayItems();
        double maxLat, minLat, maxLon, minLon;
        maxLat = maxLon = Double.MIN_VALUE;
        minLat = minLon = Double.MAX_VALUE;
        for (int i = 0; i < count; i++){
            GeoPoint geoPoint = list.get(i).getGeoPoint();
            
            double lat = geoPoint.getLat();
            double lon = geoPoint.getLon();

            maxLat = Math.max(lat, maxLat);
            minLat = Math.min(lat, minLat);
            maxLon = Math.max(lon, maxLon);
            minLon = Math.min(lon, minLon);
        }
        

        double divLat = maxLat - minLat, divLon = maxLon - minLon;
        if (divLat >= 3 || divLon >= 3) {
        	Region region = myAppl.getCurrentRegion();
        	region.mGeoPoint = myAppl.LoadGeoPoint(region.Name); 
        	if (region.mGeoPoint != null)
        		mMapController.setPositionAnimationTo(region.mGeoPoint);
Log.d(TAG, region.Name + ": (divLat >= 3 || divLon >= 3)");        	
        } else {
        	mMapController.setZoomToSpan(maxLat - minLat, maxLon - minLon);
        	mMapController.setPositionAnimationTo(new GeoPoint((maxLat + minLat)/2, (maxLon + minLon)/2));
        }
    }
	    
//===========================================================	    
    class MyBalloonItem extends BalloonItem{
    	Context mContext;
    	Filial filial;
    	int BankPosition;
    	
    	public MyBalloonItem(Context context, GeoPoint geoPoint) {
            super(context, geoPoint);
            mContext = context;
        }
    }
//===========================================================
    

} // main class
