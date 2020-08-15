package com.aci.android.musicplayer.artist.artistsongs;

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
import com.aci.android.musicplayer.databinding.ArtistSongsFragmentBinding;

import java.util.ArrayList;

public class ArtistSongsFragment extends Fragment {

    private ArtistSongsFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.artist_songs_fragment, container,false);
        SongsViewModel viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        ArtistSongsRepository.getInstance().fetchArtistSongs(getContext());

        final Observer<ArrayList<Song>> songsObserver = this::setUpRecyclerView;
        binding.getViewmodel().getArtistSongs().observe(this, songsObserver);

        return binding.getRoot();
    }

    private void setUpRecyclerView(ArrayList<Song> artistSongs){
        binding.artistSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.artistSongsRecyclerView.addItemDecoration(new SimpleDivider(getContext()));

        binding.artistSongsRecyclerView.setAdapter(null);

        ArtistSongsAdapter adapter = new ArtistSongsAdapter(getContext(), artistSongs);

        binding.artistSongsRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


}
