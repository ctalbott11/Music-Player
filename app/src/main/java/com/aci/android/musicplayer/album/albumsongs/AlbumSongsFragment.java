package com.aci.android.musicplayer.album.albumsongs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.paging.SongsViewModel;
import com.aci.android.musicplayer.utils.SimpleDivider;
import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.databinding.AlbumSongsFragmentBinding;

import java.util.ArrayList;

public class AlbumSongsFragment extends Fragment {

    public AlbumSongsFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.album_songs_fragment, container, false);

        SongsViewModel viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        AlbumSongsRepository.getInstance().fetchAlbumSongs(getContext());


        setTextViews();

        final Observer<String> albumObserver = s -> {
            s = s.replaceAll("[\\[\\](){}]","");

            binding.albumYear.setText(s);
        };
        binding.getViewmodel().getAlbumYear().observe(this, albumObserver);

        final Observer<String> artistObserver = s -> {
            s = s.replaceAll("[\\[\\](){}]","");

            binding.albumArtist.setText(s);
        };
        binding.getViewmodel().getAlbumArtist().observe(this, artistObserver);


        final Observer<ArrayList<Song>> songsObserver = this::setUpRecyclerView;
        binding.getViewmodel().getAlbumSongs().observe(this, songsObserver);

        return binding.getRoot();
    }

    private void setUpRecyclerView(ArrayList<Song> albumSongs){
        binding.albumSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.albumSongsRecyclerView.addItemDecoration(new SimpleDivider(getContext()));

        binding.albumSongsRecyclerView.setAdapter(null);

        AlbumSongsAdapter adapter = new AlbumSongsAdapter(getContext(), albumSongs);

        binding.albumSongsRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();



    }
    private void setTextViews(){
        if(getContext() != null) {
            binding.albumTitle.setText(getContext().getSharedPreferences("album", 0).getString("album", ""));
        }

    }

    //private Bitmap getArt(){
        //return AlbumSongsRepository.getInstance().getBitmap(albumSongs.get(0).data);
    //}

}
