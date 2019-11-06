package com.everypay.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.everypay.sdk.activity.PaymentActivity;
import com.everypay.sdk.data.network.AppService;



/**
 * Builder class for EverypaySessions.
 */
public class EveryPay {


    public static final String TAG = "everypay";

    private static final String EXCEPTION_NO_DEFAULT_EVERYPAY_INSTANCE = "No default Everypay instance set.";

    public static final String EVERYPAY_API_URL_STAGING = "https://gw-staging.every-pay.com/";
    public static final String EVERYPAY_API_STAGING_HOST = "gw-staging.every-pay.com";
    public static final String EVERYPAY_API_URL_DEMO = "https://gw-demo.every-pay.com/";
    public static final String EVERYPAY_API_DEMO_HOST = "gw-demo.every-pay.com";
    public static final String EVERYPAY_API_URL_LIVE = "https://gw.every-pay.eu";
    public static final String MERCHANT_API_URL_STAGING = "https://igwshop-staging.every-pay.com/";
    public static final String MERCHANT_API_URL_DEMO = "https://igwshop-demo.every-pay.com/";

    public static final int REQUEST_CODE = 123;
    public static final int RESULT_OK = 111;
    public static final int RESULT_ERROR = -1;


    private final Object initEPLock = new Object();
    private Activity mActivity;

    @SuppressLint("StaticFieldLeak")
    private static volatile EveryPay instance;

    public static EveryPay getInstance(final Context context) {
        if (instance == null) {
            instance = new EveryPay(context.getApplicationContext());
        }
        return instance;
    }

    public static EveryPay getInstance(final Activity activity) {
        if (instance == null) {
            instance = new EveryPay(activity);
        }
        return instance;
    }

    /*
    Initial for v3 api
     */

    public void initv3(String apiUsername, String host, String amount, String currency) {
        this.apiUsername = apiUsername;
        this.everyPayHost = host;
        this.amount = amount;
        this.currency = currency;
    }

    private EveryPay(Context appContext) {
        this.context = appContext.getApplicationContext();
    }

    private EveryPay(Activity activity) {
        this.mActivity = activity;
        this.context = mActivity.getApplicationContext();
    }

    private String apiUsername;

    private final Context context;
    private String everypayUrl;
    private String everyPayHost;
    private String paymentLink;
    private String amount;
    private String currency;
    private String merchantUrl;
    private String apiVersion;
    private boolean isInitDone;

    public Context getContext() {
        return context;
    }

    public String getEverypayUrl() {
        return everypayUrl;
    }

    public String getMerchantUrl() {
        return merchantUrl;
    }

    public String getEveryPayHost() {
        return everyPayHost;
    }

    private void throwIfNoInit() {
        if (!isInitDone()) {
            LogUtils.e("throwIfNoInit : Init not done !");
            throw new RuntimeException("EveryPay not initialized. Did you call EveryPay.init() first ?");
        }
    }

    private void throwIfNoInit(String param) {
        if (!isInitDone()) {
            LogUtils.e("throwIfNoInit : Init not done ! Missing param: " + param);
//            throw new RuntimeException("EveryPay not initialized. Did you call EveryPay.init() first ?  ! Missing param: " + param);
        }
    }

    private boolean isInitDone() {
        synchronized (initEPLock) {
            return isInitDone;
        }
    }


    public void handlePayment(String link, String methodSource, String mobileAccessToken) {
        if (StringUtils.isEmpty(apiUsername)) {
            throwIfNoInit("api_username");
            return;
        }
        if (StringUtils.isEmpty(everyPayHost)) {
            throwIfNoInit("host");
            return;
        }
        AppService appService = AppService.getInstance();
        appService.setHost(everyPayHost);
        Intent intent = new Intent();
        intent.setClass(context, PaymentActivity.class);
        intent.putExtra(PaymentActivity.LINK, link);
        intent.putExtra(PaymentActivity.API_USERNAME, apiUsername);
        intent.putExtra(PaymentActivity.METHOD_SOURCE, methodSource);
        intent.putExtra(PaymentActivity.TOKEN, mobileAccessToken);
        intent.putExtra(PaymentActivity.HOST, everyPayHost);
        intent.putExtra(PaymentActivity.AMOUNT, amount);
        intent.putExtra(PaymentActivity.CURRENCY, currency);
        mActivity.startActivityForResult(intent, REQUEST_CODE);

    }

}
