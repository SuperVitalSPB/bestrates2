package ru.supervital.bestrates;

import android.os.Parcel;
import android.os.Parcelable;

public class CaptionString implements Parcelable {
	public static final String TAG = "bestrates.CaptionString";
	String Caption;
	String BigString;
	String DopStr;
	String Title;
	
	public CaptionString (String aCaption, String aBigString, String aDopStr) {
		super();
		Caption   = aCaption;
		BigString = aBigString;
		DopStr    = aDopStr;
	}

	
    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {Caption, BigString, DopStr, Title});
	}

	public CaptionString(Parcel in) {
		String[] data = new String[4];
		in.readStringArray(data);
		
		Caption   = data[0];
		BigString = data[1];
		DopStr    = data[2];
		Title     = data[3];
	}	
	
	public static final Creator<CaptionString> CREATOR = new Creator<CaptionString>() {
		@Override
		public CaptionString createFromParcel(Parcel source) {
			return new CaptionString(source);
		}
		@Override
		public CaptionString[] newArray(int size) {
			return new CaptionString[size];
		}
	};
	
}
