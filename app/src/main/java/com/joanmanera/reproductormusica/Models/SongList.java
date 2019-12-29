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

    public String getListName() {
        return listName;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}
