package ru.supervital.bestrates;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class RegionArrayAdapter extends ArrayAdapter <Region>{ 

	public static final String TAG = "bestrates.RegionArrayAdapter";
	private final ArrayList<Region> regions;
    private final Context context;
    private int textViewResourceId;
    		
    public RegionArrayAdapter(Context context, int textViewResourceId, ArrayList<Region> regions) { 
    	super(context, textViewResourceId, regions);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.regions = regions;
    }

    public int getPosition(String RegionName){
    	for (Region reg: regions)
    		if (reg.Name.contains(RegionName))
    			return reg.Index;
    	return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);
        
        TextView tv = (TextView) convertView;
        tv.setText(regions.get(position).Name);
        
        return convertView;
    }
    
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public Region getItem(int position) {
        return regions.get(position);
    }    
    
}
