package com.grmasa.soundtoggle;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.Vibrator;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        int currentMode = getAudioManager().getRingerMode();
        Set<Integer> excludedModes = loadExcludedModes();

        int nextMode = getNextAllowedRingerMode(currentMode, excludedModes);
        if (nextMode != currentMode) {
            if (nextMode == AudioManager.RINGER_MODE_VIBRATE || nextMode == AudioManager.RINGER_MODE_NORMAL) {
                vibrate();
            }
            int option_silent = loadOption();
            if (option_silent==1 &&nextMode == AudioManager.RINGER_MODE_SILENT) {
                getAudioManager().setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                saveCurrentRingerMode(AudioManager.RINGER_MODE_SILENT);

                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                int allowedCategories =
                        NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS |
                                NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA;

                notificationManager.setNotificationPolicy(
                        new NotificationManager.Policy(
                                allowedCategories, // Minimal allowed interruptions
                                NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                                NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                                0
                        )
                );
            } else {
                getAudioManager().setRingerMode(nextMode);
                saveCurrentRingerMode(nextMode);
            }
        }
        updateTile();
    }

    private int loadOption() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        return prefs.getInt("toggle_option", 0);
    }

    private void saveCurrentRingerMode(int mode) {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putInt("last_ringer_mode", mode);
        editor.apply();
    }

    private Set<Integer> loadExcludedModes() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        Set<String> excludedStrings = prefs.getStringSet("excluded_modes", new HashSet<>());

        Set<Integer> excluded = new HashSet<>();
        for (String s : excludedStrings) {
            switch (s) {
                case "NORMAL" -> excluded.add(AudioManager.RINGER_MODE_NORMAL);
                case "VIBRATE" -> excluded.add(AudioManager.RINGER_MODE_VIBRATE);
                case "SILENT" -> excluded.add(AudioManager.RINGER_MODE_SILENT);
            }
        }
        return excluded;
    }

    private int getNextAllowedRingerMode(int currentMode, Set<Integer> excluded) {
        List<Integer> allModes = Arrays.asList(
                AudioManager.RINGER_MODE_NORMAL,
                AudioManager.RINGER_MODE_VIBRATE,
                AudioManager.RINGER_MODE_SILENT
        );

        int currentIndex = allModes.indexOf(currentMode);
        int size = allModes.size();

        for (int i = 1; i <= size; i++) {
            int nextMode = allModes.get((currentIndex + i) % size);
            if (!excluded.contains(nextMode)) {
                return nextMode;
            }
        }
        return currentMode;
    }

    private void restoreRingerMode() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        int lastMode;
        if (prefs.contains("last_ringer_mode")) {
            lastMode = prefs.getInt("last_ringer_mode", AudioManager.RINGER_MODE_NORMAL);
        } else {
            lastMode = AudioManager.RINGER_MODE_NORMAL;
        }
        getAudioManager().setRingerMode(lastMode);
    }

    public void onStartListening() {
        restoreRingerMode();
        updateTile();
        registerReceiver(this.broadcastReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
    }

    public void onStopListening() {
        unregisterReceiver(this.broadcastReceiver);
    }
}
