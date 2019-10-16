package com.everypay.sdk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.everypay.sdk.EveryPay;
import com.everypay.sdk.R;
import com.everypay.sdk.activity.PaymentActivity;
import com.everypay.sdk.data.network.responsedata.PaymentStateWebviewResponse;
import com.everypay.sdk.data.network.task.GetPaymentStatusWebviewTask;
import com.everypay.sdk.data.network.task.base.BaseCallback;

import java.util.Objects;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AlternativePaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlternativePaymentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mLink;
    private String mLinkMethod;
    WebView mWvAlternativePayment;
    private boolean mIsChecking;
    private ACProgressFlower mDialog;

    // TODO: Rename and change types and number of parameters
    public static AlternativePaymentFragment newInstance(String link) {
        AlternativePaymentFragment fragment = new AlternativePaymentFragment();
        Bundle args = new Bundle();
        args.putString(PaymentActivity.LINK, link);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLink = getArguments().getString(PaymentActivity.LINK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ep_fragment_alternative_payment, container, false);
        if (mLink == null)
            return view;
        showProgress();

        mWvAlternativePayment = view.findViewById(R.id.wv_alternative_payment);
        WebSettings setting = mWvAlternativePayment.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setLoadWithOverviewMode(true);
        setting.setBuiltInZoomControls(true);
        setting.setUseWideViewPort(true);
        setting.setDomStorageEnabled(true);
        mWvAlternativePayment.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String message = consoleMessage.message();
                LogUtils.i(message);
                return super.onConsoleMessage(consoleMessage);

            }
        });
        mWvAlternativePayment.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissDiaLogLoading();
                LogUtils.i(url);
                handleGetPaymentStatus(url);
                super.onPageFinished(view, url);
            }
        });
        mWvAlternativePayment.loadUrl(mLink);
        return view;

    }

    private void handleGetPaymentStatus(String url) {
        if (mIsChecking || getActivity() == null || !mLink.contains(url))
            return;
        String urlHost = url + "?format=json";
        mIsChecking = true;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            GetPaymentStatusWebviewTask getPaymentStatusWebviewTask = new GetPaymentStatusWebviewTask(getContext(), urlHost);
            getPaymentStatusWebviewTask.observable(new BaseCallback.OnFinishCallbackListener<PaymentStateWebviewResponse>() {
                @Override
                public void onSuccess(PaymentStateWebviewResponse response, Headers headers) {
                    mIsChecking = false;
                    String status = response.getState();
                    LogUtils.i(status);
                    Activity activity = getActivity();
                    if (activity  instanceof PaymentActivity) {
                        if (StringUtils.isEmpty(status) || status.equalsIgnoreCase("failed")) {
                            Intent intent = new Intent();
                            Objects.requireNonNull(getActivity()).setResult(EveryPay.RESULT_ERROR, intent);
                            getActivity().finish();
                        }
                        if (status.equalsIgnoreCase("settled") || status.equalsIgnoreCase("completed")) {
                            Intent intent = new Intent();
                            Objects.requireNonNull(getActivity()).setResult(EveryPay.RESULT_OK, intent);
                            getActivity().finish();
                        }
                    }
                }

                @Override
                public void onError(Object error, Headers headers) {
                    mIsChecking = false;
                }

                @Override
                public void onFailed(Integer response) {
                    mIsChecking = false;
                }
            });
        });

    }


    private void showProgress() {
        new Handler(Looper.getMainLooper()).post(() -> {
            mDialog = new ACProgressFlower.Builder(getContext())
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

    public void dismissDiaLogLoading() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mDialog != null && mDialog.isShowing()) {
                try {
                    mDialog.dismiss();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
