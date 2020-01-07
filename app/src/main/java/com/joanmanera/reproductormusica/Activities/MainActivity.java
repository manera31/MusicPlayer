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
import android.widget.AdapterView;
import android.widget.ImageButton;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
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

    private boolean appPaused =false, playbackPaused=false;

    private ImageButton ibShuffle, ibPrevious, ibPlayPause, ibNext, ibRepeatRepeatOne, ibAddList, ibList, ibQueueList;
    private SeekBar sbProgreso;
    private boolean isShuffle = false, isRepeat = false, isRepeatOne = false, isPaused = true;

    private ScheduledExecutorService schedulerSeekBar, schedulerTimeSong;
    private TextView tvNombreCancion, tvTiempoRestante, tvTiempoActual;
    private ImageView ivImage;
    //private Spinner spinner;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(perm != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 255);
        }

        iniciarBotones();

        songList = new ArrayList<>();
        queueList = new ArrayList<>();

        getSongList();

        queueList.add(songList.get(1));
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
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        appPaused =true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(appPaused){
            appPaused =false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
        //spinner = findViewById(R.id.spinner);
        ivImage = findViewById(R.id.ivImage);

        ibShuffle.setOnClickListener(this);
        ibPrevious.setOnClickListener(this);
        ibPlayPause.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibRepeatRepeatOne.setOnClickListener(this);
        ibAddList.setOnClickListener(this);
        ibList.setOnClickListener(this);
        ibQueueList.setOnClickListener(this);
        sbProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                musicSrv.seek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicSrv.pausePlayer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
        //spinner.setBackgroundResource(R.drawable.ic_playlist_add_black_24dp);

        ArrayList<String> list = new ArrayList<>();
        list.add("NEW");
        list.add("lista 1");
        list.add("lista 2");
        list.add("lista 3");
        list.add("lista 4");

        /*SongListAdapter songListAdapter = new SongListAdapter(this, list);
        spinner.setAdapter(songListAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean first = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!first){
                    Toast.makeText(MainActivity.this, "asdasdasdasdadsas  " + position, Toast.LENGTH_LONG).show();
                    spinner.setBackgroundResource(R.drawable.ic_playlist_add_check_black_24dp);
                } else {
                    first = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
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

    public void getSongList(){

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int imageColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisDuration = musicCursor.getString(durationColumn);
                long thisDurationLong = musicCursor.getLong(durationColumn);
                long thisImage = musicCursor.getLong(imageColumn);

                String duration;
                if(String.valueOf(thisDuration) != null) {
                    try {
                        Long time = Long.valueOf(thisDuration);
                        long seconds = time/1000;
                        long minutes = seconds/60;
                        seconds = seconds % 60;


                        if(seconds<10) {
                            duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
                        } else {
                            duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
                        }
                    } catch(NumberFormatException e) {
                        duration = thisDuration;
                    }
                } else {
                    duration = "0";
                }




                songList.add(new Song(thisId, thisTitle, thisArtist, duration, thisDurationLong, thisImage));
            }

            while (musicCursor.moveToNext());
        }
        if(musicCursor != null){
            musicCursor.close();
        }

    }

    private void playNext(){
        if(playbackPaused){
            playbackPaused=false;
        }
        musicSrv.playNext();
    }

    private void playPrev(){
        if(playbackPaused){
            playbackPaused=false;
        }
        musicSrv.playPrev();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibPrevious:
                playPrev();
                setSong();
                break;

            case R.id.ibNext:
                playNext();
                setSong();
                break;

            case R.id.ibPlayPause:
                if(!isPaused){
                    ibPlayPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    playbackPaused=true;
                    if(!schedulerSeekBar.isShutdown()){
                        schedulerSeekBar.shutdown();
                    }
                    if(schedulerTimeSong != null){
                        schedulerTimeSong.shutdown();
                    }
                    musicSrv.pausePlayer();
                    sbProgreso.setProgress(musicSrv.getPosn());
                    isPaused = true;

                } else {
                    ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);

                    if(playbackPaused){
                        musicSrv.go();

                        sbProgreso.setProgress(musicSrv.getPosn());
                        setSong();
                    } else {
                        musicSrv.playSong();
                        setSong();

                        if (playbackPaused) {
                            playbackPaused = false;
                        }
                    }
                    isPaused = false;
                }
                break;

            case R.id.ibRepeatRepeatOne:
                if(isRepeat){
                    ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
                    musicSrv.repeatSong(false);
                    musicSrv.repeatOne(true);
                    isRepeat = false;
                    isRepeatOne = true;
                } else if (isRepeatOne) {
                    ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                    musicSrv.repeatSong(false);
                    musicSrv.repeatOne(false);
                    isRepeat = false;
                    isRepeatOne = false;
                } else {
                    ibRepeatRepeatOne.setBackgroundResource(R.drawable.ic_repeat_black_24dp_green);
                    musicSrv.repeatSong(true);
                    musicSrv.repeatOne(false);
                    isRepeat = true;
                    isRepeatOne = false;
                }
                break;

            case R.id.ibShuffle:
                if(isShuffle){
                    ibShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                    isShuffle = false;
                } else {
                    ibShuffle.setBackgroundResource(R.drawable.ic_shuffle_black_24dp_green);
                    isShuffle = true;
                }
                musicSrv.setShuffle();
                break;

            case R.id.ibAddList:
                boolean isInList = false;
                int posicion = musicSrv.getSongPosn();
                Song s = songList.get(posicion);

                for (Song song: queueList){
                    if(s.equals(song)){
                        isInList = true;
                    }
                }

                if(!isInList){
                    queueList.add(s);
                    ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_check_black_24dp);
                } else {
                    queueList.remove(s);
                    ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_black_24dp);
                }

                break;

            case R.id.ibList:
                Intent i = new Intent(this, ActivitySongList.class);
                i.putExtra("A", songList);
                startActivityForResult(i, 0);
                break;

            case R.id.ibQueueList:
                Intent intent = new Intent(this, ActivitySongList.class);
                intent.putExtra("A", queueList);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            String title = data.getStringExtra("stringResult");
            ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
            isPaused = false;
            Song s;
            for (int i = 0 ; i < songList.size() ; i++){
                s = songList.get(i);
                if(s.getTitle().contains(title)){
                    musicSrv.setSong(i);
                }
            }
            musicSrv.playSong();
            setSong();
            if (playbackPaused) {
                playbackPaused = false;
            }
        }
    }

    private void setSong(){
        if(schedulerSeekBar != null){
            schedulerSeekBar.shutdown();
        }
        if(schedulerTimeSong != null){
            schedulerTimeSong.shutdown();
        }

        ibPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
        isPaused = false;

        int posicion = musicSrv.getSongPosn();
        boolean isSongInList = false;

        ivImage.setImageBitmap(getAlbumart(songList.get(posicion).getImage()));
        tvNombreCancion.setText(songList.get(posicion).getTitle());
        tvTiempoRestante.setText(songList.get(posicion).getDuration());
        for (Song s: queueList){
            if(songList.get(posicion).equals(s)){
                ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_check_black_24dp);
                isSongInList = true;
            }
        }
        if(!isSongInList){
            ibAddList.setBackgroundResource(R.drawable.ic_playlist_add_black_24dp);
        }

        sbProgreso.setProgress(musicSrv.getPosn());
        sbProgreso.setMax((int) songList.get(posicion).getDurationLong());

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
        setSong();
    }
}
