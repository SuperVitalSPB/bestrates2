package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Locale;


	public class FL_ArrayAdapter extends ArrayAdapter <Filial>{  
		private final static String TAG = "FL_ArrayAdapter";
		
	    private final AppCompatActivity context;
	    public ArrayList<Filial> filials;
	    public ArrayList<Filial> originalFilials;
	    public String BankName;
	    
	    boolean flViewButtonOnMap; // признак того, надо ли показывать кнопку "на карте"
	    
//----	    
	    public FL_ArrayAdapter(AppCompatActivity context, ArrayList<Filial> filials) {
	        super(context, R.layout.filials_rowlayout, filials);
	        this.context = context;
	        this.filials = filials;
	    }
	    
	 // Класс для сохранения во внешний класс и для ограничения доступа
	 // из потомков класса
	     static class ViewHolder {
	    	 TextView  lblNameFilial;
	    	 TextView  txtNameFilial;
	    	 
	    	 TextView  lblAdress;
	    	 TextView  txtAdress;
	    	 
	    	 TextView  lblTimeWork;
	    	 TextView  txtTimeWork;

	    	 LinearLayout ll_ContactsGlobal;	    	 
	    	 LinearLayout ll_lblContacts;
	    	 TextView     lblContacts;
	    	 TextView     txtContacts;
	    	 LinearLayout ll_btnContacts;
	    	 Button       btnContacts;

	    	 LinearLayout ll_ServicesGlobal;	    	 
	    	 LinearLayout ll_lblServices;
	    	 TextView     lblServices;
	    	 TextView     txtServices;
	    	 LinearLayout ll_btnServices;
	    	 Button       btnServices;
	    	 
	    	 LinearLayout ll_btnMap;
	    	 Button       btnMap;
	    	 
//	    	 TextView     txtLat;
//	    	 TextView     txtLon;
	     }
	     
	     @SuppressLint("InflateParams")
		    @Override
	     public View getView(int position, View convertView, ViewGroup parent) {
	         // ViewHolder буферизирует оценку различных полей шаблона элемента

	         ViewHolder holder;
	         // Очищает сущетсвующий шаблон, если параметр задан
	         // Работает только если базовый шаблон для всех классов один и тот же
	         View rowView = convertView;
	         if (rowView == null) {
	             LayoutInflater inflater = context.getLayoutInflater();
	             rowView = inflater.inflate(R.layout.filials_rowlayout, null, true);
	             holder = new ViewHolder();
	             
	             holder.lblNameFilial  = (TextView) rowView.findViewById(R.id.lblNameFilial);
	             holder.txtNameFilial  = (TextView) rowView.findViewById(R.id.txtNameFilial);
	             holder.lblAdress      = (TextView) rowView.findViewById(R.id.lblAdress);
	             holder.txtAdress      = (TextView) rowView.findViewById(R.id.txtAdress);
	             holder.lblTimeWork    = (TextView) rowView.findViewById(R.id.lblTimeWork);
	             holder.txtTimeWork    = (TextView) rowView.findViewById(R.id.txtTimeWork);
	             
	             holder.ll_ContactsGlobal = (LinearLayout) rowView.findViewById(R.id.ll_ContactsGlobal);	             
	             holder.ll_lblContacts = (LinearLayout) rowView.findViewById(R.id.ll_lblContacts);
	             holder.lblContacts    = (TextView)     rowView.findViewById(R.id.lblContacts);
	             holder.txtContacts    = (TextView)     rowView.findViewById(R.id.txtContacts);
	             holder.ll_btnContacts = (LinearLayout) rowView.findViewById(R.id.ll_btnContacts);
	             holder.btnContacts    = (Button)       rowView.findViewById(R.id.btnContacts);
		    	 
	             holder.ll_ServicesGlobal = (LinearLayout) rowView.findViewById(R.id.ll_ServicesGlobal);
	             holder.ll_lblServices = (LinearLayout) rowView.findViewById(R.id.ll_lblServices);
	             holder.lblServices    = (TextView)     rowView.findViewById(R.id.lblServices);
	             holder.txtServices    = (TextView)     rowView.findViewById(R.id.txtServices);
	             holder.ll_btnServices = (LinearLayout) rowView.findViewById(R.id.ll_btnServices);
	             holder.btnServices    = (Button)       rowView.findViewById(R.id.btnServices);
	             
	             holder.ll_btnMap      = (LinearLayout) rowView.findViewById(R.id.ll_btnMap);
	             holder.btnMap         = (Button)       rowView.findViewById(R.id.btnMap);
            
	             
//	             holder.txtLat		   = (TextView) rowView.findViewById(R.id.txtLat);
//	             holder.txtLon		   = (TextView) rowView.findViewById(R.id.txtLon);
	             
	             rowView.setTag(holder);
	         } else {
	             holder = (ViewHolder) rowView.getTag();
	         }
	         
             String sStr;
             Boolean Viewfiled;
             CaptionString cs;
	         
	         if (filials.get(position).NameFilial == null || filials.get(position).NameFilial.trim().length() == 0){
	        	 holder.lblNameFilial.setVisibility(View.GONE);
	        	 holder.txtNameFilial.setVisibility(View.GONE);
	         } else {
	        	 holder.lblNameFilial.setVisibility(View.VISIBLE);
	        	 holder.txtNameFilial.setVisibility(View.VISIBLE);
	        	 sStr = filials.get(position).NameFilial;
//!!!
// develop test sStr = String.valueOf(position) + " - "+ sStr; 	        	 
	        	 holder.txtNameFilial.setText(sStr);
	         }
             
             holder.txtAdress.setText(filials.get(position).Adress);
             holder.txtTimeWork.setText(filials.get(position).TimeWork);
             
             Viewfiled = (filials.get(position).Contacts.length() != 0);
             if (Viewfiled) {
                 holder.ll_ContactsGlobal.setVisibility(View.VISIBLE);
	             Viewfiled = (filials.get(position).Contacts.length() <= 100);
	             sStr = filials.get(position).Contacts;
	             if (Viewfiled) {
	            	 holder.ll_lblContacts.setVisibility(View.VISIBLE);
	            	 holder.ll_btnContacts.setVisibility(View.GONE);
	            	 holder.txtContacts.setText(sStr);
	             } else {
	            	 holder.ll_lblContacts.setVisibility(View.GONE);
	            	 holder.ll_btnContacts.setVisibility(View.VISIBLE);
	            	 cs = new CaptionString(BankName + ", " + filials.get(position).NameFilial, 
	            			                sStr, 
	            			                holder.lblContacts.getText().toString());
	            	 holder.btnContacts.setTag(cs);
		             holder.btnContacts.setEnabled(sStr.length() != 0);
		             holder.btnContacts.setOnClickListener(ShowMemo);
	             }
             } else {
                 holder.ll_ContactsGlobal.setVisibility(View.GONE);            	 
             }
             
             
             Viewfiled = (filials.get(position).Services.length() != 0);
             if (Viewfiled) {
                 holder.ll_ServicesGlobal.setVisibility(View.VISIBLE);            	             	 
	             Viewfiled = (filials.get(position).Services.length() <= 100);
	             sStr = filials.get(position).Services;
	             if (Viewfiled) {
	            	 holder.ll_lblServices.setVisibility(View.VISIBLE);
	            	 holder.ll_btnServices.setVisibility(View.GONE);
	            	 holder.txtServices.setText(sStr);
	             } else {
	            	 holder.ll_lblServices.setVisibility(View.GONE);
	            	 holder.ll_btnServices.setVisibility(View.VISIBLE);
	            	 cs = new CaptionString(BankName + ", " + 
	            			 				filials.get(position).NameFilial, 
	            			 				sStr, 
	            			 				holder.lblServices.getText().toString());
	                 holder.btnServices.setTag(cs);
	                 holder.btnServices.setEnabled(sStr.length() != 0);
	                 holder.btnServices.setOnClickListener(ShowMemo);
	             }
             } else {
                 holder.ll_ServicesGlobal.setVisibility(View.GONE);            	             	 
             }
	         
             holder.ll_btnMap.setVisibility(flViewButtonOnMap ? View.VISIBLE : View.GONE);
             holder.btnMap.setOnClickListener(ShowMap);
             holder.btnMap.setTag(filials.get(position).NameFilial);
             
//             holder.txtLat.setText(String.valueOf(filials.get(position).mGeoPoint.getLat()));
//             holder.txtLon.setText(String.valueOf(filials.get(position).mGeoPoint.getLon()));
            		 
             return rowView;
	     }
	     
		OnClickListener ShowMemo = new OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptionString cs = (CaptionString) v.getTag();
				
		        Intent intent = new Intent(context, ShowMemoActivity.class);
		        intent.putExtra(ShowMemoActivity.MEMOTEXT, cs);				
				
		        context.startActivity(intent);
			}
		};	  	

		OnClickListener ShowMap = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String nf = (String) v.getTag();
		        
		        Intent intent = new Intent(context, MapActivity.class);
		        intent.putExtra(MapActivity.SHOW_ONE_FILIAL, nf);
		        context.startActivity(intent);
			}
		};	  	
		
		@Override
		public int getCount() {
			return filials.size();
		}
		
		@Override
		public Filial getItem(int num) {
			return filials.get(num);
		}		
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		// оригинальный список филиалов, не фильтрованный
		public void MakeOriginal() {
			if (originalFilials == null)
					originalFilials = new ArrayList<Filial>();
			if (originalFilials.size() == 0)
					originalFilials.addAll(filials);
		}
		
		@Override
	    public Filter getFilter() {
	        return filter;
	    }
		
        Filter filter = new Filter() {
        	@SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
        		if (constraint == null || constraint.length() == 0) {
        			filials.clear();
        			filials.addAll(originalFilials);
        		} else
        			filials = (ArrayList<Filial>) results.values;
        		
        	    notifyDataSetChanged();
        	}

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
            	if (constraint == null || constraint.length() == 0) return null;
            	constraint = constraint.toString().toLowerCase(new Locale("ru", "RU"));
            	FilterResults result = new FilterResults();
            	if(constraint.toString().length() > 0) {
            	    ArrayList<Filial> filteredItems = new ArrayList<Filial>();
            	    if (originalFilials == null)
            	    	MakeOriginal();
            	    for(int i = 0; i < originalFilials.size(); i++) {
	            	     Filial filial = originalFilials.get(i);
	            	     if(filial.toString().toLowerCase(new Locale("ru", "RU")).contains(constraint)) {
		            	      filteredItems.add(filial);
		            	      result.count = filteredItems.size();
			             	  result.values = filteredItems;
			             	  break;
	            	     }
            	    }
            	} else {
            	    synchronized(this)
            	     {
            	      result.values = originalFilials;
            	      result.count = originalFilials.size();
            	     }
            	}
            	return result;	            	
            }
        };
		
				
} // main class
	

