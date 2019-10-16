package com.everypay.sdk.data.network.requestdata;

import com.everypay.sdk.data.network.responsedata.EveryPayTokenResponseData;
import com.google.gson.annotations.SerializedName;

public class MerchantPaymentRequestData {
    @SerializedName("hmac")
    public String hmac;
    @SerializedName("cc_token_encrypted")
    public String ccTokenEncrypted;

    public MerchantPaymentRequestData(String hmac, EveryPayTokenResponseData everypayResponse) {
        this.hmac = hmac;
        this.ccTokenEncrypted = everypayResponse.getToken();
    }
}
