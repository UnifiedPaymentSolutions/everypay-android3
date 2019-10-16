package com.everypay.sdk.data.network.requestdata;


import com.google.gson.annotations.SerializedName;

public class MerchantParamsRequestData {

    @SerializedName("account_id")
    String accountId;
    @SerializedName("api_version")
    String apiVersion;

    public MerchantParamsRequestData(String apiVersion, String accountId) {
        this.apiVersion = apiVersion;
        this.accountId = accountId;
    }


    @Override
    public String toString() {
        return "MerchantParamsRequestData{" +
                "accountId='" + accountId +
                ", apiVersion='" + apiVersion +
                '}';
    }
}
