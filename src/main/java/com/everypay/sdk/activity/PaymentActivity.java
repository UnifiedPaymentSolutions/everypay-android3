package com.everypay.sdk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.everypay.sdk.R;
import com.everypay.sdk.fragment.AlternativePaymentFragment;
import com.everypay.sdk.fragment.CardDetailFragment;

public class PaymentActivity extends AppCompatActivity {

    public static final String LINK = "Link";
    public static final String HOST = "Host";
    public static final String API_USERNAME = "Api username";
    public static final String METHOD_SOURCE = "Payment method";
    public static final String CURRENCY = "Currency";
    public static final String AMOUNT = "Amount";
    public static final String TOKEN = "Token";
    public static final String CARD = "card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ep_activity_payment);
        setTitle("Every Pay");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ep_primary));
        }

        Intent intent = getIntent();
        String paymentMethod = intent.getStringExtra(METHOD_SOURCE);
        String link = intent.getStringExtra(LINK);
        String apiUsername = intent.getStringExtra(API_USERNAME);
        String mobileAccessToken = intent.getStringExtra(TOKEN);
        if (CARD.equalsIgnoreCase(paymentMethod)) {
            String amount = intent.getStringExtra(AMOUNT);
            String currency = intent.getStringExtra(CURRENCY);
            replaceFragment(CardDetailFragment.newInstance(apiUsername, link, mobileAccessToken, amount, currency));
        } else {
            replaceFragment(AlternativePaymentFragment.newInstance(link));
        }

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.cl_main_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void popFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
