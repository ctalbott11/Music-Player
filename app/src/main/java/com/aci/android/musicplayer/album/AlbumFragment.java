package com.aci.android.musicplayer.album;

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
import com.aci.android.musicplayer.databinding.AlbumFragmentBinding;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    private AlbumFragmentBinding binding;
    private AlbumAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.album_fragment, container, false);

        SongsViewModel viewModel = ViewModelProviders.of(this).get(SongsViewModel.class);

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        AlbumRepository.getInstance().fetchAlbums(getContext());

        binding.getViewmodel().getAlbums().observe(this, Observer-> setUpRecyclerView());

        return binding.getRoot();
    }

    private void setUpRecyclerView(){

        binding.albumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.albumRecyclerView.addItemDecoration(new SimpleDivider(getContext()));

        binding.albumRecyclerView.setAdapter(null);

        ArrayList<String> albums = binding.getViewmodel().getAlbums().getValue();

        adapter = new AlbumAdapter(getContext(), albums);


        binding.albumRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }
}
