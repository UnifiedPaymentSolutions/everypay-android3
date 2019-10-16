package com.everypay.sdk.data.network.task.base;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public abstract class BaseCallbackTask<T> extends BaseCallback<T> {

    private CompositeDisposable mCompositeDisposable;


    public BaseCallbackTask(Context context, boolean showLoading) {
        super(context, showLoading);
    }

    public BaseCallbackTask(Context context) {
        this(context, true);
    }



    protected abstract Observable<T> processAction();

    public void observable(OnFinishCallbackListener<T> onFinishCallbackListener) {
        mOnFinishCallbackListener = onFinishCallbackListener;
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(processSubscribe(processAction()).subscribeWith(new DisposableObserver<T>() {
            @Override
            public void onNext(T value) {
                if (value != null) {
                    onSuccessful(value);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (mCompositeDisposable != null)
                    mCompositeDisposable.clear();
                dismissDiaLogLoading();
            }
        }));
    }
}