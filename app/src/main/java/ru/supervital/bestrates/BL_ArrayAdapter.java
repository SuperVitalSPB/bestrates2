package ru.supervital.bestrates;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BL_ArrayAdapter extends ArrayAdapter <Bank> {
	private final static String TAG = "BL_ArrayAdapter";
	
    private final AppCompatActivity context;
    public final ArrayList<Bank> banks;
    ApplicationBestRates.TYPE_VAL type_val;
    
    public BL_ArrayAdapter(AppCompatActivity context, ArrayList<Bank> banks) {
        super(context, R.layout.banklist_rowlayout, banks);
        this.context = context;
        this.banks = banks;
    }
    
 // Класс для сохранения во внешний класс и для ограничения доступа
 // из потомков класса
     static class ViewHolder {
         public ImageView imgBank;
         public TextView  txtNameBank;
         public TextView  txtVbuy;
         public TextView  txtVsell;
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
             rowView = inflater.inflate(R.layout.banklist_rowlayout, null, true);
             holder = new ViewHolder();
             holder.imgBank     = (ImageView) rowView.findViewById(R.id.imgBank);
             holder.txtNameBank = (TextView)  rowView.findViewById(R.id.txtNameBank);
             holder.txtVbuy     = (TextView)  rowView.findViewById(R.id.txtVbuy);
             holder.txtVsell    = (TextView)  rowView.findViewById(R.id.txtVsell);

             rowView.setTag(holder);
         } else {
             holder = (ViewHolder) rowView.getTag();
         }
         
         if (banks.get(position).BitmapBank != null)
        	 holder.imgBank.setImageBitmap(banks.get(position).BitmapBank);
         
         String sStr = banks.get(position).NameBank;
//!!!
// develop test sStr = String.valueOf(position) + " - "+ sStr;
         holder.txtNameBank.setText(sStr);
         
         if (type_val == ApplicationBestRates.TYPE_VAL.tvUSD) {
        	 holder.txtVbuy.setText((CharSequence) String.valueOf(banks.get(position).USDbuy));
        	 holder.txtVsell.setText((CharSequence) String.valueOf(banks.get(position).USDsell));
         } else {
        	 holder.txtVbuy.setText((CharSequence) String.valueOf(banks.get(position).EURbuy));
        	 holder.txtVsell.setText((CharSequence) String.valueOf(banks.get(position).EURsell));
         }

         return rowView;
     }
}

