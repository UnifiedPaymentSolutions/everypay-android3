package com.everypay.sdk.data.network.responsedata;

import com.google.gson.annotations.SerializedName;

public class CardDetailResponse{

	@SerializedName("payment_state")
	private String status;

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CardDetailResponse{" + 
			"payment_state = '" + status + '\'' +
			"}";
		}
}