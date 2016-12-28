package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker.OnValueChangeListener;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_BANK_OPER;
import ru.supervital.bestrates.ApplicationBestRates.TYPE_VAL;

@SuppressLint("NewApi")
public class fraSettings extends BestratesFragment{
  NumberPicker npTopBanks;
  CheckBox chkShowTop;
  TextView lblInteres, lblOperation, txtDescrTopAll;
  RadioButton rbEuro, rbUsd, rbBuy, rbSale;
  RadioGroup rgValute, rgOperation;
	
  Boolean startNeedTop;
  Integer startTopBanks;
  
  public fraSettings() {
	ID = MainActivity.FRAME_SETTING;
  }	

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	  View rootView = inflater.inflate(R.layout.activity_setting, container, false);
      
      initVal();
      startNeedTop = myAppl.settingNeedTop;
      startTopBanks = myAppl.settingTopBanks;
    		  
      initView(rootView);
      initListeners();
      
      return rootView;
  }
  
  void initListeners() {
	  chkShowTop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			myAppl.setSettingNeedTop(isChecked);
			txtDescrTopAll.setVisibility(isChecked ? View.GONE : View.VISIBLE);
  		    npTopBanks.setVisibility(isChecked ? View.VISIBLE : View.GONE);
		}
	  });

	  npTopBanks.setOnValueChangedListener(new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			myAppl.settingTopBanks = picker.getValue();
	 	}
	   });
	  
	  rgValute.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (rgValute.getCheckedRadioButtonId()){
	         case R.id.rbEUR:
	        	 myAppl.setSettingValute(TYPE_VAL.tvEUR);
	        	 break;
	         case R.id.rbUSD:
	        	 myAppl.setSettingValute(TYPE_VAL.tvUSD);
	        	 break;	        	 
			}
		}
	});

	  rgOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (rgOperation.getCheckedRadioButtonId()){
	         case R.id.rbBuy:
	        	 myAppl.setSettingOperation(TYPE_BANK_OPER.boBuy);
	        	 break;	        	 
	         case R.id.rbSale:
	        	 myAppl.setSettingOperation(TYPE_BANK_OPER.boSale);
	        	 break;	        	 
			}
		}
	});

	  
  }
  
  void initView(View rootView) {
	  chkShowTop = (CheckBox) rootView.findViewById(R.id.chkShowTop);
	  chkShowTop.setChecked(myAppl.settingNeedTop);
	  
	  txtDescrTopAll = (TextView) rootView.findViewById(R.id.txtDescrTopAll);
	  txtDescrTopAll.setVisibility(myAppl.settingNeedTop ? View.GONE : View.VISIBLE);
	  
	  npTopBanks = (NumberPicker) rootView.findViewById(R.id.npTopBanks);
	  npTopBanks.setMaxValue(200);
	  npTopBanks.setMinValue(1);
	  npTopBanks.setValue(myAppl.settingTopBanks);
	  npTopBanks.setVisibility(myAppl.settingNeedTop ? View.VISIBLE : View.GONE);
	  
	  lblInteres   = (TextView) rootView.findViewById(R.id.lblInteres);
	  rgValute     = (RadioGroup) rootView.findViewById(R.id.rgValute);
	  rbEuro  = (RadioButton) rootView.findViewById(R.id.rbEUR);	  
	  rbUsd   = (RadioButton) rootView.findViewById(R.id.rbUSD);	  
	  if (myAppl.settingValute==TYPE_VAL.tvEUR)
		  rbEuro.setChecked(true);
	  else
		  rbUsd.setChecked(true);

	  lblOperation = (TextView) rootView.findViewById(R.id.lblOperation);
	  rgOperation  = (RadioGroup) rootView.findViewById(R.id.rgOperation);	  
	  rbBuy   = (RadioButton) rootView.findViewById(R.id.rbBuy);	  
	  rbSale  = (RadioButton) rootView.findViewById(R.id.rbSale);	  
	  if (myAppl.settingOperation==TYPE_BANK_OPER.boBuy)
		  rbBuy.setChecked(true);
	  else
		  rbSale.setChecked(true);

  }
  
  @Override
  public void onPause() {
      super.onPause();
      myAppl.setSettingTopBanks(myAppl.settingTopBanks); // записали в реестр, здесь иначе тормозит  
      
      if (startNeedTop != myAppl.settingNeedTop || startTopBanks != myAppl.settingTopBanks)
    	  		myAppl.getCurrentRegion().banks.clear();
  }  
  
}
