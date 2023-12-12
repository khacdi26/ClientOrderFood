package com.example.dacnapp;

import android.app.Application;

import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.pyplcheckout.BuildConfig;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AVu5gFZ3Zsgyc06qZQ2PtLuSEw2uAdYPgcr-X6nStAqA4ck2KEmW582w9KKPDcUkA8xAPqQ9VLndXrqS",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                "com.example.dacnapp://paypalpay"
        ));
    }
}
