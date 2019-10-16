
package com.everypay.sdk.data.network.responsedata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class CcDetails {

    @SerializedName("cc_holder_name")
    private String holderName;
    @Expose
    private String issuer;
    @SerializedName("c_issuer_country")
    private String issuerCountry;
    @SerializedName("cc_last_four_digits")
    private String lastFourDigits;
    @Expose
    @SerializedName("cc_month")
    private String month;
    @Expose
    @SerializedName("cc_token")
    private String token;
    @Expose
    @SerializedName("cc_type")
    private String type;
    @Expose
    @SerializedName("cc_year")
    private String year;

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "CcDetails{" +
                "holderName='" + holderName + '\'' +
                ", issuer='" + issuer + '\'' +
                ", issuerCountry='" + issuerCountry + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", month='" + month + '\'' +
                ", token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
