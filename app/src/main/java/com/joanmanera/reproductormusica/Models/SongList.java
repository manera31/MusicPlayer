package com.joanmanera.reproductormusica.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class SongList implements Serializable {
    private String listName;
    private ArrayList<Song> songs;

    public SongList(String listName) {
        this.listName = listName;
        songs = new ArrayList<>();
    }

    public SongList(String listName, ArrayList<Song> songs) {
        this.listName = listName;
        this.songs = songs;
    }

    public boolean addSong(Song song){
        return songs.add(song);
    }

    public boolean removeSong (Song song){
        return songs.remove(song);
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
