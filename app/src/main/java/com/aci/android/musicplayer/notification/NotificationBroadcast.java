package com.aci.android.musicplayer.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aci.android.musicplayer.MainActivity;
import com.aci.android.musicplayer.MuttMediaPlayer;
import com.aci.android.musicplayer.allsongs.AllSongsRepository;
import com.aci.android.musicplayer.utils.AppApplication;

//Broadcast Receiver for notification player
public class NotificationBroadcast extends BroadcastReceiver {

    private MuttMediaPlayer mediaPlayer = new MuttMediaPlayer();

    public static int NOTIFICATION_ID = 100;
    public static final String NOTIFY_PREVIOUS = "com.aci.android.musicplayer.previous";
    public static final String NOTIFY_DELETE = "com.aci.android.musicplayer.delete";
    public static final String NOTIFY_PAUSE = "com.aci.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "com.aci.android.musicplayer.play";
    public static final String NOTIFY_NEXT = "com.aci.android.musicplayer.next";

    public static MainActivity mainActivity;
    public static void setMainActivity(MainActivity activity) { mainActivity = activity; }

    public NotificationBroadcast(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        mainActivity.runOnUiThread(() -> {
            if (intent.getAction() != null) {
                try {
                    switch (intent.getAction()) {
                        case NOTIFY_PLAY:
                            if(MuttMediaPlayer.player != null){
                                mediaPlayer.startPlayer();
                                AllSongsRepository.getInstance().checkPlayButton();
                            }
                            break;
                        case NOTIFY_PAUSE:
                            if(MuttMediaPlayer.player != null){
                                mediaPlayer.pauseMediaPlayer();
                                AllSongsRepository.getInstance().checkPlayButton();
                            }

                            break;
                        case NOTIFY_NEXT:
                            mainActivity.onNextSongClick();
                            break;
                        case NOTIFY_DELETE:
                            if(MuttMediaPlayer.player != null){
                                mediaPlayer.pauseMediaPlayer();
                            }
                            AllSongsRepository.getInstance().checkPlayButton();
                            try {
                                NotificationManager mNotificationManager = (NotificationManager) AppApplication.getInstance()
                                        .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                if (mNotificationManager != null) {
                                    mNotificationManager.cancel(NOTIFICATION_ID);
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            break;
                        case NOTIFY_PREVIOUS:
                            mainActivity.onPreviousSongClick();
                            break;
                    }
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            }

        });
    }
}
