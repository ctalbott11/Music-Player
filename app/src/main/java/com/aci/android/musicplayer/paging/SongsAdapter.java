package com.aci.android.musicplayer.paging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;
import com.aci.android.musicplayer.Song;
import com.aci.android.musicplayer.utils.SongsOnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongsAdapter extends PagedListAdapter<Song, SongsAdapter.SongsViewHolder> {

    private Context context;
    private SongsOnItemClickListener onItemClickListener;

    public SongsAdapter(Context mCtx) {
        super(DIFF_CALLBACK);
        this.context = mCtx;
        this.onItemClickListener = (SongsOnItemClickListener) mCtx;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_row, parent, false);
        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        Song song = getItem(position);
        assert song != null;
        holder.tvTitle.setText(song.title);
        holder.tvArtist.setText(song.artist);
        holder.tvAlbum.setText(song.album);
        int p = Integer.parseInt(song.length);
        int result = (p / 1000) % 60;

        String t = String.format(Locale.getDefault(), "%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(p), result);
        holder.tvTrackLength.setText(t);
    }

    private static DiffUtil.ItemCallback<Song> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Song>() {
                @Override
                public boolean areItemsTheSame(@NotNull Song oldItem, @NotNull Song newItem) {
                    return oldItem.title.equals(newItem.title);
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Song oldItem, @NotNull Song newItem) {
                    return oldItem.equals(newItem);
                }
            };


    class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvTitle;
        final TextView tvArtist;
        final TextView tvAlbum;
        final TextView tvTrackLength;

        public SongsViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.list_title);
            tvArtist = view.findViewById(R.id.list_artist);
            tvAlbum = view.findViewById(R.id.list_album);
            tvTrackLength = view.findViewById(R.id.row_track_length);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public SongsOnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(SongsOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
