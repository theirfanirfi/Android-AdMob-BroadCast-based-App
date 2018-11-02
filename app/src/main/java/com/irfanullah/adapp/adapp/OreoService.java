package com.irfanullah.adapp.adapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static com.irfanullah.adapp.adapp.App.CHANNEL_ID;

public class OreoService extends Service {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Boolean IS_RUNNING = false;
    private final int DELAY = 300000;
    private final String NOTIFICATION_TITLE = "AdMob App";
    private final String NOTIFICATION_CONTENT = "Click Me.";
    private static final String PREFERENCE_FILE = "TO_LOAD_CHECK";
    private static final String SCREEN_CHECK = "screen";
    private static final String AD_CHECK = "AdIsOn";
    private static final String INC_CHECK = "inc";
    private static final int LOCK_COUNT = 5;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        if(!IS_RUNNING) {
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    showAdAfterEachThreeMinutes(intent);
                    handler.postDelayed(this, DELAY);
                }
            };

            handler.postDelayed(runnable, DELAY);



            IS_RUNNING = true;
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            BroadcastReceiver br = new JS.BReciever();
            registerReceiver(br, intentFilter);


            Intent notificationIntent = new Intent(this, AdActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_CONTENT)
                    .setSmallIcon(R.drawable.ic_android_black)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
            startForeground(1,notification);


        }
        return START_NOT_STICKY;
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
                    if(i == LOCK_COUNT)
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
        int IS_SCREEN_ON = preferences.getInt(SCREEN_CHECK,0);
        int IS_AD_ALREADY_DISPLAYED = preferences.getInt(AD_CHECK,1);
        if(IS_SCREEN_ON == 1 && IS_AD_ALREADY_DISPLAYED == 1)
        {
            editor.putInt(AD_CHECK,0);
            editor.commit();
            editor.apply();
            Intent ad = new Intent(getApplicationContext(),AdActivity.class);
            ad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(ad);
        }

    }
}
