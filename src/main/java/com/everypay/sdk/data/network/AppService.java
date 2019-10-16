package com.everypay.sdk.data.network;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.everypay.sdk.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AppService {

    private static AppService sAppService;

    private static Retrofit sRetrofit;

    private String mHost;

    public static AppService getInstance() {
        if (sAppService == null) {
            sAppService = new AppService();
        }

        return sAppService;
    }

    private String mMobileAccessToken = "";

    public Retrofit getRetrofit() {
        return sRetrofit;
    }

    private OkHttpClient configClient(Context context, String mobileToken) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        Interceptor headerIntercept = chain -> {
            Request.Builder builder = chain.request().newBuilder();
            builder.header("Content-Type", "application/json");
            builder.header("Accept", "application/json, text/plain, */*");
            if (!StringUtils.isEmpty(mobileToken)) {
                builder.addHeader("Authorization", mobileToken);
            }

            /*if (!TextUtils.isEmpty(accessToken) && addAccessToken) {
                builder.addHeader("Authorization", "Bearer " + accessToken);
            } else {
                builder.addHeader("Authorization", "Basic ");
            }*/
            Request request = builder.build();
            return chain.proceed(request);
        };

        // Log
        okHttpClient.addInterceptor(new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BODY)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .build());

        okHttpClient.addNetworkInterceptor(headerIntercept);
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.readTimeout(60, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS);
        okHttpClient.retryOnConnectionFailure(true);

        return okHttpClient.build();
    }

    public Retrofit restClient(Context context, final String urlHost, final String mobileToken) {
        if (sRetrofit == null || (mMobileAccessToken != null && !mMobileAccessToken.equalsIgnoreCase(mobileToken)) || (mMobileAccessToken == null && StringUtils.isEmpty(mobileToken))) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            String host = urlHost;
            if (!urlHost.contains("http"))
                host = "https://" + urlHost;
            try {
                sRetrofit = new Retrofit.Builder()
                        .baseUrl(host)
                        .client(configClient(context, mobileToken))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            } catch (Exception e) {
                LogUtils.e("Init Retrofit error");
                sRetrofit = new Retrofit.Builder()
                        .baseUrl("https://google.com/")
                        .client(configClient(context, mobileToken))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
        }
        return sRetrofit;
    }

    private <T> T createService(Class<T> serviceClass, Context context, final String urlHost, final String mobileToken) {
        Retrofit retrofit = restClient(context, urlHost, mobileToken);
        return retrofit.create(serviceClass);
    }

    public ApiServiceInterface createService(Context context, String mobileAccessToken) {
        return createService(ApiServiceInterface.class, context, mHost, mobileAccessToken);
    }

    public ApiServiceInterface createService(Context context) {
        return createService(ApiServiceInterface.class, context, mHost, null);
    }

    public String getHost() {
        return mHost;
    }

    public void setHost(String host) {
        mHost = host;
    }
}
