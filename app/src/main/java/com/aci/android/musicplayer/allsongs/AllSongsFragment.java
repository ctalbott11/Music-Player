package com.aci.android.musicplayer.allsongs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.utils.SongsOnItemClickListener;
import com.aci.android.musicplayer.paging.SongsAdapter;
import com.aci.android.musicplayer.paging.SongsViewModel;
import com.aci.android.musicplayer.utils.SimpleDivider;
import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.databinding.AllSongsFragmentBinding;


public class AllSongsFragment extends Fragment implements SongsOnItemClickListener {

    private AllSongsFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        binding = DataBindingUtil.inflate(inflater,
                R.layout.all_songs_fragment, container, false);

        binding.allSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.allSongsRecyclerView.setHasFixedSize(true);
        binding.allSongsRecyclerView.addItemDecoration(new SimpleDivider(getContext()));

        SongsViewModel songsViewModel = ViewModelProviders.of(this).get(SongsViewModel.class);


        binding.setLifecycleOwner(this);
        binding.setViewmodel(songsViewModel);

        final SongsAdapter adapter = new SongsAdapter(getContext());

        binding.getViewmodel().getSongsLiveData().observe(this, adapter::submitList);

        binding.allSongsRecyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);


        return binding.getRoot();
    }

    @Override
    public void onItemClick(int position) {
        binding.getViewmodel().onItemClick(position);
       // AllSongsRepository.getInstance().checkPlayButton();
        }

    @Override
    public void onPlayClick(View v) {

    }

    @Override
    public void onPreviousSongClick() {
        binding.getViewmodel().onPreviousSongClick();
    }

    @Override
    public void onNextSongClick() {
        binding.getViewmodel().onNextSongClick();
    }

}
