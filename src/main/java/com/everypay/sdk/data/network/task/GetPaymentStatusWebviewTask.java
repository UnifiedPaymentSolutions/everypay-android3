package com.everypay.sdk.data.network.task;

import android.content.Context;

import com.everypay.sdk.data.network.responsedata.PaymentStateWebviewResponse;
import com.everypay.sdk.data.network.task.base.BaseCallbackTask;

import io.reactivex.Observable;

public class GetPaymentStatusWebviewTask extends BaseCallbackTask<PaymentStateWebviewResponse> {

    private String mHost;

    public GetPaymentStatusWebviewTask(Context context, String host) {
        super(context);
        mHost = host;
    }

    @Override
    protected Observable<PaymentStateWebviewResponse> processAction() {
//        if (mHost.contains("http://"))
//            mHost = mHost.replace("http://", "");
//        if (mHost.contains("https://"))
//            mHost = mHost.replace("https://", "");
//        int index = mHost.indexOf("/");
//        String host = mHost.substring(0, index + 1);
//        String endPoint = mHost.substring(index + 2, mHost.length());
        return getAppService().createService(getContext()).getPaymentStatusWebview(mHost);
    }
}
