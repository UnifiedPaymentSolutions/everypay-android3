package com.everypay.sdk.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.everypay.sdk.EveryPay;
import com.everypay.sdk.R;
import com.everypay.sdk.activity.PaymentActivity;
import com.everypay.sdk.data.network.requestdata.CardDetailRequest;
import com.everypay.sdk.data.network.requestdata.CcDetails;
import com.everypay.sdk.data.network.responsedata.CardDetailResponse;
import com.everypay.sdk.data.network.task.GetCardDetailTask;
import com.everypay.sdk.data.network.task.base.BaseCallback;
import com.everypay.sdk.model.Card;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardDetailFragment extends Fragment {

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;
    private static final String WAITING_FOR_3DS = "waiting_for_3ds_response";
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    EditText mEtCardNumber;
    EditText mEtNameOfCard;
    EditText mEtExpirationDate;
    EditText mEtCvc;
    TextInputLayout mTilCardNumber;
    TextInputLayout mTilNameOfCard;
    TextInputLayout mTilExpirationDate;
    TextInputLayout mTilCvc;
    Button mBtnPay;

    private String mApiUsername;
    private String mMobileToken;
    private String mLink;
    private String mAmount = "";

    public static CardDetailFragment newInstance(String apiUsername, String link, String mobileAccessToken, String amount, String currency) {
        CardDetailFragment fragment = new CardDetailFragment();
        Bundle args = new Bundle();
        args.putString(PaymentActivity.API_USERNAME, apiUsername);
        args.putString(PaymentActivity.LINK, link);
        args.putString(PaymentActivity.TOKEN, mobileAccessToken);
        args.putString(PaymentActivity.AMOUNT, amount);
        args.putString(PaymentActivity.CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.ep_fragment_card_detail, container, false);
        initData();

        mEtCardNumber = rootView.findViewById(R.id.et_card_number);
        mEtNameOfCard = rootView.findViewById(R.id.et_name_of_card);
        mEtCvc = rootView.findViewById(R.id.et_cvc);
        mEtExpirationDate = rootView.findViewById(R.id.et_expiration_date);
        mTilCardNumber = rootView.findViewById(R.id.til_card_number);
        mTilNameOfCard = rootView.findViewById(R.id.til_name_of_card);
        mTilCvc = rootView.findViewById(R.id.til_cvc);
        mTilExpirationDate = rootView.findViewById(R.id.til_date);
        mBtnPay = rootView.findViewById(R.id.btn_pay);


        mBtnPay.setText(getString(R.string.ep_pay, mAmount));

        mEtCardNumber.setOnFocusChangeListener((v, hasFocus) -> mEtCardNumber.setHint(hasFocus ? "XXXX-XXXX-XXXX-XXXX" : ""));

        mEtCardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
                }
            }
        });

        mEtExpirationDate.setOnFocusChangeListener((v, hasFocus) -> mEtExpirationDate.setHint(hasFocus ? "MM/YY" : ""));
        mEtExpirationDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
                    s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
                }
            }
        });

        mEtCvc.setOnFocusChangeListener((v, hasFocus) -> mEtCvc.setHint(hasFocus ? "XXX" : ""));
        mEtCvc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
                    s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
                }
            }
        });

        mBtnPay.setOnClickListener(v -> {
            CardDetailRequest request = new CardDetailRequest();
            request.setApiUsername(mApiUsername);
            request.setNonce(randomString(30));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            request.setTimestamp(TimeUtils.getNowString(format));
            CcDetails ccDetails = new CcDetails();
            ccDetails.setHolderName(mEtNameOfCard.getText().toString().trim());
            ccDetails.setNumber(mEtCardNumber.getText().toString().trim().replace("-", ""));
            ccDetails.setCvc(mEtCvc.getText().toString().trim());
            try {
                String mExpirationDate = mEtExpirationDate.getText().toString().trim();
                ccDetails.setMonth(mExpirationDate.substring(0, 2));
                ccDetails.setYear(mExpirationDate.substring(3));
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setCcDetails(ccDetails);
//            if (!validateWithToast(request, mEtCvc.getText().toString())) {
//                return;
//            }
            request.setTokenConsented(true);
            request.setMobileToken(mMobileToken);
            if (validateCard(ccDetails)) {
                GetCardDetailTask getCardDetailTask = new GetCardDetailTask(getActivity(), mMobileToken, request);
                getCardDetailTask.observable(new BaseCallback.OnFinishCallbackListener<CardDetailResponse>() {
                    @Override
                    public void onSuccess(CardDetailResponse response, Headers headers) {
                        String status = response.getStatus();
                        LogUtils.i(status);
                        Activity activity = getActivity();
                        if (activity instanceof PaymentActivity) {
                            if (StringUtils.isEmpty(status) || status.equalsIgnoreCase("failed")) {
                                Intent intent = new Intent();
                                activity.setResult(EveryPay.RESULT_ERROR, intent);
                                activity.finish();
                                return;
                            }
                            if (status.equalsIgnoreCase(WAITING_FOR_3DS)) {
                                ((PaymentActivity) activity).popFragment();
                                ((PaymentActivity) activity).replaceFragment(AlternativePaymentFragment.newInstance(mLink));
                            } else if (status.equalsIgnoreCase("settled") || status.equalsIgnoreCase("authorised")){
                                Intent intent = new Intent();
                                activity.setResult(EveryPay.RESULT_OK, intent);
                                activity.finish();
                            }

                        }
                    }

                    @Override
                    public void onError(Object error, Headers headers) {

                    }

                    @Override
                    public void onFailed(Integer response) {

                    }
                });
            }
        });
        return rootView;
    }

    /**
     * validate card's components
     * @param ccDetails: Card object
     * @return: true if card is valid
     */
    private boolean validateCard(CcDetails ccDetails) {

        if (StringUtils.isEmpty(ccDetails.getNumber()) || ccDetails.getNumber().length() != 16) {
            mEtCardNumber.requestFocus();
            ToastUtils.showShort(R.string.ep_cc_error_number_invalid);
            return false;
        }
        if (StringUtils.isEmpty(ccDetails.getHolderName())) {
            mEtNameOfCard.requestFocus();
            ToastUtils.showShort(R.string.ep_cc_error_name_missing);
            return false;
        }
        if (StringUtils.isEmpty(ccDetails.getCvc()) || ccDetails.getCvc().length() != 3) {
            mEtCvc.requestFocus();
            ToastUtils.showShort(R.string.ep_cc_error_cvc_missing);
            return false;
        }
        if (StringUtils.isEmpty(ccDetails.getMonth()) || StringUtils.isEmpty(ccDetails.getYear())) {
            mEtExpirationDate.requestFocus();
            ToastUtils.showShort(R.string.ep_cc_error_expiration_date_missing);
            return false;
        }

        try {
            int month = Integer.parseInt(ccDetails.getMonth(), 10);
            if (month < Card.MONTH_MIN_VALUE) {
                ToastUtils.showShort(R.string.ep_cc_error_month_invalid);
                return false;
            }
            if (month > Card.MONTH_MAX_VALUE) {
                ToastUtils.showShort(R.string.ep_cc_error_month_invalid);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Get data from previous screen
     */
    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        mLink = bundle.getString(PaymentActivity.LINK);
        mApiUsername = bundle.getString(PaymentActivity.API_USERNAME);
        mMobileToken = bundle.getString(PaymentActivity.TOKEN);
        String currency = bundle.getString(PaymentActivity.CURRENCY);
        String amount = bundle.getString(PaymentActivity.AMOUNT);
        if (!StringUtils.isEmpty(currency))
            mAmount += currency;
        if (!StringUtils.isEmpty(amount))
            mAmount += amount;
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(new Random().nextInt(DATA.length())));
        }

        return sb.toString();
    }


}
