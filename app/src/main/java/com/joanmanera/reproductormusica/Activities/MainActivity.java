package com.joanmanera.reproductormusica.Activities;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joanmanera.reproductormusica.Interfaces.IChangeSongListener;
import com.joanmanera.reproductormusica.Models.Song;
import com.joanmanera.reproductormusica.Services.MusicService;
import com.joanmanera.reproductormusica.R;

public class MainActivity extends Activity implements View.OnClickListener, IChangeSongListener {

    private ArrayList<Song> songList, queueList;

    private MusicService musicSrv;
    private Intent playIntent;

    private boolean firstPlaySong = true;

    private ImageButton ibShuffle, ibPrevious, ibPlayPause, ibNext, ibRepeatRepeatOne, ibAddList, ibList, ibQueueList;
    private SeekBar sbProgreso;
    private boolean isShuffle = false, isRepeatOne = false, isPaused = true;

    private ScheduledExecutorService schedulerSeekBar, schedulerTimeSong;
    private TextView tvNombreCancion, tvTiempoRestante, tvTiempoActual;
    private ImageView ivImage;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobación de version para ocultar las notificaciones. También se requierre modificar el Manifest.
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Solicitar permisos.
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(perm != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 255);
        }

        iniciarBotones();

        songList = new ArrayList<>();
        queueList = new ArrayList<>();

        getSongList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        // Al destruir la aplicacion paramos el servicio.
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    // Este método controla que cuando se cambie la orientación no se desajuste la interfaz.
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void iniciarBotones(){
        ibShuffle = findViewById(R.id.ibShuffle);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibPlayPause = findViewById(R.id.ibPlayPause);
        ibNext = findViewById(R.id.ibNext);
        ibRepeatRepeatOne = findViewById(R.id.ibRepeatRepeatOne);
        ibAddList = findViewById(R.id.ibAddList);
        ibList = findViewById(R.id.ibList);
        ibQueueList = findViewById(R.id.ibQueueList);
        tvNombreCancion = findViewById(R.id.tvNombreCancion);
        tvTiempoRestante = findViewById(R.id.tvTiempoRestante);
        tvTiempoActual = findViewById(R.id.tvTiempoEscuchado);
        sbProgreso = findViewById(R.id.sbProgreso);
        ivImage = findViewById(R.id.ivImage);

        ibShuffle.setOnClickListener(this);
        ibPrevious.setOnClickListener(this);
        ibPlayPause.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibRepeatRepeatOne.setOnClickListener(this);
        ibAddList.setOnClickListener(this);
        ibList.setOnClickListener(this);
        ibQueueList.setOnClickListener(this);

        // Creación de un OnSeekBarChangeListener.
        sbProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // Se ejecutará cuando se haya modificado la seek bar.
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // Si es un cambio realizado por el usuario modifica la posición de la canción que se este reproduciendo por la de la seek bar.
                if (fromUser)
                    musicSrv.seek(progress);
            }

            // Se ejecutará cuando se empieze a modificar la seek bar.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // Pausa la reproducción.
                musicSrv.pausePlayer();
            }

            // Se ejecutará cuando se pare de modificar la seek bar.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Continua la reproducción.
                musicSrv.go();
            }
        });

        ibShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
        ibPrevious.setBackgroundResource(R.drawable.ic_skip_previous_black_24dp);
        ibPlayPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
        ibNext.setBackgroundResource(R.drawable.ic_skip_next_black_24dp);
        ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
        ibList.setBackgroundResource(R.drawable.baseline_format_list_bulleted_24);
        ibQueueList.setBackgroundResource(R.drawable.ic_playlist_play_black_24dp);

    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            musicSrv.setChangeSongListener(MainActivity.this);
            //pass list
            musicSrv.setList(songList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    // TODO Crear método para cargar canciones desde la carpeta raw.
    // Este método carga todas las canciones que encuentre en el dispositivo.
    public void getSongList(){

        ContentResolver musicResolver = getContentResolver();

        // Busca todos los archivos de audio del dispositivo.
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            // Coge las columnas
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int imageColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            // Añade las canciones al array.
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisDuration = milisegundosASegundosString((int)musicCursor.getLong(durationColumn)) ;
                long thisDurationLong = musicCursor.getLong(durationColumn);
                long thisImage = musicCursor.getLong(imageColumn);


                songList.add(new Song(thisId, thisTitle, thisArtist, thisDuration, thisDurationLong, thisImage));
            }

            while (musicCursor.moveToNext());
        }
        if(musicCursor != null){
            musicCursor.close();
        }

    }

    private void playNext(){
        if(isPaused){
            isPaused=false;
        }
        musicSrv.playNext();
    }

    private void playPrev(){
        if(isPaused){
            isPaused=false;
        }
        musicSrv.playPrev();
    }

    @Override
    public void onClick(View v) {
        if(songList.size() > 0) {
            switch (v.getId()) {
                case R.id.ibPrevious:
                    playPrev();
                    setSong();
                    break;

                case R.id.ibNext:
                    playNext();
                    setSong();
                    break;

                case R.id.ibPlayPause:
                    if (isPaused) {
                        //Si estaba pausada la reproducción.

                        ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);

                        // Si es la primera vez que se reproduce una cancion llama a playSong o sino a go.
                        if (firstPlaySong) {
                            musicSrv.playSong();
                            firstPlaySong = false;

                        } else {
                            musicSrv.go();

                        }

                        // Modifica la interfaz.
                        setSong();
                        isPaused = false;

                    } else {
                        // Si la reproducción no estaba pausada.

                        ibPlayPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);

                        // Apaga los executors.
                        if (!schedulerSeekBar.isShutdown()) {
                            schedulerSeekBar.shutdown();
                        }
                        if (schedulerTimeSong != null) {
                            schedulerTimeSong.shutdown();
                        }

                        // Pausa la reproducción.
                        musicSrv.pausePlayer();

                        // Modifica el progreso de la seek bar.
                        sbProgreso.setProgress(musicSrv.getPosn());
                        isPaused = true;
                    }

                    break;

                case R.id.ibRepeatRepeatOne:
                    if (isRepeatOne) {
                        ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                        musicSrv.repeatOne(false);
                        isRepeatOne = false;
                    } else {
                        ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
                        musicSrv.repeatOne(true);
                        isRepeatOne = true;
                    }
                    break;

                case R.id.ibShuffle:
                    if (isShuffle) {
                        ibShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                        isShuffle = false;
                    } else {
                        ibShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp_green);
                        isShuffle = true;
                    }

                    // Cambia el estado shuffle del reproductor.
                    musicSrv.setShuffle();
                    break;

                case R.id.ibAddList:
                    boolean isInList = false;
                    int posicion = musicSrv.getSongPosn();
                    Song s = songList.get(posicion);

                    // Busca si la canción está en la lista.
                    for (Song song : queueList) {
                        if (s.equals(song)) {
                            isInList = true;
                        }
                    }

                    if (!isInList) {

                        // Si no está en la lista la añade.
                        queueList.add(s);
                        ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_check_black_24dp);
                    } else {

                        // Si está en la lista la elimina.
                        queueList.remove(s);
                        ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_black_24dp);
                    }

                    break;

                case R.id.ibList:

                    // Crea un intent que le pasa todas las canciones y espera el resultado.
                    Intent intentSongList = new Intent(this, ActivitySongList.class);
                    intentSongList.putExtra("A", songList);
                    startActivityForResult(intentSongList, 0);
                    break;

                case R.id.ibQueueList:

                    // Crea un intent que le pasa todas las canciones y espera el resultado.
                    Intent intentQueueList = new Intent(this, ActivitySongList.class);
                    intentQueueList.putExtra("A", queueList);
                    startActivityForResult(intentQueueList, 0);
                    break;
            }
        } else {
            Toast.makeText(this, "No se han encontrado canciones en el dispositivo.", Toast.LENGTH_LONG).show();
        }
    }

    // Recibe el resultado de los intents anteriores.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si el resultado es correcto.
        if(resultCode == Activity.RESULT_OK){
            String title = data.getStringExtra("stringResult");
            ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
            isPaused = false;
            Song s;

            // Busca la canción entre la lista de canciones.
            for (int i = 0 ; i < songList.size() ; i++){
                s = songList.get(i);
                if(s.getTitle().contains(title)){
                    musicSrv.setSong(i);
                }
            }

            // Inicia el reproductor.
            musicSrv.playSong();
            setSong();
        }
    }

    // Método para modificar la interfaz según la canción que se vaya a reproducir.
    private void setSong(){
        // Si existen los executors los apaga.
        if(schedulerSeekBar != null){
            schedulerSeekBar.shutdown();
        }
        if(schedulerTimeSong != null){
            schedulerTimeSong.shutdown();
        }

        ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
        isPaused = false;

        // Guarda la posición de la canción.
        int posicion = musicSrv.getSongPosn();

        boolean isSongInList = false;

        ivImage.setImageBitmap(getAlbumart(songList.get(posicion).getImage()));
        tvNombreCancion.setText(songList.get(posicion).getTitle());
        tvTiempoRestante.setText(songList.get(posicion).getDuration());

        // Busca si la canción esta en una lista.
        for (Song s: queueList){
            if(songList.get(posicion).equals(s)){
                ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_check_black_24dp);
                isSongInList = true;
            }
        }
        if(!isSongInList){
            ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_black_24dp);
        }

        // Modifica el progreso y el máximo de la seek bar dependiendo de la canción.
        sbProgreso.setProgress(musicSrv.getPosn());
        sbProgreso.setMax((int) songList.get(posicion).getDurationLong());

        // Crea los nuevos executors para controlar que avance la seek bar y los segundos en tiempo real de la canción.
        schedulerSeekBar = Executors.newSingleThreadScheduledExecutor();
        schedulerTimeSong = Executors.newSingleThreadScheduledExecutor();
        schedulerSeekBar.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sbProgreso.setProgress(sbProgreso.getProgress()+200);
            }
        }, 500, 200, TimeUnit.MILLISECONDS);
        schedulerTimeSong.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                tvTiempoActual.setText(milisegundosASegundosString(musicSrv.getPosn()));
            }
        }, 500, 1000, TimeUnit.MILLISECONDS);
    }

    // StackOverflow
    private Bitmap getAlbumart(long imageColumn) {
        Bitmap bm = null;
        try
        {
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, imageColumn);

            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    // Convierte milisegundos a un string con formato mm:ss
    private String milisegundosASegundosString(int milisegundos){
        String duration;
        try {
            Long time = Long.valueOf(milisegundos);
            long seconds = time/1000;
            long minutes = seconds/60;
            seconds = seconds % 60;


            if(seconds<10) {
                duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            } else {
                duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }

        } catch(NumberFormatException e) {
            duration = "0000";
        }
        return duration;
    }

    @Override
    public void onChangeSong() {
        // Cuando se cambie una canción porque se ha terminado, se ejecutara setSong.
        setSong();
    }
}
