package com.irfanullah.adapp.adapp;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    ProgressBar progressBar;
    private int IS_MAIN_ACTIVITY = 0;
    private int IS_CHECK_VAR = 0;
    private Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String PREFERENCE_FILE = "TO_LOAD_CHECK";
    private static final String SCREEN_CHECK = "screen";
    private static final String AD_CHECK = "AdIsOn";
    private static final String INC_CHECK = "inc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        preferences = getSharedPreferences(PREFERENCE_FILE,MODE_PRIVATE);
        IS_CHECK_VAR = preferences.getInt(INC_CHECK,0);
        editor = preferences.edit();


        if(IS_CHECK_VAR > 5) {
            editor.putInt(INC_CHECK, 0);
            editor.commit();
            editor.apply();
        }

        //SCREEN LIGHT IS ON.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(myKM.isDeviceLocked()) {
                editor.putInt(SCREEN_CHECK, 1);
                editor.commit();
                editor.apply();
            }
            else
            {
                editor.putInt(SCREEN_CHECK, 1);
                editor.commit();
                editor.apply();
            }
        }


    progressBar = findViewById(R.id.progressBar2);
    context = this;
    progressBar.setVisibility(View.GONE);
        IS_MAIN_ACTIVITY++;



        if((Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) )
        {
//            Intent i = new Intent(this, JS.class);
//            startService(i);
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, JS.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(), 2000, pintent);


        }
        //FOR VERSIONS GREATER THANT ANDROID O.
        // A separate class is coded for this functionality.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Intent i = new Intent(this,OreoService.class);
            startService(i);
        }
        else
        {
            Intent i = new Intent(this,JS.class);
            startService(i);
        }

        //load the add if main activity is on.
        loadAd();

    }


    private void finishApp()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.finishAndRemoveTask();

        }
        else {
            this.finishAffinity();
            this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IS_MAIN_ACTIVITY++;
        //load the add on Activity Resumption
        loadAd();
    }

    public void loadAd()
    {
        if(IS_MAIN_ACTIVITY > 0)
        {
            progressBar.setVisibility(View.VISIBLE);
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

//                    int id = android.os.Process.myPid();
//                    android.os.Process.killProcess(id);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(powerManager.isScreenOn())
        {
            editor.putInt(SCREEN_CHECK, 1);
            editor.putInt(AD_CHECK, 1);

            editor.commit();
            editor.apply();
        }
        else
        {
            editor.putInt(SCREEN_CHECK, 0);
            editor.putInt(AD_CHECK, 1);

            editor.commit();
            editor.apply();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(powerManager.isScreenOn())
        {
            editor.putInt(SCREEN_CHECK, 1);
            editor.putInt(AD_CHECK, 1);

            editor.commit();
            editor.apply();

        }
        else
        {
            editor.putInt(SCREEN_CHECK, 0);
            editor.putInt(AD_CHECK, 1);
            editor.commit();
            editor.apply();
        }

    }
}
