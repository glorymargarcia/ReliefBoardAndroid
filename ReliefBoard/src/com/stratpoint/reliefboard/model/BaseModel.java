package com.stratpoint.reliefboard.model;

import android.os.Parcel;
import android.os.Parcelable;

abstract class BaseModel implements Parcelable {
	protected int id;
	
	public BaseModel() { }
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	public abstract void readFromParcel(Parcel in);
}
