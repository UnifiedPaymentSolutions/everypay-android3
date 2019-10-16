package com.everypay.sdk.data.network.responsedata;

import com.google.gson.annotations.SerializedName;

public class PaymentStateWebviewResponse{

	@SerializedName("state")
	private String state;

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	@Override
 	public String toString(){
		return 
			"PaymentStateWebviewResponse{" + 
			"state = '" + state + '\'' + 
			"}";
		}
}