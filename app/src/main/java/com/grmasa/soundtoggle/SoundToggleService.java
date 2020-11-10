package com.grmasa.soundtoggle;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class SoundToggleService extends TileService {

    private final BroadcastReceiver broadcastReceiver = new Receiver();

    class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            SoundToggleService.this.updateTile();
        }
    }

    private AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private int setIcon() {
        int ringerMode = getAudioManager().getRingerMode();
        return ringerMode != 0 ? ringerMode != 1
                ? R.drawable.ic_audio_vol : R.drawable.ic_audio_ring_notif_vibrate : R.drawable.ic_audio_vol_mute;
    }

    public void updateTile() {
        Tile qsTile = getQsTile();
        int ringerMode = getAudioManager().getRingerMode();
        String tileLabel = ringerMode != 0 ? ringerMode != 1 ? ringerMode != 2
                ? "Unknown" : "Normal" : "Vibrate" : "Silent";
        qsTile.setContentDescription(tileLabel);
        qsTile.setLabel(tileLabel);
        qsTile.setIcon(Icon.createWithResource(this, setIcon()));
        qsTile.updateTile();
        qsTile.setState(2);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    public void onClick() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            int ringerMode = getAudioManager().getRingerMode();
            int nextMode = -1;
            if (ringerMode == 0) {
                vibrate();
                nextMode = 2;
            } else if (ringerMode == 1) {
                nextMode = 0;
            } else if (ringerMode == 2) {
                vibrate();
                nextMode = 1;
            }
            getAudioManager().setRingerMode(nextMode);
        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }

    }

    public void onStartListening() {
        updateTile();
        registerReceiver(this.broadcastReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
    }

    public void onStopListening() {
        unregisterReceiver(this.broadcastReceiver);
    }
}
