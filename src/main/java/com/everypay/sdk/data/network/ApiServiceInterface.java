package com.everypay.sdk.data.network;

import com.everypay.sdk.data.network.requestdata.CardDetailRequest;
import com.everypay.sdk.data.network.responsedata.CardDetailResponse;
import com.everypay.sdk.data.network.responsedata.PaymentStateWebviewResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiServiceInterface {

    @POST("api/v3/mobile_payments/card_details")
    Observable<CardDetailResponse> getCardDetail(@Body CardDetailRequest cardDetailRequest);

    @GET
    Observable<PaymentStateWebviewResponse> getPaymentStatusWebview(@Url String url);

}
