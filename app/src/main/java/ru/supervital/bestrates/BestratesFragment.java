package ru.supervital.bestrates;
// немного текста

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BestratesFragment extends Fragment {

	AppCompatActivity mActivity;
	ApplicationBestRates myAppl;
	
	int ID;

	public BestratesFragment() {
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	initVal();
        return null;
    }	
    
	public void initVal() {
		mActivity = (AppCompatActivity) getActivity();
		if (mActivity != null)
			myAppl = (ApplicationBestRates) mActivity.getApplication();
	}
}
