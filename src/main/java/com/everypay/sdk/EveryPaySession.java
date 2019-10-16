package com.everypay.sdk;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.everypay.sdk.api.EveryPayApi;
import com.everypay.sdk.api.EveryPayError;
import com.everypay.sdk.api.MerchantApi;
import com.everypay.sdk.data.network.responsedata.EveryPayTokenResponseData;
import com.everypay.sdk.data.network.responsedata.MerchantParamsResponseData;
import com.everypay.sdk.data.network.responsedata.MerchantPaymentResponseData;
import com.everypay.sdk.inter.EveryPay3DsConfirmListener;
import com.everypay.sdk.inter.EveryPayTokenListener;
import com.everypay.sdk.inter.MerchantParamsListener;
import com.everypay.sdk.inter.MerchantPaymentListener;
import com.everypay.sdk.inter.WebAuthListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.EveryPay3DsConfirmStep;
import com.everypay.sdk.steps.EveryPayTokenStep;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.steps.Step;
import com.everypay.sdk.steps.WebAuthStep;
import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;


public class EveryPaySession {


    private static final String EXCEPTION_CARD_IS_NULL = "Card is null";
    private static final String EXCEPTION_LISTENER_IS_NULL = "Listener is null";
    private static final String PAYMENT_STATE_WAITING_FOR_3DS = "waiting_for_3ds_response";
    private static final String TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS = "com.everypay.sdk.TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS";
    private static final String TAG_EVERYPAY_SESSION_SAVE_CARD = "com.everypay.sdk.TAG_EVERYPAY_SESSION_SAVE_CARD";
    private static final String TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT = "com.everypay.sdk.TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT";
    private static final String ACCOUNT_ID_3DS_INDICATOR = "3D";
    private static final CharSequence PAYMENT_STATE_FAILED = "failed";
    private static final String PARAMETER_PAYMENT_REFERENCE = "payment_reference";
    private static final String PARAMETER_SECURE_CODE_ONE = "secure_code_one";
    private static final String PARAMETER_MOBILE_3DS_HMAC = "mobile_3ds_hmac";
    private static final String PATH_WEB_VIEW = "/authentication3ds/new";
    private Handler handler;
    private Context context;
    private String id;
    private EveryPay ep;
    private String apiVersion;
    private String accountId;
    private String hmac;
    private EveryPayListener listener;
    private static final Log log = Log.getInstance(EveryPaySession.class);

    private Card card;


    // Steps
    private MerchantParamsStep merchantParamsStep;
    private EveryPayTokenStep everyPayTokenStep;
    private MerchantPaymentStep merchantPaymentStep;
    private EveryPay3DsConfirmStep everyPay3DsConfirmStep;
    private WebAuthStep webAuthStep;


    public EveryPaySession(Context context, EveryPay ep, Card card, EveryPayListener listener, String apiVersion, String accountId) {
        this.handler = new Handler();
        this.context = context;
        this.ep = ep;
        this.apiVersion = apiVersion;
        this.id = Util.getRandomString();
        this.accountId = accountId;

        if (card == null)
            throw new IllegalArgumentException(EXCEPTION_CARD_IS_NULL);
        this.card = card;

        this.listener = listener;
        if (listener == null)
            throw new IllegalArgumentException(EXCEPTION_LISTENER_IS_NULL);

        this.merchantParamsStep = ep.getMerchantParamsStep();
        this.everyPayTokenStep = new EveryPayTokenStep();
        this.merchantPaymentStep = ep.getMerchantPaymentStep();
        this.everyPay3DsConfirmStep = new EveryPay3DsConfirmStep();
        this.webAuthStep = new WebAuthStep();
    }


    public void startPaymentFlow() {
        EveryPayApi.createNewInstance(ep.getContext(), ep.getEverypayUrl());
        MerchantApi.createNewInstance(ep.getContext(), ep.getMerchantUrl());
        callStepStarted(merchantParamsStep);
        getMerchantParams(TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS);
    }

    private void getMerchantParams(String tag) {
        log.d("getMerchantParams called");
        merchantParamsStep.run(tag, ep, apiVersion, accountId, new MerchantParamsListener() {
            @Override
            public void onMerchantParamsSucceed(MerchantParamsResponseData responseData) {
                log.d("EverypaySession merchantParams succeed");
                callStepSuccess(merchantParamsStep);
                saveCard(TAG_EVERYPAY_SESSION_SAVE_CARD, responseData);
            }

            @Override
            public void onMerchantParamsFailure(EveryPayError error) {
                log.d("EverypaySession merchantParams failed");
                callStepFailure(merchantParamsStep, error);
            }
        });

    }

