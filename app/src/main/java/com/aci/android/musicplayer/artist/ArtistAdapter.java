package com.aci.android.musicplayer.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.CustomViewHolder> {
    private final ArrayList<String> artistList;
    private final OnArtistClick onArtistClick;

    ArtistAdapter(Context context, ArrayList<String> artists){
        this.artistList = artists;
        this.onArtistClick = (ArtistAdapter.OnArtistClick) context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_row, viewGroup, false);
        return new ArtistAdapter.CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        customViewHolder.tvArtist.setText(artistList.get(i));

    }

    @Override
    public int getItemCount() {
        return (artistList == null) ? 0 : artistList.size();
    }


    public interface OnArtistClick{
        void onArtistClick(int pos);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView tvArtist;

        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            tvArtist = view.findViewById(R.id.artist_tv);
        }
        @Override
        public void onClick(View view) {
            try {
                onArtistClick.onArtistClick(getAdapterPosition());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

