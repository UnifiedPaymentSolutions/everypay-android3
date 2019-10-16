package com.everypay.sdk.data.network.task;

import android.content.Context;

import com.everypay.sdk.data.network.requestdata.CardDetailRequest;
import com.everypay.sdk.data.network.responsedata.CardDetailResponse;
import com.everypay.sdk.data.network.task.base.BaseCallbackTask;

import io.reactivex.Observable;

public class GetCardDetailTask extends BaseCallbackTask<CardDetailResponse> {

    private String mToken;
    private CardDetailRequest mRequest;

    public GetCardDetailTask(Context context, String token, CardDetailRequest request) {
        super(context);
        mToken = token;
        mRequest = request;
    }

    @Override
    protected Observable<CardDetailResponse> processAction() {
            return getAppService().createService(getContext(), "Bearer " + mToken).getCardDetail(mRequest);
    }
}
