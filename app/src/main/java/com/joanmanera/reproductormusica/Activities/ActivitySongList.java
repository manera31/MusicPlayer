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

    // Activity para cargar el fragment de la lista de canciones.
    // Implementa la interfaz IPickSongListener para controlar cuando se seleccione una canción.
public class ActivitySongList extends AppCompatActivity implements IPickSongListener {

    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_song_list);

        // Comprobación de version para ocultar las notificaciones. También se requierre modificar el Manifest.
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Carga el fragment.
        FragmentSongList detalle = (FragmentSongList)getSupportFragmentManager().findFragmentById(R.id.FrgSongList);
        detalle.setSongListener(this);
        songs = (ArrayList<Song>) getIntent().getSerializableExtra("A");
        detalle.mostrarLista(songs);
    }


    // Al hacer click sobre un item se llamara a este método.
    @Override
    public void onSongSelected(int posicion) {
        Intent returnIntent = new Intent();

        // Carga el resultado.
        returnIntent.putExtra("stringResult", songs.get(posicion).getTitle());

        // Llama a onActivityResult y le envia el resultado.
        setResult(Activity.RESULT_OK, returnIntent);

        // Se cierra y finaliza la activity.
        finish();
    }
}
