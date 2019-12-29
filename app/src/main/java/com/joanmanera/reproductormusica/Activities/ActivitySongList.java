package com.joanmanera.reproductormusica.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.joanmanera.reproductormusica.Fragments.FragmentSongList;
import com.joanmanera.reproductormusica.Interfaces.IPickSongListener;
import com.joanmanera.reproductormusica.Models.Song;
import com.joanmanera.reproductormusica.R;

import java.util.ArrayList;

public class ActivitySongList extends AppCompatActivity implements IPickSongListener {

    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_song_list);

        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        FragmentSongList detalle = (FragmentSongList)getSupportFragmentManager().findFragmentById(R.id.FrgSongList);
        detalle.setSongListener(this);
        songs = (ArrayList<Song>) getIntent().getSerializableExtra("A");
        detalle.mostrarLista(songs);
    }

    @Override
    public void onSongSelected(int posicion) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("stringResult", songs.get(posicion).getTitle());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
