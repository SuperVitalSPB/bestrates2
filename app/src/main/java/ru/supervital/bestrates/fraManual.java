package ru.supervital.bestrates;
// немного текста
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class fraManual extends BestratesFragment {
	public static final String TAG = "bestrates.fraManual";

	TextView txtFrom;
	
	public fraManual() {
    	ID = MainActivity.FRAME_MANUAL;
	}	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.activity_manual, container, false);

    	txtFrom = (TextView) rootView.findViewById(R.id.txtFrom);

    	txtFrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donorsite))));				
			}
		});
    	
        return rootView;
    }
    
        

}
