package com.everypay.sdk.data.network.requestdata;

import com.everypay.sdk.data.network.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.model.Card;
import com.google.gson.annotations.SerializedName;


public class EveryPayTokenRequestData {

    @SerializedName("encrypted_payment_instrument")
    public EncryptedTokenRequestData encryptedPaymentInstrument;


    public EveryPayTokenRequestData(MerchantParamsResponseData params, Card card) {
        this.encryptedPaymentInstrument = new EncryptedTokenRequestData(params, card);
    }

    private static class EncryptedTokenRequestData {

        @SerializedName("api_username")
        String apiUsername;  // Merchant ID
        @SerializedName("account_id")
        String accountId;
        @SerializedName("user_ip")
        String userIp;
        @SerializedName("hmac")
        String hmac;
        @SerializedName("nonce")
        String nonce;
        @SerializedName("timestamp")
        long timestamp;
        @SerializedName("cc_holder_name")
        String ccHolderName;
        @SerializedName("cc_number")
        String ccNumber;
        @SerializedName("cc_month")
        String ccMonth;
        @SerializedName("cc_year")
        String ccYear;
        @SerializedName("cc_verification")
        String ccVerification;
        @SerializedName("api_version")
        String apiVersion;
        @SerializedName("order_reference")
        String orderReference;

        public EncryptedTokenRequestData(MerchantParamsResponseData params, Card card) {
            this.apiUsername = params.apiUsername;
            this.accountId = params.accountId;
            this.userIp = params.userIp;
            this.hmac = params.hmac;
            this.nonce = params.nonce;
            this.timestamp = params.timestamp;

            this.ccHolderName = card.getName();
            this.ccNumber = card.getNumber();
            this.ccYear = card.getExpYear();
            this.ccMonth = card.getExpMonth();
            this.ccVerification = card.getCVC();


            this.apiVersion = params.apiVersion;
            this.orderReference = params.orderReference;
        }

        @Override
        public String toString() {
            return "EncryptedTokenRequestData{" +
                    "apiUsername='" + apiUsername +
                    ", accountId='" + accountId +
                    ", userIp='" + userIp +
                    ", hmac='" + hmac +
                    ", nonce='" + nonce +
                    ", timestamp=" + timestamp +
                    ", ccHolderName='" + ccHolderName +
                    ", ccNumber='" + ccNumber +
                    ", ccMonth='" + ccMonth +
                    ", ccYear='" + ccYear +
                    ", ccVerification='" + ccVerification +
                    ", apiVersion='" + apiVersion +
                    ", orderReference='" + orderReference +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EveryPayTokenRequestData{" +
                "encryptedPaymentInstrument=" + encryptedPaymentInstrument.toString() +
                '}';
    }
}