    private void saveCard(final String tag, final MerchantParamsResponseData merchantParamsResponseData) {
        callStepStarted(everyPayTokenStep);
        log.d("saveCard called");
        everyPayTokenStep.run(tag, ep, merchantParamsResponseData, card, new EveryPayTokenListener() {
            @Override
            public void onEveryPayTokenSucceed(EveryPayTokenResponseData responseData) {
                log.d("EveryPaySession saveCard succeed");
                callStepSuccess(everyPayTokenStep);
                if(TextUtils.equals(responseData.getPaymentState(), PAYMENT_STATE_WAITING_FOR_3DS) && accountId.contains(ACCOUNT_ID_3DS_INDICATOR)) {
                    startwebViewStep(context,buildUrlForWebView(ep, responseData.getPaymentReference(), responseData.getSecureCodeOne(), merchantParamsResponseData.getHmac()), id, ep);
                } else if(!TextUtils.equals(responseData.getPaymentState(), PAYMENT_STATE_FAILED)){
                    merchantPayment(TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT, responseData, merchantParamsResponseData.getHmac());
                } else {
                    callStepFailure(everyPayTokenStep, new EveryPayError(EveryPayError.ERROR_UNKNOWN_ACCOUNT_ID_OR_PAYMENT_STATE, context.getString(R.string.ep_err_unknown_account_id_or_payment_state)));
                }
            }

            @Override
            public void onEveryPayTokenFailure(EveryPayError error) {
                log.d("EveryPaySession saveCard failure");
                callStepFailure(everyPayTokenStep, error);
            }
        });
    }

    private void merchantPayment(final String tag, final EveryPayTokenResponseData responseData, final String hmac) {
        log.d("merchantPayment called");
        callStepStarted(merchantPaymentStep);
        merchantPaymentStep.run(tag, ep, hmac, responseData, new MerchantPaymentListener() {
            @Override
            public void onMerchantPaymentSucceed(MerchantPaymentResponseData responseData) {
                log.d("EveryPaySession callMakePayment succeed");
                callStepSuccess(merchantPaymentStep);
                callFullSuccess(responseData);
            }

            @Override
            public void onMerchantPaymentFailure(EveryPayError error) {
                log.d("EveryPaySession callMakePayment failure");
                callStepFailure(merchantPaymentStep, error);
            }
        });
    }


    private String buildUrlForWebView(EveryPay ep, String paymentReference, String secureCodeOne, String hmac) {
        this.hmac = hmac;
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(ep.getEveryPayHost())
                .path(PATH_WEB_VIEW)
                .appendQueryParameter(PARAMETER_PAYMENT_REFERENCE, paymentReference)
                .appendQueryParameter(PARAMETER_SECURE_CODE_ONE, secureCodeOne)
                .appendQueryParameter(PARAMETER_MOBILE_3DS_HMAC, hmac)
                .build();
        return uri.toString();
    }

    private void startwebViewStep(Context context, String url, String id, EveryPay ep) {
        PaymentBrowserActivity.start(ep, context, url, id, new WebAuthListener() {
            @Override
            public void onWebAuthSucceed(String paymentReference) {
                log.d("EveryPaySession webView finished with success");
                encryptedPaymentInstrumentsConfirm(TAG_EVERYPAY_SESSION_GET_MERHANT_PARAMS, paymentReference);
            }

            @Override
            public void onWebAuthFailure(EveryPayError error) {
                log.d("EveryPaySession webView finished with failure");
                callStepFailure(webAuthStep, error);
            }

            @Override
            public void onWebAuthCanceled(EveryPayError error) {
                log.d("EveryPay webView finished with cancel");
                callStepFailure(webAuthStep, error);
            }
        });
    }

    private void encryptedPaymentInstrumentsConfirm(String tag, String paymentReference) {
        log.d("encryptedPaymentInstrumentsConfirm");
        everyPay3DsConfirmStep.run(tag, ep, paymentReference, hmac, apiVersion, new EveryPay3DsConfirmListener() {
            @Override
            public void onEveryPay3DsConfirmSucceed(EveryPayTokenResponseData responseData) {
                log.d("EveryPaySession encryptedPaymentInstrumentsConfirm succeed");
                merchantPayment(TAG_EVERYPAY_SESSION_MERCHANT_PAYMENT, responseData, hmac);
            }

            @Override
            public void onEveryPay3DsConfirmFailure(EveryPayError error) {

            }
        });
    }

    private void callStepStarted(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepStarted(step.getType());
                }
            });
        }
    }

    private void callStepSuccess(final Step step) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepSuccess(step.getType());
                }
            });
        }
    }

    private void callFullSuccess(final MerchantPaymentResponseData responseData) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.fullSuccess(responseData);
                }
            });
        }
    }

    private void callStepFailure(final Step step, final EveryPayError errorMessage) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.stepFailure(step.getType(), errorMessage);
                }
            });
        }
    }

}
