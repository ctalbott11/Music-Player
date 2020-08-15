package com.aci.android.musicplayer.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aci.android.musicplayer.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.CustomViewHolder> {
    private final ArrayList<String> albumList;
    private final OnAlbumClick onAlbumClick;

    AlbumAdapter(Context context, ArrayList<String> albums){
        this.albumList = albums;
        this.onAlbumClick = (OnAlbumClick) context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_row, viewGroup, false);
        return new AlbumAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        customViewHolder.tvAlbum.setText(albumList.get(i));

    }

    @Override
    public int getItemCount() {
        return (albumList == null) ? 0 : albumList.size();
    }


    public interface OnAlbumClick{
        void onAlbumClick(int pos);

    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvAlbum;

        CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            tvAlbum = view.findViewById(R.id.album_tv);
        }
        @Override
        public void onClick(View view) {
            try {
                onAlbumClick.onAlbumClick(getAdapterPosition());
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

