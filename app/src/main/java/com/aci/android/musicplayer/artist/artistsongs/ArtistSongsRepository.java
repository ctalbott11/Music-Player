package com.aci.android.musicplayer.artist.artistsongs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

public class ArtistSongsRepository {
    public static ArtistSongsRepository instance;

    private ArrayList<Song> artistSongs = new ArrayList<>();

    private NotificationPlayer notificationPlayer = NotificationPlayer.getInstance();
    private MuttMediaPlayer mediaPlayer = MuttMediaPlayer.getInstance();


    @NonNull
    private static MutableLiveData<ArrayList<Song>> artistSongsList = new MutableLiveData<>();

    public static ArtistSongsRepository getInstance(){
        if(instance == null){
            synchronized (ArtistSongsRepository.class){
                if(instance == null){
                    instance = new ArtistSongsRepository();
                }
            }
        }
        return instance;
    }

    @NonNull
    public MutableLiveData<ArrayList<Song>> getArtistSongs(){
        return artistSongsList;
    }

    public void onArtistSongClick(int pos){
        if(getArtistSongs().getValue() != null) {
            AllSongsRepository.setCurrentSong(getArtistSongs().getValue().get(pos));
            notificationPlayer.setUpNotificationLayout(false);
            mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getArtistSongs().getValue()).get(pos).data);
        }
    }
    public void onPreviousArtistSongClick(int pos){
        if(getArtistSongs().getValue() != null) {
            try {
                if (getArtistSongs().getValue().get(pos - 1) != null) {
                    AllSongsRepository.setCurrentSong(getArtistSongs().getValue().get(pos - 1));
                    notificationPlayer.setUpNotificationLayout(false);
                    mediaPlayer.startPlayer(AppApplication.getInstance(), getArtistSongs().getValue().get(pos - 1).data);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void onNextArtistSongClick(int pos){
        if(getArtistSongs().getValue() != null){
            try{
                if (getArtistSongs().getValue().get(pos + 1) != null) {
                    AllSongsRepository.setCurrentSong(getArtistSongs().getValue().get(pos + 1));
                    notificationPlayer.setUpNotificationLayout(false);
                    mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getArtistSongs().getValue()).get(pos + 1).data );
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void fetchArtistSongs(Context context){
        SharedPreferences preferences = Objects.requireNonNull(context).getSharedPreferences("artist", 0);
        String artist = preferences.getString("artist", "");
        
        artistSongs.clear();
        new Thread(() -> {
            try {
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
                String[] projection = {
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM
                };

                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);
                if(cursor != null) {
                    while(cursor.moveToNext()){
                        Song song = new Song(cursor.getString(1), cursor.getString(0), cursor.getString(4),
                                cursor.getString(3), cursor.getString(2));
                        if(song.artist.equals(artist)) {
                            artistSongs.add(song);
                        }
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            Set<Song> set = new HashSet<>(artistSongs);
            artistSongs.clear();
            artistSongs.addAll(set);

        }).start();

        artistSongsList.postValue(artistSongs);
    }
}
