package com.joanmanera.reproductormusica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements View.OnClickListener {

    private ArrayList<Song> songs;
    private View.OnClickListener listener;

    public SongAdapter(ArrayList<Song> songs){
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        itemView.setOnClickListener(this);
        SongViewHolder viewHolder = new SongViewHolder(itemView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bindSong(song);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) {
            listener.onClick(view);
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private TextView tvArtist;
        private TextView tvDuration;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.song_title);
            tvArtist = itemView.findViewById(R.id.song_artist);
            tvDuration = itemView.findViewById(R.id.song_duration);
        }

        public void bindSong(Song song){
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            tvDuration.setText(song.getDuration());
        }
    }

    public void setSongs(ArrayList<Song> songs){
        this.songs = songs;
        notifyDataSetChanged();
    }
}
