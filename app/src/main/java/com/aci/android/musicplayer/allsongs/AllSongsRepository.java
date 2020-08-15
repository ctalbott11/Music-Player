package com.aci.android.musicplayer.allsongs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aci.android.musicplayer.MuttMediaPlayer;
import com.aci.android.musicplayer.Song;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AllSongsRepository {

    public static AllSongsRepository instance;

    //used to keep the currently playing track easily accessible
    public static Song currentSong;


    //get instance of our mediaPlayer
    public MuttMediaPlayer mediaPlayer = MuttMediaPlayer.getInstance();



    //liveData for mainActivity music player(textViews and a seekBar), observed in mainActivity
    @NonNull
    private static MutableLiveData<Integer> mediaDuration = new MutableLiveData<>();

    @NonNull
    private static MutableLiveData<Integer> mediaPosition = new MutableLiveData<>();

    @NonNull
    private static MutableLiveData<String> seekBarFormatString = new MutableLiveData<>();

    @NonNull
    private static MutableLiveData<String> songTitle = new MutableLiveData<>();


    //liveData that is observed for the Play buttons(mainActivity and Notification Player), these buttons must
    //be kept in sync
    @NonNull
    private static MutableLiveData<String> playPause = new MutableLiveData<>();




    //getters for our liveData
    @NonNull
    public MutableLiveData<Integer> getMediaDuration(){
        return mediaDuration;
    }

    @NonNull
    public MutableLiveData<Integer> getMediaPosition(){
        return mediaPosition;
    }

    @NonNull
    public MutableLiveData<String> getSeekBarFormatString(){
        return seekBarFormatString;
    }

    @NonNull
    public MutableLiveData<String> getSongTitle(){
        return songTitle;
    }

    @NonNull
    public MutableLiveData<String> getPlayPause(){
        return playPause;
    }



    //helper used to keep the play buttons in sync, called from observer in mainActivity and
    //also from the Notification BroadcastReceiver
    public void checkPlayButton(){
        if(MuttMediaPlayer.player != null) {
            if (mediaPlayer.isPlaying()) {
                playPause.setValue("pause");//liveData
            } else {
                playPause.setValue("play");//liveData
            }
        }
    }

    //set up liveData to be observed for seekBar
    public void setUpSeekBar(){
        int mediaDurationRaw = mediaPlayer.getDuration();
        int mediaPositionRaw = mediaPlayer.getCurrentPosition();

        mediaDuration.postValue(mediaDurationRaw);
        mediaPosition.postValue(mediaPositionRaw);

        int result = (mediaDurationRaw/1000) % 60;

        String format = String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaPositionRaw),
                TimeUnit.MILLISECONDS.toSeconds(mediaPositionRaw) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPositionRaw)))
                + " / " + String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaDurationRaw),
                result);
        Log.i("seekBar", format);
        seekBarFormatString.postValue(format);

    }

    //getter and setter for our currently playing song
    public static Song getCurrentSong(){
        return currentSong;
    }

    public static void setCurrentSong(Song song){
        songTitle.setValue(song.title);
        currentSong = song;
    }

    public static AllSongsRepository getInstance(){
        if(instance == null){
            synchronized (AllSongsRepository.class){
                if(instance == null){
                    instance = new AllSongsRepository();
                }
            }
        }
        return instance;
    }
}
