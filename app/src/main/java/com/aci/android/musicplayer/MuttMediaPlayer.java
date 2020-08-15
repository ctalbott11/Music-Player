package com.aci.android.musicplayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.aci.android.musicplayer.notification.NotificationPlayer;

import java.io.File;

public class MuttMediaPlayer {
    public static MuttMediaPlayer instance;

    public static MediaPlayer player;

    private NotificationPlayer notificationPlayer = NotificationPlayer.getInstance();


    public void startPlayer(Context context, String song) {
        stopMediaPlayer();
        player = new MediaPlayer();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        getFile(context, song);
        notificationPlayer.setUpNotificationLayout(false);

    }



    public void getFile(Context context, String song){
        File y = new File(song);
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", y);
        if (player != null) {
            try {
                player.setDataSource(context, uri);
                player.prepare();
                player.setOnPreparedListener(MediaPlayer::start);
                player.setOnCompletionListener(mp -> player.seekTo(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isNull(){
        return player == null;
    }


    public boolean isPlaying(){
        return player.isPlaying();
    }


    public void restartPlayer(){
        if(player != null){
            player.start();
            notificationPlayer.setUpNotificationLayout(false);
        }
    }



    public void stopMediaPlayer(){
        try {
            if (player != null) {
                player.reset();
                player.release();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    public void startPlayer(){
        if(player != null) {
            if (!isPlaying()) {
                player.start();
                notificationPlayer.setUpNotificationLayout(false);
            }  //player.start();

        }
    }



    public void pauseMediaPlayer(){
        if(player != null) {
            if(isPlaying()){
                notificationPlayer.setUpNotificationLayout(true);
                player.pause();

            }
        }
    }



    public void playerSeekTo(int seek){
        player.seekTo(seek);
    }



    public int getDuration(){
        return player.getDuration();
    }




    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }










    public static MuttMediaPlayer getInstance(){
        if(instance == null){
            synchronized (MuttMediaPlayer.class){
                if(instance == null){
                    instance = new MuttMediaPlayer();
                }
            }
        }
        return instance;
    }

}
