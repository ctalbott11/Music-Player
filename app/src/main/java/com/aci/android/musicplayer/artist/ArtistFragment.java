package com.aci.android.musicplayer.artist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.paging.SongsViewModel;
import com.aci.android.musicplayer.utils.SimpleDivider;
import com.aci.android.musicplayer.databinding.ArtistFragmentBinding;

import java.util.ArrayList;

public class ArtistFragment extends Fragment {

    private ArtistFragmentBinding binding;
    ArtistAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.artist_fragment, container, false);

        SongsViewModel viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        ArtistRepository.getInstance().fetchArtists(getContext());

        binding.getViewmodel().getArtists().observe(this, Observer -> setUpRecyclerView());

        return binding.getRoot();
    }


    private void setUpRecyclerView(){

        binding.artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.artistRecyclerView.addItemDecoration(new SimpleDivider(getContext()));

        binding.artistRecyclerView.setAdapter(null);

        ArrayList<String> artists = binding.getViewmodel().getArtists().getValue();

        adapter = new ArtistAdapter(getContext(), artists);

        binding.artistRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
