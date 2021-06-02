package com.players.nest.ProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.players.nest.R;

public class DepositWithdrawWebViewActivity extends AppCompatActivity {
    private static final String TAG = "DepositWithdrawWebViewA";

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_deposit_web_view);
        WebView webView = findViewById(R.id.webView);
        Intent intent = getIntent();
        String amt = intent.getStringExtra("amount");
        String userID = intent.getStringExtra("userID");
        Log.e(TAG, "onCreate: " + amt + userID);
        String param = "?";
        if (!TextUtils.isEmpty(amt)) {
            param += ("pay=" + amt + "&");
        }
        if (!TextUtils.isEmpty(userID)) {
            param += ("userId=" + userID);
        }
        Log.e(TAG, "onCreate: " + param);
        // webView.setWebViewClient(new MyCustomWebViewClient(this));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("file:///android_asset/index.html" + param);

    }
}
