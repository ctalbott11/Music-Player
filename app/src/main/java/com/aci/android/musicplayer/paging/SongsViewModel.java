package com.aci.android.musicplayer.paging;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.aci.android.musicplayer.MuttMediaPlayer;
import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.album.AlbumRepository;
import com.aci.android.musicplayer.album.albumsongs.AlbumSongsAdapter;
import com.aci.android.musicplayer.album.albumsongs.AlbumSongsRepository;
import com.aci.android.musicplayer.allsongs.AllSongsRepository;
import com.aci.android.musicplayer.artist.ArtistRepository;
import com.aci.android.musicplayer.artist.artistsongs.ArtistSongsAdapter;
import com.aci.android.musicplayer.artist.artistsongs.ArtistSongsRepository;
import com.aci.android.musicplayer.notification.NotificationPlayer;
import com.aci.android.musicplayer.utils.AppApplication;

import java.util.ArrayList;
import java.util.Objects;


public class SongsViewModel extends AndroidViewModel implements
        AlbumSongsAdapter.OnAlbumSongsClick, ArtistSongsAdapter.OnArtistSongsClick {


    //paging library liveData, dataSource and factory
    public LiveData<PagedList<Song>> songsPagedList;
    private SongsDataSourceFactory itemDataSourceFactory;
    LiveData<SongsDataSource> liveDataSource;


    //get instances of the notificationPlayer and mediaPlayer
    private NotificationPlayer notificationPlayer = NotificationPlayer.getInstance();
    private MuttMediaPlayer mediaPlayer = MuttMediaPlayer.getInstance();



    //liveData observed by artistSongsFragment
    @NonNull
    private MutableLiveData<ArrayList<Song>> artistSongsList;

    //liveData observed by albumSongsFragment
    @NonNull
    private MutableLiveData<ArrayList<Song>> albumSongsList;

    @NonNull
    private MutableLiveData<String> albumYear;

    @NonNull
    private MutableLiveData<String> albumArtist;

    //liveData used for mainActivity seekBar and textViews
    @NonNull
    private MutableLiveData<Integer> mediaDuration;

    @NonNull
    private MutableLiveData<Integer> mediaPosition;

    @NonNull
    private MutableLiveData<String> seekBarFormatString;

    @NonNull
    private MutableLiveData<String> songTitle;

    //liveData observed to keep the play buttons in sync
    @NonNull
    private MutableLiveData<String> playPause;

    //liveData used to populate albumFragment
    @NonNull
    private MutableLiveData<ArrayList<String>> albumList;

    //liveData used to populate artistFragment
    @NonNull
    private MutableLiveData<ArrayList<String>> artistList;

    //get instances of our Artist and Album Repos
    private ArtistSongsRepository artistRepo;
    private AlbumSongsRepository albumRepo;

    //constructor
    public SongsViewModel(@NonNull Application application){
        super(application);

        //paging library
        itemDataSourceFactory = new SongsDataSourceFactory();
        loadSongs();

        //get AllSongsRepo instance and sync liveData
        AllSongsRepository repo = AllSongsRepository.getInstance();
        mediaDuration = repo.getMediaDuration();
        mediaPosition = repo.getMediaPosition();
        seekBarFormatString = repo.getSeekBarFormatString();
        songTitle = repo.getSongTitle();
        playPause = repo.getPlayPause();

        //get ArtistSongsRepo instance and sync liveData
        artistRepo = ArtistSongsRepository.getInstance();
        artistSongsList = artistRepo.getArtistSongs();

        //getAlbumSongsRepo instance and sync liveData
        albumRepo = AlbumSongsRepository.getInstance();
        albumSongsList = albumRepo.getAlbumSongs();
        albumYear = albumRepo.getAlbumYear();
        albumArtist = albumRepo.getAlbumArtist();

        //getAlbumRep and getArtistRepo instances and sync liveData
        AlbumRepository albumRepository = AlbumRepository.getInstance();
        albumList = albumRepository.getAlbums();

        ArtistRepository artistRepository = ArtistRepository.getInstance();
        artistList = artistRepository.getArtists();

    }

    //PagedList liveData getter and load method
    public LiveData<PagedList<Song>> getSongsLiveData() {
        return songsPagedList;
    }
    public void loadSongs(){
        liveDataSource = SongsDataSourceFactory.getMutableLiveData();
        //Getting PagedList config
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(SongsDataSource.PAGE_SIZE).build();

        songsPagedList =  (new LivePagedListBuilder<>(itemDataSourceFactory, pagedListConfig))
                .build();
    }

    //play button sync
    @NonNull
    public LiveData<String> getPlayPause(){
        return playPause;
    }

    //getters for albumSongsFragment and artistSongsFragment
    @NonNull
    public LiveData<ArrayList<Song>> getArtistSongs(){
        return artistSongsList;
    }

    @NonNull
    public LiveData<ArrayList<Song>> getAlbumSongs(){
        return albumSongsList;
    }

    @NonNull
    public LiveData<String> getAlbumYear(){
        return albumYear;
    }

    @NonNull
    public LiveData<String> getAlbumArtist(){
        return albumArtist;
    }

    //getters for mainActivity player textViews data and seekBar data
    @NonNull
    public LiveData<Integer> getMediaDuration(){
        return mediaDuration;
    }

    @NonNull
    public LiveData<Integer> getPosition(){
        return mediaPosition;
    }

    @NonNull
    public LiveData<String> getSeekBarFormatString(){
        return seekBarFormatString;
    }

    @NonNull
    public LiveData<String> getSongTitle(){
        return songTitle;
    }


    //getters to populate albumFragment with albums and artistFragment with artists
    @NonNull
    public LiveData<ArrayList<String>> getAlbums(){
        return albumList;
    }

    @NonNull
    public LiveData<ArrayList<String>> getArtists(){
        return artistList;
    }
    //Integer used to keep track of current position in recyclerView(s)
    int pos = 0;

    //click listener for AllSongsFragment recyclerView, plays selected song
    public void onItemClick(int position) {
        if(Objects.requireNonNull(getSongsLiveData().getValue()).get(position) != null) {
            //set our current song
            AllSongsRepository.setCurrentSong(Objects.requireNonNull(getSongsLiveData().getValue().get(position)));
            //set up our notification player and pass a boolean used to sync notification player play button
            notificationPlayer.setUpNotificationLayout(false);

            //AllSongsRepository.getInstance().setUpSeekBar();
            //set our class variable to selected position
            pos = position;
            //update play button liveData
            playPause.setValue("pause");
            //finally, start our mediaPlayer with selected song
            mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getSongsLiveData().getValue().get(position)).data);
        }
    }
    //click listener for previous song buttons
    public void onPreviousSongClick()  {
        try {
            if (getSongsLiveData().getValue() != null) {
                if (getSongsLiveData().getValue().get(pos - 1) != null) {
                    if (!mediaPlayer.isNull()) {
                        if (mediaPlayer.isPlaying()) {
                            //set previous song
                            AllSongsRepository.setCurrentSong(Objects.requireNonNull(Objects.requireNonNull(getSongsLiveData().getValue()).get(pos - 1)));
                            //set up notification player
                            notificationPlayer.setUpNotificationLayout(false);
                            //play previous song
                            mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getSongsLiveData().getValue().get(pos - 1)).data);
                            //keep class variable Integer in sync with current position
                            pos--;
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    //click listener for next song buttons
    public void onNextSongClick() {
        try {
            if (getSongsLiveData().getValue() != null) {
                if (getSongsLiveData().getValue().get(pos + 1) != null) {
                    if (!mediaPlayer.isNull()) {
                        if (mediaPlayer.isPlaying()) {
                            //set next song
                            AllSongsRepository.setCurrentSong(Objects.requireNonNull(getSongsLiveData().getValue().get(pos + 1)));
                            //set up notificaton player
                            notificationPlayer.setUpNotificationLayout(false);
                            //play next song
                            mediaPlayer.startPlayer(AppApplication.getInstance(), Objects.requireNonNull(getSongsLiveData().getValue().get(pos + 1)).data);
                            //track our position
                            pos++;
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //checks if mediaPlayer is playing
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    //checks if mediaPlayer is null
    public boolean isPlayerNull(){
        return !mediaPlayer.isNull();
    }

    //pause mediaPlayer
    public void pauseMediaPlayer(){
        mediaPlayer.pauseMediaPlayer();
    }
    //restart mediaPlayer
    public void restartPlayer(){
        mediaPlayer.restartPlayer();
    }

    //seekBar seekTo, allows rewinding and fast forward using seekBar
    public void playerSeekTo(int seekTo){
        mediaPlayer.playerSeekTo(seekTo);
    }




    //click listeners for artistSongsFragment
    @Override
    public void onArtistSongsClick(int pos) {
        artistRepo.onArtistSongClick(pos);
    }
    public void onNextArtistSongClick(int pos) {
        artistRepo.onNextArtistSongClick(pos);
    }
    public void onPreviousArtistSongClick(int pos) {
        artistRepo.onPreviousArtistSongClick(pos);
    }


    //AlbumSongsFragment Button Listeners
    @Override
    public void onAlbumSongsClick(int pos) {
        albumRepo.onAlbumSongClick(pos);
    }
    public void onPreviousAlbumSongClick(int pos) {
        albumRepo.onPreviousAlbumSongClick(pos);
    }
    public void onNextAlbumSongClick(int pos) {
        albumRepo.onNextAlbumSongClick(pos);
    }
}
