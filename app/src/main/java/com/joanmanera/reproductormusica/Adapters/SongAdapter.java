package com.joanmanera.reproductormusica.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanmanera.reproductormusica.Activities.MainActivity;
import com.joanmanera.reproductormusica.Models.Song;
import com.joanmanera.reproductormusica.R;

import java.io.FileDescriptor;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements View.OnClickListener {

    private ArrayList<Song> songs;
    private View.OnClickListener listener;
    private Context context;

    public SongAdapter(ArrayList<Song> songs, Context context){
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        itemView.setOnClickListener(this);
        SongViewHolder viewHolder = new SongViewHolder(itemView, context);
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

        private ImageView ivImage;
        private TextView tvTitle;
        private TextView tvArtist;
        private TextView tvDuration;
        private Context context;

        public SongViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            ivImage = itemView.findViewById(R.id.ivImageItemSong);
            ivImage.getLayoutParams().width = 300;
            ivImage.getLayoutParams().height = 300;
            tvTitle = itemView.findViewById(R.id.song_title);
            tvArtist = itemView.findViewById(R.id.song_artist);
            tvDuration = itemView.findViewById(R.id.song_duration);
        }

        public void bindSong(Song song){
            ivImage.setImageBitmap(getAlbumart(song.getImage()));
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            tvDuration.setText(song.getDuration());
        }

        private Bitmap getAlbumart(long imageColumn) {
            Bitmap bm = null;
            try
            {
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri uri = ContentUris.withAppendedId(sArtworkUri, imageColumn);

                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");

                if (pfd != null)
                {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } catch (Exception e) {
            }
            return bm;
        }
    }

    public void setSongs(ArrayList<Song> songs){
        this.songs = songs;
        notifyDataSetChanged();
    }


}
