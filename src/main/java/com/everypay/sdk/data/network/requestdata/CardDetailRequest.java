package com.everypay.sdk.data.network.requestdata;

import com.google.gson.annotations.SerializedName;

public class CardDetailRequest{

	@SerializedName("api_username")
	private String apiUsername;

	@SerializedName("cc_details")
	private CcDetails ccDetails;

	@SerializedName("nonce")
	private String nonce;

	@SerializedName("token_consented")
	private Boolean tokenConsented;

	@SerializedName("mobile_token")
	private String mobileToken;

	@SerializedName("timestamp")
	private String timestamp;

	public void setApiUsername(String apiUsername){
		this.apiUsername = apiUsername;
	}

	public String getApiUsername(){
		return apiUsername;
	}

	public void setCcDetails(CcDetails ccDetails){
		this.ccDetails = ccDetails;
	}

	public CcDetails getCcDetails(){
		return ccDetails;
	}

	public void setNonce(String nonce){
		this.nonce = nonce;
	}

	public String getNonce(){
		return nonce;
	}

	public void setTokenConsented(Boolean tokenConsented){
		this.tokenConsented = tokenConsented;
	}

	public Boolean getTokenConsented(){
		return tokenConsented;
	}

	public void setMobileToken(String mobileToken){
		this.mobileToken = mobileToken;
	}

	public String getMobileToken(){
		return mobileToken;
	}

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

	public String getTimestamp(){
		return timestamp;
	}

	@Override
 	public String toString(){
		return 
			"CardDetailRequest{" + 
			"api_username = '" + apiUsername + '\'' + 
			",cc_details = '" + ccDetails + '\'' + 
			",nonce = '" + nonce + '\'' + 
			",token_consented = '" + tokenConsented + '\'' + 
			",mobile_token = '" + mobileToken + '\'' + 
			",timestamp = '" + timestamp + '\'' + 
			"}";
		}
}