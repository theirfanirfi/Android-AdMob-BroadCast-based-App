package com.irfanullah.adapp.adapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AdActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    ProgressBar progressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String PREFERENCE_FILE = "TO_LOAD_CHECK";
    private static final String AD_CHECK = "AdIsOn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

       preferences = getSharedPreferences(PREFERENCE_FILE,MODE_PRIVATE);
       editor = preferences.edit();

        editor.putInt(AD_CHECK, 1);
        editor.commit();
        editor.apply();

    progressBar = findViewById(R.id.progressBar);


        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.admob_app));
         mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

mInterstitialAd.setAdListener(new AdListener(){
    @Override
    public void onAdClosed() {
        super.onAdClosed();
        editor.putInt(AD_CHECK, 0);
        editor.commit();
        editor.apply();
            finishApp();

    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        mInterstitialAd.show();
        progressBar.setVisibility(View.GONE);
        editor.putInt(AD_CHECK, 1);
        editor.commit();
        editor.apply();
    }
});




    }

    @Override
    protected void onStart() {
        super.onStart();
       // mInterstitialAd.show();
    }

    private void finishApp()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.finishAndRemoveTask();
        }
        else {
            this.finishAffinity();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putInt(AD_CHECK, 1);
        editor.commit();
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putInt(AD_CHECK, 1);
        editor.commit();
        editor.apply();
    }
}
