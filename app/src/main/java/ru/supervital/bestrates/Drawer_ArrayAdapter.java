package ru.supervital.bestrates;
// немного текста

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


@SuppressLint({ "InflateParams", "NewApi" })
public class Drawer_ArrayAdapter extends ArrayAdapter <Drawer_ArrayAdapter.DrawerMenuItem> {
	private final static String TAG = "Drawer_ArrayAdapter";
	
	ArrayList<Integer> NotWorkItems = new ArrayList<Integer>();
						
    private final AppCompatActivity mActivity;
    int iSelectedItem = 0;
    
    ArrayList<DrawerMenuItem> mnuItems;
    
    public Drawer_ArrayAdapter(AppCompatActivity context, ArrayList<DrawerMenuItem> mnuItems) {
        super(context, R.layout.drawer_list_item, mnuItems);
        this.mActivity = (AppCompatActivity) context;
        this.mnuItems = mnuItems;
//		NotWorkItems.add(MainActivity.FRAME_MANUAL);
//		NotWorkItems.add(MainActivity.FRAME_FEEDBACK);
//		NotWorkItems.add(MainActivity.FRAME_RATE);
//		NotWorkItems.add(MainActivity.FRAME_SUPPORT);
//		NotWorkItems.add(MainActivity.FRAME_ABOUT);
    }

// Класс для сохранения во внешний класс и для ограничения доступа
// из потомков класса
     static class ViewHolder {
    	 public LinearLayout ll_Item;
         public ImageView    imgMenuDrawer;
         public TextView     txtMenuDrawer;
         public TextView     txtMenuDrawerDescr;
     }
     
     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         // ViewHolder буферизирует оценку различных полей шаблона элемента

         ViewHolder holder;
         // Очищает сущетсвующий шаблон, если параметр задан
         // Работает только если базовый шаблон для всех классов один и тот же
         View rowView = convertView;
         if (rowView == null) {
             LayoutInflater inflater = mActivity.getLayoutInflater();
             rowView = inflater.inflate(R.layout.drawer_list_item, null, true);
             //
             holder = new ViewHolder();
             holder.ll_Item             = (LinearLayout) rowView.findViewById(R.id.ll_Item);
             holder.imgMenuDrawer       = (ImageView)    rowView.findViewById(R.id.imgMenuDrawer);
             holder.txtMenuDrawer       = (TextView)     rowView.findViewById(R.id.txtMenuDrawer);
             holder.txtMenuDrawerDescr  = (TextView)     rowView.findViewById(R.id.txtMenuDrawerDescr);
             rowView.setTag(holder);
         } else {
             holder = (ViewHolder) rowView.getTag();
         }
         
         Boolean bEnabled = !(NotWorkItems.contains(mnuItems.get(position).ID));         
         holder.ll_Item.setEnabled(bEnabled);
         
         int ident = mActivity.getResources().getIdentifier(mnuItems.get(position).NameImage, 
         												  "drawable", 
         												  mActivity.getPackageName().toString());
         if (ident != 0) {
        	holder.imgMenuDrawer.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(), ident));
         }

         CharSequence sStr = mnuItems.get(position).Name;
         if (mnuItems.get(position).ID == iSelectedItem) {
     		sStr = Html.fromHtml("<b>" + sStr + "</b>");
         }
         holder.txtMenuDrawer.setText(sStr);
        	 
         holder.txtMenuDrawer.setEnabled(bEnabled);
      	 holder.txtMenuDrawerDescr.setText((CharSequence) mnuItems.get(position).Description);
      	 holder.txtMenuDrawerDescr.setEnabled(bEnabled);
      	 
      	 if (position == iSelectedItem)
      		 		setTitleByPos(position);
      		 
         return rowView;
     }
     
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void setTitleByPos(int aPosition) {
     	if (aPosition == MainActivity.FRAME_BUTTONS)
     		mActivity.getSupportActionBar().setTitle(mActivity.getString(R.string.app_name));
     	else    		
     		for (DrawerMenuItem item : mnuItems) {
     			if (item.ID == aPosition) {
     				mActivity.getSupportActionBar().setTitle(item.Name);
     				break;
     			}
     		}
     }


    public static class DrawerMenuItem {
        Integer    ID;
        String NameImage;
        String Name;
        String Description;

        public DrawerMenuItem(String aNameImage, String aName, String aDescription, Integer aID) {
            super();
            NameImage = aNameImage;
            Name = aName;
            Description = aDescription;
            ID = aID;
        }
    }

}
