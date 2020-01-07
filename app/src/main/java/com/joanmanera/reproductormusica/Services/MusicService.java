package com.joanmanera.reproductormusica.Services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.joanmanera.reproductormusica.Interfaces.IChangeSongListener;
import com.joanmanera.reproductormusica.Models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosn;

    private boolean shuffle = false, repeatList = false, repeatOne = false;
    private Random rand;

    private IChangeSongListener listener;

    private final IBinder musicBind = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicia la posición de la canción a 0.
        songPosn=0;
        player = new MediaPlayer();

        rand=new Random();

        initMusicPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void playSong(){
        // Resetea el player.
        player.reset();

        // Crea una variable con la canción a escuchar.
        Song playSong = songs.get(songPosn);

        // Crea la Uri de la id de la canción.
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        // Intenta cargar la canción. Si salta una excepción IOException crea un mensaje de error y no carga la canción.
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(IOException e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        //Prepara la tarea del player.
        player.prepareAsync();

        // Utiliza el método del listener para notificar que la canción
        listener.onChangeSong();
    }


    // Este método se ejecutara cuando una canción se termine de reproducir.
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeatOne){
            // Si se ha pulsado el botón de repetir canción, resetea el MediaPlayer y ejecuta el método playSong() (no se ha cambiado la posición de la canción).
            mp.reset();
            playSong();

        } else {
            // Si la posicion es mayor que 0
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void setShuffle(){
        if(shuffle){
            shuffle=false;
        } else {
            shuffle=true;
        }
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public int getSongPosn() {
        return songPosn;
    }

    public void playPrev(){
        songPosn--;
        if(songPosn < 0) {
            songPosn=songs.size()-1;
        }
        playSong();
    }

    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        } else{
            songPosn++;
            if(songPosn >= songs.size()){
                songPosn=0;
            }
        }
        playSong();
    }

    public void repeatSong(boolean bool){
        repeatList = bool;
    }

    public void repeatOne(boolean bool){
        repeatOne = bool;
    }

    public void setChangeSongListener(IChangeSongListener listener) {
        this.listener = listener;
    }

}
