package com.everypay.sdk.data.network.responsedata;


import com.everypay.sdk.api.ErrorHelper;
import com.everypay.sdk.api.EveryPayError;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MerchantPaymentResponseData extends ErrorHelper {
    private static final long serialVersionUID = 1432107208287516008L;

    @SerializedName("status")
    public String status;

    public MerchantPaymentResponseData(ArrayList<EveryPayError> errors) {
        super(errors);
    }

    @Override
    public String toString() {
        return "MerchantPaymentResponseData{" +
                "status='" + status + '\'' +
                '}';
    }
}
