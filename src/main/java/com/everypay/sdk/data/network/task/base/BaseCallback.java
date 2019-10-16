package com.everypay.sdk.data.network.task.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.everypay.sdk.R;
import com.everypay.sdk.data.network.AppService;
import com.everypay.sdk.data.network.responsedata.ErrorResponse;
import com.everypay.sdk.util.DialogUtils;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.io.IOException;
import java.lang.annotation.Annotation;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public abstract class BaseCallback<T> {
    private String TAG = this.getClass().toString();
    private boolean mIsShowLoading = true;
    private ACProgressFlower mDialog;
    private Context mContext;
    private static final int SUCCESS = 200;
    private static final int ERROR_INPUT = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    /*
    Const for exception
     */
    private int CONST_EXCEPTION = 0;
    private int CONST_TIME_OUT = 1;
    private int CONST_NOT_FOUND = 2;
    private int CONST_AUTHENTICATE = 3;
    private int CONST_SERVER_ERROR = 4;

    private int mStateObservable;

    private AppService mAppService;


    public BaseCallback(Context context, boolean showLoading) {
        this.mContext = context;
        this.mIsShowLoading = showLoading;
        Activity activity = ActivityUtils.getTopActivity();
        mAppService = AppService.getInstance();

        if (showLoading && context != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                mDialog = new ACProgressFlower.Builder(context)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .sizeRatio(0.15f)
                        .themeColor(Color.WHITE)
                        .petalThickness(7)
                        .bgAlpha(0)
                        .build();
                try {
                    mDialog.show();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public BaseCallback(Context context) {
        this(context, false);
    }

    public Context getContext() {
        return mContext;
    }

    // Call back when task processed
    protected OnFinishCallbackListener<T> mOnFinishCallbackListener;

    protected Observable<T> processSubscribe(Observable<T> observable) {
        if (!NetworkUtils.isConnected()) {
            onNoInternetAccess();
            dismissDiaLogLoading();
            return Observable.empty();
        } else {
            if (StringUtils.isEmpty(mAppService.getHost())) {
                dismissDiaLogLoading();
                onServerError();
                return Observable.empty();
            }
            return observable
                    .doOnSubscribe(disposable -> mStateObservable = 0)
                    .doOnDispose(() -> mStateObservable = 1)
                    .doOnComplete(() -> mStateObservable = 2)
                    .doOnError(throwable -> mStateObservable = 3)
                    .doOnTerminate(() -> {
                        mStateObservable = 4;
                        dismissDiaLogLoading();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(this::handleOnError);
        }
    }

    private Observable<T> handleOnError(Throwable throwable) {
        dismissDiaLogLoading();
        LogUtils.d(throwable.toString());
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int status = httpException.code();
            ResponseBody responseBody = httpException.response().errorBody();
            if (responseBody != null) {
                Converter<ResponseBody, ErrorResponse> errorConverter =
                        AppService.getInstance().getRetrofit().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                try {
                    ErrorResponse error = errorConverter.convert(responseBody);
                    if (error != null) {
                        // show errorMsg here
                        ErrorResponse.Error errorObj = error.getError();
                        if (errorObj != null) {
                            String errorStr = errorObj.getMessage();
                            ToastUtils.showShort(errorStr);
                            onError(error, null);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //DO ERROR HANDLING HERE
            } else {
                getErrorFromCode(status);
            }
        } else {
            onException();
        }
        return Observable.empty();
    }

    public void onSuccessful(T response, Headers headers) {
        LogUtils.d("AppConnectBaseTask", "onSuccess");
        if (mOnFinishCallbackListener != null) {
            mOnFinishCallbackListener.onSuccess(response, headers);
        }
    }

    public void onSuccessful(T response) {
        LogUtils.d("AppConnectBaseTask", "onSuccess");
        if (mOnFinishCallbackListener != null) {
            mOnFinishCallbackListener.onSuccess(response, null);
        }
    }

    public void onError(ErrorResponse error, Headers headers) {
        LogUtils.d("AppConnectBaseTask", "onError");
        if (mOnFinishCallbackListener != null) {
            mOnFinishCallbackListener.onError(error, headers);
        }
    }

    public void onError(ErrorResponse error) {
        LogUtils.d("AppConnectBaseTask", "onError");
        if (mOnFinishCallbackListener != null) {
            mOnFinishCallbackListener.onError(error, null);
        }
    }

    public void onFailed(Integer pReturnFailed) {
        LogUtils.e(TAG, "onFailed");
        if (mOnFinishCallbackListener != null) {
            mOnFinishCallbackListener.onFailed(pReturnFailed);
        }
    }

    private void getErrorFromCode(int pReturnFailed) {
        switch (pReturnFailed) {
            case NOT_FOUND:
                onDocumentNotFound();
                break;
            case UNAUTHORIZED:
                onAuthenticatedFail();
                break;
            case REQUEST_TIMEOUT:
            case GATEWAY_TIMEOUT:
                onConnectionTimeout();
                break;
            case INTERNAL_SERVER_ERROR:
            case BAD_GATEWAY:
            case SERVICE_UNAVAILABLE:
                onServerError(pReturnFailed);
                break;
            default:
                onException(pReturnFailed);
                break;
        }
    }

    public void onException() {
        LogUtils.d("AppConnectBaseTask", "onException");
        onFailed(CONST_EXCEPTION);
    }

    public void onException(int status) {
        LogUtils.d("AppConnectBaseTask", "onException");
        onFailed(CONST_EXCEPTION);
    }

    public void onConnectionTimeout() {
        new Handler().post(DialogUtils.getOkAlertDialog(getContext(), "Timeout", (pAlertDialog, pDialogType) -> {

        })::show);
        LogUtils.d("AppConnectBaseTask", "onConnectionTimeout");
        onFailed(CONST_TIME_OUT);
    }

    public void onDocumentNotFound() {
        LogUtils.d("AppConnectBaseTask", "onDocumentNotFound");
        onFailed(CONST_NOT_FOUND);
    }

    public void onAuthenticatedFail() {
        LogUtils.d("AppConnectBaseTask", "onAuthenticatedFail");
        onFailed(CONST_AUTHENTICATE);
    }

    public void onNoInternetAccess() {
        new Handler(Looper.getMainLooper()).post(() -> ToastUtils.showShort(R.string.ep_err_network_failure));
        LogUtils.d("AppConnectBaseTask", "onNoInternetAccess");
        onFailed(CONST_EXCEPTION);
    }

    public void onServerError() {
        LogUtils.d("AppConnectBaseTask", "onServerError");
        onFailed(CONST_SERVER_ERROR);
    }

    public void onServerError(int status) {
        LogUtils.d("AppConnectBaseTask", "onServerError");
        onFailed(CONST_SERVER_ERROR);
    }

    public interface OnFinishCallbackListener<T> {
        void onSuccess(T response, Headers headers);

        void onError(Object error, Headers headers);

        void onFailed(Integer response);
    }

    public void dismissDiaLogLoading() {
        if (mIsShowLoading) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (mContext != null && mDialog != null && mDialog.isShowing()) {
                    try {
                        mDialog.dismiss();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public AppService getAppService() {
        return mAppService;
    }

    public void setAppService(AppService appService) {
        mAppService = appService;
    }
}
