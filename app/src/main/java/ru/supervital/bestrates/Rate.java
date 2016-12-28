package ru.supervital.bestrates;
// немного текста
public class Rate {
//    public ImageView imageView;
	public String Code;
    public double Rate;
    public double RatePrev;
    public String Nominal;
    public String NameVal;
    public String ID;
    public String NumCode;
	public boolean isPrevLoaded = false;
    
    public Rate(String Code, String Nominal, String NameVal){
    	super();
    	this.Code = Code;
    	this.Nominal = Nominal;
    	this.NameVal = NameVal;    	
    }
    
}

