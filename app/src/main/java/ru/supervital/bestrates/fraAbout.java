package ru.supervital.bestrates;
// немного текста

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class fraAbout extends BestratesFragment {
	public static final String TAG = "bestrates.fraAbout";

	TextView txtVer, txtThanks, txtAutor;
	ImageView star;
	Animation anim;
	
	public fraAbout() {
    	ID = MainActivity.FRAME_ABOUT;
	}	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.activity_about, container, false);
    	initVal();
    	initView(rootView);
		star.startAnimation(anim);
        return rootView;
    }
    
    void initView(View rootView) {
    	txtAutor = (TextView) rootView.findViewById(R.id.txtAutor);
    	txtAutor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myAppl.SendMail((AppCompatActivity) getActivity());
			}
		});
    
    	
    	txtVer = (TextView) rootView.findViewById(R.id.txtVer);
    	txtVer.setText(String.format(getString(R.string.about2), getVersionName()));
    	
    	txtThanks = (TextView) rootView.findViewById(R.id.txtThanks);
    	
    	star = (ImageView)rootView.findViewById(R.id.star);
		anim = AnimationUtils.loadAnimation(mActivity, R.anim.anim);
		anim.setAnimationListener(new AnimationListener () {
			@Override
			public void onAnimationEnd(Animation animation) {
				star.setVisibility(View.GONE);
				Log.d(TAG, "animation end");
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				Log.d(TAG, "animation repeat");
			}
			@Override
			public void onAnimationStart(Animation animation) {
				star.setVisibility(View.VISIBLE);
				Log.d(TAG, "animation start");
			}
			
		});
    	
    }
    
    String getVersionName() {
    	String res = null;
    	try {
    		res = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
    	}
    	catch (final NameNotFoundException e) {
    		Log.e(mActivity.getClass().getSimpleName(), "Could not get version from manifest.");
    	}
    	if (res == null) {
    		res = "unknown";
    	}
    	return res;        
    }
}
