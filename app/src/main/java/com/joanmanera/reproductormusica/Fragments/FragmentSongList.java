package com.joanmanera.reproductormusica.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanmanera.reproductormusica.Adapters.SongAdapter;
import com.joanmanera.reproductormusica.Interfaces.IPickSongListener;
import com.joanmanera.reproductormusica.Models.Song;
import com.joanmanera.reproductormusica.R;

import java.util.ArrayList;

// Fragment para mostrar la lista de canciones.
public class FragmentSongList extends Fragment {

    private ArrayList<Song> songs;
    private RecyclerView recyclerView;
    private EditText etSearch;
    private SongAdapter songAdapter;
    private IPickSongListener listener;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.activity_song_list, container, false);
        etSearch = layout.findViewById(R.id.etSearch);

        // Listener para controlar cuando se modifique el texto del EditText.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // Después de modificar el texto, se invoca a este método.
            @Override
            public void afterTextChanged(Editable s) {

                // Le pasamos como parámetro el texto que se acaba de modificar.
                filter(s.toString());
            }
        });

        recyclerView = layout.findViewById(R.id.rvSongs);
        songAdapter = new SongAdapter(songs, getActivity());

        // Al hacer click sobre un item se invoca este método.
        songAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Envia la posición de la canción a través del listener del la interfaz IPickSongListener.
                listener.onSongSelected(recyclerView.getChildAdapterPosition(v));
            }
        });
        recyclerView.setAdapter(songAdapter);

        // Linea visual para separar las canciones del recycler view.
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void mostrarLista(ArrayList<Song> songs) {
        this.songs = songs;
        songAdapter.setSongs(songs);
    }
    private void filter(String text){
        // Creacion de un ArrayList donde se guardarán las canciones fitlradas.
        ArrayList<Song> filtered = new ArrayList<>();

        for (Song s: songs){

            //Si el título de la canción contiene el texto que se le ha pasado como parámetro lo añade a la lista anteriormente creada.
            if(s.getTitle().toLowerCase().contains(text.toLowerCase())){
                filtered.add(s);
            }
        }

        // Modifica la lista del adaptador.
        songAdapter.setSongs(filtered);
    }

    public void setSongListener(IPickSongListener listener) {
        this.listener = listener;
    }
}
