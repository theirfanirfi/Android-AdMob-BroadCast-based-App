package com.irfanullah.adapp.adapp;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

public class JS extends Service {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Boolean IS_RUNNING = false;
    public static Handler handler = new Handler();
    public static Runnable runnable;
    private final int DELAY = 300000;
    private static final String PREFERENCE_FILE = "TO_LOAD_CHECK";
    private static final String SCREEN_CHECK = "screen";
    private static final String AD_CHECK = "AdIsOn";
    private static final String INC_CHECK = "inc";
    private static final int LOCK_COUNT = 5;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCE_FILE,MODE_PRIVATE);
       final int IS_AD_ALREADY_DISPLAYED = preferences.getInt(AD_CHECK,1);






        if(!IS_RUNNING) {
            IS_RUNNING = true;
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            BroadcastReceiver br = new BReciever();
            registerReceiver(br, intentFilter);

            runnable = new Runnable() {
                @Override
                public void run() {

                    showAdAfterEachThreeMinutes(intent);


                    handler.postDelayed(this, DELAY);

                }
            };


            handler.postDelayed(runnable, DELAY);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class BReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
          SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FILE,MODE_PRIVATE);
          SharedPreferences.Editor editor = preferences.edit();
          int i = 0;
          i = preferences.getInt(INC_CHECK,0);
            switch (intent.getAction())
            {
                case Intent.ACTION_SCREEN_ON:
                    editor.putInt(SCREEN_CHECK,1);
                    editor.commit();
                    editor.apply();
                    if(i ==LOCK_COUNT)
                    {

                        i = 1;
                        editor.putInt(INC_CHECK,i);
                        editor.commit();
                        editor.apply();
                        Intent ad = new Intent(context,AdActivity.class);
                    ad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(ad);
                    }
                    else
                    {
                        i++;
                        editor.putInt(INC_CHECK,i);
                        editor.commit();
                        editor.apply();

                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    editor.putInt(SCREEN_CHECK,0);
                    editor.commit();
                    editor.apply();
                    break;

            }
        }
    }

    private void showAdAfterEachThreeMinutes(Intent intent)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFERENCE_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int IS_AD_ALREADY_DISPLAYED = preferences.getInt(AD_CHECK,1);
        int IS_SCREEN_ON = preferences.getInt(SCREEN_CHECK,0);
        if(IS_SCREEN_ON == 1 && IS_AD_ALREADY_DISPLAYED == 1)
        {
            editor.putInt(AD_CHECK,0);
            editor.commit();
            editor.apply();
            Intent ad = new Intent(getApplicationContext(),AdActivity.class);
            ad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(ad);
            handler.removeCallbacks(runnable);
        }

    }
}
