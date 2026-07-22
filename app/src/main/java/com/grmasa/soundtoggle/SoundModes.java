package com.grmasa.soundtoggle;

import android.app.NotificationManager;
import android.media.AudioManager;
import android.os.Build;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class SoundModes {
    private SoundModes() {
    }

    public static final class Mode {
        public final String name;
        public final int icon;
        public final int targetMode;
        public final Predicate<AudioManager> condition;
        public final Consumer<SoundToggleService> activate;
        public final boolean restoreVolume; // Restore media volume on exit

        public Mode(String name, int icon, Predicate<AudioManager> condition, Consumer<SoundToggleService> activate, boolean restoreVolume, int targetMode) {
            this.name = name;
            this.icon = icon;
            this.condition = condition;
            this.activate = activate;
            this.restoreVolume = restoreVolume; // Default value
            this.targetMode = targetMode;
        }
    }

    public static final Mode[] MODES = new Mode[]{
            new Mode(
                    "Normal",
                    R.drawable.ic_audio_vol,
                    (am) -> am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL,
                    (service) -> {
                        service.getAudioManager().setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        service.vibrate();
                    },
                    false, 2
            ),
            new Mode(
                    "Vibrate",
                    R.drawable.ic_audio_ring_notif_vibrate,
                    (am) -> (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) ,
                    (service) -> {
                        AudioManager am = service.getAudioManager();
                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, 0);
                        service.vibrate();
                    },
                    false, 1
            ),
            new Mode(
                    "Silent",
                    R.drawable.ic_audio_vol_mute,
                    (am) -> am.getRingerMode() == AudioManager.RINGER_MODE_SILENT,
                    (service) -> {
                        AudioManager am = service.getAudioManager();
                        int option_silent = service.loadOption();
                        if (option_silent == 1) {
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                            NotificationManager notificationManager = service.getSystemService(NotificationManager.class);

                            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                            int allowedCategories =
                                    0;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                allowedCategories = NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS |
                                        NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA;
                            }

                            notificationManager.setNotificationPolicy(
                                    new NotificationManager.Policy(
                                            allowedCategories,
                                            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                                            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                                            0
                                    )
                            );
                        } else {
                            //Force the System UI to drop the Vibrate icon
                            if (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            }
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                    },
                    false, 0
            ),
            
    };
}