package com.pfl.chargingcontroller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ChargingService extends Service {

    private final static String LOG_TAG = "ChargingService";
    private int logCnt = 0;

    private final IBinder binder = new LocalBinder();

    ChargingReceiver chargingReceiver;

    public boolean isStarted = false;
    public int percentLimit = 80;

    private boolean isBroadcastRegistered = false;
    private boolean isBound = false;


    StopCharging stopCharging;

    MainActivity.Listener listener;

    public ChargingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "OnStart");
        startControl();
        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        isBound = true;
        Log.d(LOG_TAG, "OnBind");
        return binder;
    }
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        isBound = true;
        Log.d(LOG_TAG, "OnRebind");
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "OnUnbind");
        isBound = false;
        return false;
        //return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "OnDestroy");
        isBound = false;
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        ChargingService getService() {
            return ChargingService.this;
        }
    }

    public void startControl() {
        chargingReceiver = new ChargingReceiver();
        startForeground(1, MyNotification.getNotification(getApplicationContext()));
        registerBroadcastReceiver();
        isStarted = true;
    }

    public void stopControl() {
        isStarted = false;
        if (isBroadcastRegistered) {
            unregisterBroadcastReceiver();
        }
        if (isBound) {
            listener.onStopService();
        }
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(chargingReceiver, filter);
        isBroadcastRegistered = true;
    }

    public void unregisterBroadcastReceiver() {
        this.unregisterReceiver(chargingReceiver);
        isBroadcastRegistered = false;
    }

    public class ChargingReceiver extends BroadcastReceiver {

        private static final String LOG_TAG = "ChargingReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            String rxAction = intent.getAction();
            Log.d(LOG_TAG, "Action " + logCnt);
            if (rxAction.equals(Intent.ACTION_BATTERY_CHANGED)) {
                Log.d(LOG_TAG, "Action battery "  + logCnt);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float percentNow = level * 100 / (float) scale;

                if (percentNow >= percentLimit) {

                    Log.d(LOG_TAG, "Action battery limit " + logCnt);
                    stopCharging.disableCharging();
                    stopControl();


                }
            }
            logCnt++;

        }

    }
}