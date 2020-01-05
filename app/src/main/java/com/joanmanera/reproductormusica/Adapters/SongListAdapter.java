package com.joanmanera.reproductormusica.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.joanmanera.reproductormusica.Models.SongList;
import com.joanmanera.reproductormusica.R;

import java.util.ArrayList;

public class SongListAdapter extends ArrayAdapter<String> {

    public SongListAdapter(@NonNull Context context, ArrayList<String> songList) {
        super(context, 0, songList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song_list, parent, false);
        }

        TextView tvNameSongList = convertView.findViewById(R.id.tvNameSongList);


        String songList = getItem(position);

        if (songList != null){
            if (position == 0) {
                tvNameSongList.setTypeface(null, Typeface.BOLD);
                tvNameSongList.setGravity(Gravity.CENTER);
                tvNameSongList.setText(songList);
            } else {
                tvNameSongList.setText("- " + songList);
            }
        }


        return convertView;
    }


}
