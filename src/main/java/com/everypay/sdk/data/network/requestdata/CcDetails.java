package com.everypay.sdk.data.network.requestdata;

import com.google.gson.annotations.SerializedName;

public class CcDetails{

	@SerializedName("number")
	private String number;

	@SerializedName("cvc")
	private String cvc;

	@SerializedName("month")
	private String month;

	@SerializedName("year")
	private String year;

	@SerializedName("holder_name")
	private String holderName;

	public void setNumber(String number){
		this.number = number;
	}

	public String getNumber(){
		return number;
	}

	public void setCvc(String cvc){
		this.cvc = cvc;
	}

	public String getCvc(){
		return cvc;
	}

	public void setMonth(String month){
		this.month = month;
	}

	public String getMonth(){
		return month;
	}

	public void setYear(String year){
		this.year = year;
	}

	public String getYear(){
		return year;
	}

	public void setHolderName(String holderName){
		this.holderName = holderName;
	}

	public String getHolderName(){
		return holderName;
	}

	@Override
 	public String toString(){
		return 
			"CcDetails{" + 
			"number = '" + number + '\'' + 
			",cvc = '" + cvc + '\'' + 
			",month = '" + month + '\'' + 
			",year = '" + year + '\'' + 
			",holder_name = '" + holderName + '\'' + 
			"}";
		}
}