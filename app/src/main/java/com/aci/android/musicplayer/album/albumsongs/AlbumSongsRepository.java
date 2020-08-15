package com.aci.android.musicplayer.album.albumsongs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aci.android.musicplayer.MuttMediaPlayer;
import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.allsongs.AllSongsRepository;
import com.aci.android.musicplayer.notification.NotificationPlayer;
import com.aci.android.musicplayer.utils.AppApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AlbumSongsRepository {
    public static AlbumSongsRepository instance;

    private ArrayList<Song> albumSongs = new ArrayList<>();

    private NotificationPlayer notificationPlayer = NotificationPlayer.getInstance();
    private MuttMediaPlayer mediaPlayer = MuttMediaPlayer.getInstance();


    @NonNull
    private static MutableLiveData<ArrayList<Song>> albumSongsList = new MutableLiveData<>();

    @NonNull
    private static MutableLiveData<String> albumYear = new MutableLiveData<>();

    @NonNull
    private static MutableLiveData<String> albumArtist = new MutableLiveData<>();

    public static AlbumSongsRepository getInstance(){
        if(instance == null){
            synchronized (AlbumSongsRepository.class){
                if(instance == null){
                    instance = new AlbumSongsRepository();
                }
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<ArrayList<Song>> getAlbumSongs(){
        return albumSongsList;
    }

    @NonNull
    public MutableLiveData<String> getAlbumYear(){
        return albumYear;
    }

    @NonNull
    public MutableLiveData<String> getAlbumArtist(){
        return albumArtist;
    }

    public void onAlbumSongClick(int pos){
        if(getAlbumSongs().getValue() != null) {
            AllSongsRepository.setCurrentSong(getAlbumSongs().getValue().get(pos));
            notificationPlayer.setUpNotificationLayout(false);
            mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getAlbumSongs().getValue()).get(pos).data);
        }
    }
    public void onPreviousAlbumSongClick(int pos){
        if(getAlbumSongs().getValue() != null) {
            try {
                if (getAlbumSongs().getValue().get(pos - 1) != null) {
                    AllSongsRepository.setCurrentSong(getAlbumSongs().getValue().get(pos - 1));
                    notificationPlayer.setUpNotificationLayout(false);
                    mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getAlbumSongs().getValue()).get(pos - 1).data);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void onNextAlbumSongClick(int pos){
        if(getAlbumSongs().getValue() != null){
            try{
                if (getAlbumSongs().getValue().get(pos + 1) != null) {
                    AllSongsRepository.setCurrentSong(getAlbumSongs().getValue().get(pos + 1));
                    notificationPlayer.setUpNotificationLayout(false);
                    mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getAlbumSongs().getValue()).get(pos + 1).data );
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void fetchAlbumSongs(Context context){
        SharedPreferences preferences = Objects.requireNonNull(context).getSharedPreferences("album", 0);
        String album = preferences.getString("album", "");

        ArrayList<String> years = new ArrayList<>();
        ArrayList<String> artist = new ArrayList<>();
        albumSongs.clear();
        new Thread(() -> {
            try {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR
                };

                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);
                if(cursor != null) {
                    while(cursor.moveToNext()){
                        Song song = new Song(cursor.getString(1), cursor.getString(0), cursor.getString(4),
                                cursor.getString(3), cursor.getString(2));
                        if(song.album.equals(album)) {
                            albumSongs.add(song);

                            if(cursor.getString(5) != null) {
                                years.add(cursor.getString(5));
                            }
                            if(cursor.getString(0 ) != null){
                                artist.add(cursor.getString(0));
                            }
                        }
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            Set<Song> set = new HashSet<>(albumSongs);
            albumSongs.clear();
            albumSongs.addAll(set);

            Set<String> yearSet = new HashSet<>(years);
            years.clear();
            years.addAll(yearSet);

            Set<String> artistSet = new HashSet<>(artist);
            artist.clear();
            artist.addAll(artistSet);

            albumArtist.postValue(artist.toString());

            albumYear.postValue(years.toString());

        }).start();

        albumSongsList.postValue(albumSongs);
    }


   /* public Bitmap getBitmap(String sourceFile){
        Bitmap c = null;
        metadata.setDataSource(sourceFile);

        if (metadata.getEmbeddedPicture() != null) {
            byte[] b = metadata.getEmbeddedPicture();
            c = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return c;
    }
    public void getYearMetadata(String sourceFile){
        metadata.setDataSource(sourceFile);
        albumYear.postValue(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
    } */
}
