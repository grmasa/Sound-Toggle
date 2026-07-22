package com.grmasa.soundtoggle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class RingerService extends Service {
    private static final String CHANNEL_ID = "soundtoggle_service_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        if (intent != null) {
            int fromMode = intent.getIntExtra("from_mode", -1);
            int toMode = intent.getIntExtra("to_mode", -1);
            executeAudioSwitch(fromMode, toMode);
        }
        demoteAndExit();

        return START_NOT_STICKY;
    }


    private void createNotificationChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SoundToggle Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            nm.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Switching sound mode")
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void executeAudioSwitch(int fromMode, int toMode) {
        SoundToggleService service = SoundToggleService.getActiveInstance();
        if (service != null && fromMode >= 0 && toMode >= 0) {
            service.switchMode(fromMode, toMode);
            service.updateTile();
        }
    }

    private void demoteAndExit() {
        stopForeground(Service.STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}