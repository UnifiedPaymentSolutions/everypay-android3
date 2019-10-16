package com.everypay.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;
import com.everypay.sdk.activity.PaymentActivity;
import com.everypay.sdk.data.network.AppService;
import com.everypay.sdk.inter.ServiceListener;
import com.everypay.sdk.model.Card;
import com.everypay.sdk.steps.MerchantParamsStep;
import com.everypay.sdk.steps.MerchantPaymentStep;
import com.everypay.sdk.util.Log;

import java.util.WeakHashMap;


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

    private static final Log log = Log.getInstance(EveryPay.class);

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

    public void init(String everypayUrl, String merchantUrl, String apiVersion, String everyPayHost) {
        init(everypayUrl, merchantUrl, apiVersion, everyPayHost, new MerchantParamsStep(), new MerchantPaymentStep());
    }

    public void init(String everypayUrl, String merchantUrl, String apiVersion, String everyPayHost, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep) {
        this.everypayUrl = everypayUrl;
        this.merchantUrl = merchantUrl;
        this.apiVersion = apiVersion;
        this.everyPayHost = everyPayHost;
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
        this.isInitDone = true;
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
    private MerchantParamsStep merchantParamsStep;
    private EveryPaySession session;
    private MerchantPaymentStep merchantPaymentStep;
    private boolean isInitDone;
    private final WeakHashMap<String, ServiceListener> listeners = new WeakHashMap<>();

    private EveryPay(Context appContext, String everypayUrl, String merchantUrl, MerchantParamsStep merchantParamsStep, MerchantPaymentStep merchantPaymentStep, String apiVersion, String everyPayHost) {
        this.context = appContext;
        this.everypayUrl = everypayUrl;
        this.merchantUrl = merchantUrl;
        this.merchantParamsStep = merchantParamsStep;
        this.merchantPaymentStep = merchantPaymentStep;
        this.apiVersion = apiVersion;
        this.everyPayHost = everyPayHost;
    }

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

    public MerchantParamsStep getMerchantParamsStep() {
        return merchantParamsStep;
    }

    public MerchantPaymentStep getMerchantPaymentStep() {
        return merchantPaymentStep;
    }

    public void startFullPaymentFlow(String tag, Card card, EveryPayListener callback, String accountId) {
        throwIfNoInit();
        setListener(tag, callback);
        Log.setLogLevel(Config.USE_DEBUG ? Log.LOG_LEVEL_DEBUG : Log.LOG_LEVEL_RELEASE);
        session = new EveryPaySession(context, instance, card, callback, apiVersion, accountId);
        session.startPaymentFlow();
    }

    private void throwIfNoInit() {
        if (!isInitDone()) {
            log.e("throwIfNoInit : Init not done !");
            throw new RuntimeException("EveryPay not initialized. Did you call EveryPay.init() first ?");
        }
    }

    private void throwIfNoInit(String param) {
        if (!isInitDone()) {
            log.e("throwIfNoInit : Init not done ! Missing param: " + param);
//            throw new RuntimeException("EveryPay not initialized. Did you call EveryPay.init() first ?  ! Missing param: " + param);
        }
    }

    private boolean isInitDone() {
        synchronized (initEPLock) {
            return isInitDone;
        }
    }

    /**
     * Overwrite or clear a listener for a specific tag.
     * NB: For an initial listener set it when calling a specific method.
     *
     * @param tag      Listener tag
     * @param listener Listener to set
     */
    public void setListener(final String tag, @Nullable final ServiceListener listener) {
        log.d("setListener: " + tag + ", listener: " + listener);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        synchronized (listeners) {
            listeners.put(tag, listener);
        }
    }

    /**
     * Getter for specific listener.
     *
     * @param tag            unique tag that listener was set with
     * @param forgetListener if we should listen for callback or not
     * @param type           listener type
     * @return listener of provided type
     */
    public <T extends ServiceListener> T getListener(final String tag, final boolean forgetListener, @NonNull final Class<T> type) {
        log.d("getListener: " + tag + ", forgetListener: " + forgetListener);
        //noinspection ConstantConditions
        if (TextUtils.isEmpty(tag) || type == null) {
            return null;
        }
        synchronized (listeners) {
            if (listeners.get(tag) != null && type.isInstance(listeners.get(tag))) {
                //noinspection unchecked
                return (T) (forgetListener ? listeners.remove(tag) : listeners.get(tag));
            }
        }
        return null;
    }

    /**
     * Method to remove listener.
     *
     * @param tag unique tag that listeners was set with
     */
    public void removeListener(final String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        synchronized (listeners) {
            listeners.remove(tag);
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
