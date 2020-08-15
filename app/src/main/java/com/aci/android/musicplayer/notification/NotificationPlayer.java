package com.aci.android.musicplayer.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.RemoteViews;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.allsongs.AllSongsRepository;
import com.aci.android.musicplayer.utils.AppApplication;

public class NotificationPlayer {

    public static NotificationPlayer instance;

    private String title, artist;
    private Bitmap albumArt;
    public static int NOTIFICATION_ID = 100;
    public static final String NOTIFY_PREVIOUS = "com.aci.android.musicplayer.previous";
    public static final String NOTIFY_DELETE = "com.aci.android.musicplayer.delete";
    public static final String NOTIFY_PAUSE = "com.aci.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "com.aci.android.musicplayer.play";
    public static final String NOTIFY_NEXT = "com.aci.android.musicplayer.next";

    private Resources resources = AppApplication.getInstance().getResources();
    private RemoteViews simpleContentView;
    private CharSequence name = "Music Player Notification";

    Intent pause;

    public static NotificationPlayer getInstance(){
        if(instance == null){
            synchronized (NotificationPlayer.class){
                if(instance == null){
                    instance = new NotificationPlayer();
                }
            }
        }
        return instance;
    }

    public void setUpNotificationLayout(boolean paused){

        simpleContentView = new RemoteViews(AppApplication.getInstance().getContext().getPackageName(), R.layout.player_push_notification);
        getMetaData(AllSongsRepository.getCurrentSong().data);

        if(albumArt == null){
            simpleContentView.setImageViewBitmap(R.id.image, BitmapFactory.decodeResource(resources, R.drawable.no_album_art));
        } else {
            simpleContentView.setImageViewBitmap(R.id.image, albumArt);
        }
        if(artist == null){
            simpleContentView.setTextViewText(R.id.push_artist, "No Artist");
        } else {
            simpleContentView.setTextViewText(R.id.push_artist, artist);
        }
        if(title == null){
            simpleContentView.setTextViewText(R.id.push_artist, "No Title");
        } else {
            simpleContentView.setTextViewText(R.id.push_track_title, title);
        }

        setListeners(simpleContentView);

        int importance = NotificationManager.IMPORTANCE_LOW;

        String CHANNEL_ID = "my_channel_01";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

        Notification notification = new Notification.Builder(AppApplication.getInstance().getContext(), channel.toString())
                .setSmallIcon(R.drawable.cast_ic_expanded_controller_play)
                .setChannelId(CHANNEL_ID)
                .setCustomContentView(simpleContentView)
                .setOngoing(true)
                .build();
        if (paused) {
            simpleContentView.setViewVisibility(R.id.push_pause, View.GONE);
            simpleContentView.setViewVisibility(R.id.push_play, View.VISIBLE);
        } else {
            simpleContentView.setViewVisibility(R.id.push_pause, View.VISIBLE);
            simpleContentView.setViewVisibility(R.id.push_play, View.GONE);
        }

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        NotificationManager mNotificationManager = (NotificationManager) AppApplication.getInstance()
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }



    }
    private void getMetaData(String dataSource){
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(dataSource);

        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if(metadataRetriever.getEmbeddedPicture() != null){
            byte[] byteArray = metadataRetriever.getEmbeddedPicture();
            albumArt = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            simpleContentView.setImageViewResource(R.id.image, R.drawable.no_album_art);
        }
    }

    //notification player listeners
    public void setListeners(RemoteViews view) {

        Intent previous = new Intent(AppApplication.getInstance().getApplicationContext(), NotificationBroadcast.class);
        previous.setAction(NOTIFY_PREVIOUS);
        Intent delete = new Intent(AppApplication.getInstance().getApplicationContext(), NotificationBroadcast.class);
        delete.setAction(NOTIFY_DELETE);
        pause = new Intent(AppApplication.getInstance().getApplicationContext(), NotificationBroadcast.class);
        pause.setAction(NOTIFY_PAUSE);
        Intent next = new Intent(AppApplication.getInstance().getApplicationContext(), NotificationBroadcast.class);
        next.setAction(NOTIFY_NEXT);
        Intent play = new Intent(AppApplication.getInstance().getApplicationContext(), NotificationBroadcast.class);
        play.setAction(NOTIFY_PLAY);

        PendingIntent pPause = PendingIntent.getBroadcast(AppApplication.getInstance().getContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.push_pause, pPause);
        PendingIntent pPrevious = PendingIntent.getBroadcast(AppApplication.getInstance().getContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.push_previous, pPrevious);
        PendingIntent pNext = PendingIntent.getBroadcast(AppApplication.getInstance().getContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.push_next, pNext);
        PendingIntent pDelete = PendingIntent.getBroadcast(AppApplication.getInstance().getContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.push_delete, pDelete);
        PendingIntent pPlay = PendingIntent.getBroadcast(AppApplication.getInstance().getContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.push_play, pPlay);
    }
}
