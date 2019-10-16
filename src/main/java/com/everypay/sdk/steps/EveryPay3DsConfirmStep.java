package com.everypay.sdk.steps;

import android.support.annotation.Nullable;

import com.everypay.sdk.EveryPay;
import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.data.network.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.inter.EveryPay3DsConfirmListener;
import com.everypay.sdk.inter.EveryPayCallback;
import com.everypay.sdk.util.Log;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class EveryPay3DsConfirmStep extends Step {

    private static final Log log = Log.getInstance(MerchantPaymentStep.class);
    private static final String PARAMETER_MOBILE_3DS_HMAC = "mobile_3ds_hmac";
    private static final String PARAMETER_API_VERSION = "api_version";

    @Override
    public StepType getType() {
        return StepType.EVERYPAY_3DS_CONFIRM;
    }

    public void run(final String tag, final EveryPay ep, String paymentReference, String hmac, String apiVersion, EveryPay3DsConfirmListener listener) {
        if(ep != null) {
            ep.setListener(tag, listener);
        }
        EveryPayApi.EveryPayApiCalls apiCalls = EveryPayApi.getInstance(ep.getContext(), ep.getEverypayUrl()).getApiCalls();
        HashMap<String, String> params = new HashMap<>();
        params.put(PARAMETER_MOBILE_3DS_HMAC, hmac);
        params.put(PARAMETER_API_VERSION, apiVersion);
       final Call<EveryPayTokenResponseData> call = apiCalls.encryptedPaymentInstrumentConfirmed(paymentReference, params);
       call.enqueue(new EveryPayCallback<EveryPayTokenResponseData>() {
           @Override
           public void onSuccess(Call<EveryPayTokenResponseData> call, Response<EveryPayTokenResponseData> response) {
               log.d("encryptedPaymentInstrumentsConfirmed success");
               final EveryPay3DsConfirmListener listener = ep.getListener(tag, true, EveryPay3DsConfirmListener.class);
               if(listener != null) {
                   listener.onEveryPay3DsConfirmSucceed(response.body());
               }

           }

           @Override
           public void onFailure(Call<EveryPayTokenResponseData> call, @Nullable EveryPayError error, @Nullable Throwable t) {
               log.d("encryptedPaymentInstrumentsConfirmed failure");
               final EveryPay3DsConfirmListener listener = ep.getListener(tag, true, EveryPay3DsConfirmListener.class);
               if(listener != null) {
                   listener.onEveryPay3DsConfirmFailure(EveryPayError.from(ep.getContext(), error, t));
               }
           }
       });
    }
}
