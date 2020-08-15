package com.aci.android.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.SeekBar;

import com.aci.android.musicplayer.album.AlbumAdapter;
import com.aci.android.musicplayer.album.AlbumFragment;
import com.aci.android.musicplayer.album.AlbumRepository;
import com.aci.android.musicplayer.album.albumsongs.AlbumSongsAdapter;
import com.aci.android.musicplayer.album.albumsongs.AlbumSongsFragment;
import com.aci.android.musicplayer.allsongs.AllSongsFragment;
import com.aci.android.musicplayer.allsongs.AllSongsRepository;
import com.aci.android.musicplayer.artist.ArtistAdapter;
import com.aci.android.musicplayer.artist.ArtistFragment;
import com.aci.android.musicplayer.artist.ArtistRepository;
import com.aci.android.musicplayer.artist.artistsongs.ArtistSongsAdapter;
import com.aci.android.musicplayer.artist.artistsongs.ArtistSongsFragment;
import com.aci.android.musicplayer.databinding.ActivityMainBinding;
import com.aci.android.musicplayer.notification.ClosingService;
import com.aci.android.musicplayer.notification.NotificationBroadcast;
import com.aci.android.musicplayer.paging.SongsViewModel;
import com.aci.android.musicplayer.utils.SongsOnItemClickListener;
import com.aci.android.musicplayer.utils.ViewPageAdapter;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SongsOnItemClickListener,
        ArtistAdapter.OnArtistClick, ArtistSongsAdapter.OnArtistSongsClick, AlbumAdapter.OnAlbumClick,
        AlbumSongsAdapter.OnAlbumSongsClick {

    private ActivityMainBinding binding;

    private SongsViewModel allSongsViewModel;

    public int index = 0;
    public NotificationBroadcast notificationBroadcast;
    private ViewPageAdapter adapter;

    private AllSongsFragment allSongsFragment = new AllSongsFragment();
    private ArtistFragment artistFragment = new ArtistFragment();
    private AlbumFragment albumFragment = new AlbumFragment();

    public static final String REGISTER_RECEIVER = "com.aci.android.musicplayer.REGISTER_RECEIVER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //viewModel for mainActivity and allSongsFragment
        allSongsViewModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        setUpViewPager();


        //set up notification player
        notificationBroadcast = new NotificationBroadcast();
        NotificationBroadcast.setMainActivity(this);

        //bind lifeCycleOwner and viewModel
        binding.setLifecycleOwner(this);
        binding.setViewmodel(allSongsViewModel);
        binding.setMain(this);

        //start service that makes sure notification player closes when it should
        startService(new Intent(this, ClosingService.class));


        //observer for current time/song length textView
        final Observer<String> seekBarFormatObserver = s -> binding.trackLength.setText(s);
        binding.getViewmodel().getSeekBarFormatString().observe(this, seekBarFormatObserver);


        //observer for song duration, sets the seekBar max for each song
        final Observer<Integer> mediaDurationObserver = s -> {
            if(binding.getViewmodel().getMediaDuration().getValue() != null) {
                binding.seekBar.setMax(s);
            }
        };
        binding.getViewmodel().getMediaDuration().observe(this, mediaDurationObserver);


        //observes if mediaPlayer is playing or is paused, and sets the play button image accordingly, mainly used for orientation changes
        AllSongsRepository.getInstance().checkPlayButton();
        final Observer<String> playPauseObserver = s -> {
            if(binding.getViewmodel().getPlayPause().getValue() != null) {
                setPlayButtonImage(s);
            }
        };
        binding.getViewmodel().getPlayPause().observe(this, playPauseObserver);


        //observes the seekBar position and keeps it at the right place
        final Observer<Integer> mediaPositionObserver = s -> {
            if(binding.getViewmodel().getPosition().getValue() != null) {
                binding.seekBar.setProgress(s);
                seekListener();

            }
        };
        binding.getViewmodel().getPosition().observe(this, mediaPositionObserver);


        //observes the current song title so it is displayed
        final Observer<String> songTitleObserver = s -> {
            if(binding.getViewmodel().getSongTitle().getValue() != null) {
                binding.playing.setText(s);
            }
        };
        binding.getViewmodel().getSongTitle().observe(this, songTitleObserver);

    }

    //sets play button image accordingly
    private void setPlayButtonImage(String string){
        if(string.equals("pause")){
            binding.play.setBackgroundResource(android.R.drawable.ic_media_pause);
        } else {
            binding.play.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    //Overridden from SongsOnItemClickListener, used in SongsAdapter

    @Override
    public void onItemClick(int position) {
       /* allSongsFragment.onItemClick(position);
        if(binding.getViewmodel().isPlaying()){
            seekUpdate();
            index = position;
        }*/
    }
    //click listeners for previous and next songs, respectively.
    //linked to buttons using data binding
    @Override
    public void onPreviousSongClick() {
        if (binding.pager.getCurrentItem() == 0) {
            allSongsFragment.onPreviousSongClick();
            index--;
        } else if (binding.pager.getCurrentItem() == 1) {
            allSongsViewModel.onPreviousArtistSongClick(index);
            index--;
        } else if (binding.pager.getCurrentItem() == 2) {
            allSongsViewModel.onPreviousAlbumSongClick(index);
            index--;
        }
    }

    @Override
    public void onNextSongClick()  {
        if (binding.pager.getCurrentItem() == 0) {
            allSongsFragment.onNextSongClick();
            index++;
        } else if (binding.pager.getCurrentItem() == 1) {
            allSongsViewModel.onNextArtistSongClick(index);
            index++;
        } else if (binding.pager.getCurrentItem() == 2) {
            allSongsViewModel.onNextAlbumSongClick(index);
            index++;
        }
    }

    //play button click listener, links to play button using data binding
    @Override
    public void onPlayClick(View v) {
        if(binding.getViewmodel().isPlayerNull()) {
            if (binding.getViewmodel().isPlaying()) {
                binding.getViewmodel().pauseMediaPlayer();
                binding.play.setBackgroundResource(android.R.drawable.ic_media_play);
            } else {
                binding.getViewmodel().restartPlayer();
                binding.play.setBackgroundResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    //click listener overridden from ArtistAdapter
    @Override
    public void onArtistClick(int pos) {
        SharedPreferences.Editor editor = this.getSharedPreferences("artist", 0).edit();
        editor.putString("artist", Objects.requireNonNull(ArtistRepository.getInstance().getArtists().getValue()).get(pos));
        editor.apply();

        adapter.replaceFragment(new ArtistSongsFragment(), binding.pager.getCurrentItem());
    }

    //click listener for artistSongsFragment recyclerView, overridden from ArtistSongsAdapter
    @Override
    public void onArtistSongsClick(int pos) {
        binding.getViewmodel().onArtistSongsClick(pos);
        if(binding.getViewmodel().isPlaying()) {
            seekUpdate();
            index = pos;
            binding.play.setBackgroundResource(android.R.drawable.ic_media_play);
        } else {
            seekUpdate();
            index = pos;
            binding.play.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }
    //click listener overridden from AlbumAdapter
    @Override
    public void onAlbumClick(int pos) {
        SharedPreferences.Editor editor = this.getSharedPreferences("album", 0).edit();
        editor.putString("album", Objects.requireNonNull(AlbumRepository.getInstance().getAlbums().getValue()).get(pos));
        editor.apply();

        adapter.replaceFragment(new AlbumSongsFragment(), binding.pager.getCurrentItem());
    }


    //click listener for AlbumSongsFragment, overridden from AlbumSongsAdapter
    @Override
    public void onAlbumSongsClick(int pos){
        binding.getViewmodel().onAlbumSongsClick(pos);
        if(binding.getViewmodel().isPlaying()) {
            seekUpdate();
            index = pos;
            binding.play.setBackgroundResource(android.R.drawable.ic_media_play);
        } else {
            seekUpdate();
            index = pos;
            binding.play.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    //if back pressed depends on which page of viewPager you are on, still not ideal
    @Override
    public void onBackPressed(){
        if(binding.pager.getCurrentItem() == 1) {
            adapter.replaceFragment(artistFragment, binding.pager.getCurrentItem());
        } else if(binding.pager.getCurrentItem() == 2){
            adapter.replaceFragment(albumFragment, binding.pager.getCurrentItem());
        } else {
            finish();
        }
    }

    //register Broadcast Receiver for notification player
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(notificationBroadcast, IntentFilter.create(REGISTER_RECEIVER, "text/*"));
    }

    //unregister Broadcast Receiver
    @Override
    protected void onStop() {
        super.onStop();
        // don't forget to remove receiver data source
        unregisterReceiver(notificationBroadcast);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    //viewPager set up
    private void setUpViewPager(){
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        adapter.addFragment(allSongsFragment, "All Songs");
        adapter.addFragment(artistFragment, "Artist");
        adapter.addFragment(albumFragment, "Album");

        binding.pager.setAdapter(adapter);

        binding.tabs.setupWithViewPager(binding.pager);

        binding.pager.setCurrentItem(0);
        adapter.notifyDataSetChanged();

    }

    //seekBar listener
    public void seekListener() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.getViewmodel().playerSeekTo(seekBar.getProgress());
            }
        });
    }
    //create runnable for seekBar
    private final Runnable run = this::seekUpdate;

    //set up seekBar and Handler
    public void seekUpdate(){
        if(binding.getViewmodel().isPlayerNull()) {
            AllSongsRepository.getInstance().setUpSeekBar();
           // seekListener();
            new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(run, 100);
        } else {
            binding.seekBar.setProgress(0);
        }
    }



}