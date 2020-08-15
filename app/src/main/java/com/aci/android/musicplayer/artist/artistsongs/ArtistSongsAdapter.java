package com.aci.android.musicplayer.artist.artistsongs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ArtistSongsAdapter extends RecyclerView.Adapter<ArtistSongsAdapter.CustomViewHolder> {
    private final ArrayList<Song> artistSongsList;
    //private String artist;
    private final ArtistSongsAdapter.OnArtistSongsClick onArtistSongsClick;

    ArtistSongsAdapter(Context context, ArrayList<Song> artistSongs){
        this.artistSongsList = artistSongs;
        this.onArtistSongsClick = (ArtistSongsAdapter.OnArtistSongsClick) context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_row, viewGroup, false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        Song song = artistSongsList.get(i);
        customViewHolder.tvArtist.setText(song.artist);
        customViewHolder.tvTitle.setText(song.title);
        customViewHolder.tvAlbum.setText(song.album);

        int p = Integer.parseInt(song.length);
        int result = (p / 1000) % 60;

        String s = String.format(Locale.getDefault(), "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(p), result);
        customViewHolder.tvTrackLength.setText(s);
    }

    @Override
    public int getItemCount() {
        return (artistSongsList == null) ? 0 : artistSongsList.size();
    }
    public interface OnArtistSongsClick{
        void onArtistSongsClick(int pos) throws IOException;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvArtist;
        final TextView tvTitle;
        final TextView tvAlbum;
        final TextView tvTrackLength;

        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            tvArtist = view.findViewById(R.id.list_artist);
            tvTitle = view.findViewById(R.id.list_title);
            tvAlbum = view.findViewById(R.id.list_album);
            tvTrackLength = view.findViewById(R.id.row_track_length);
        }
        @Override
        public void onClick(View view) {
            try {
                onArtistSongsClick.onArtistSongsClick(getAdapterPosition());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
