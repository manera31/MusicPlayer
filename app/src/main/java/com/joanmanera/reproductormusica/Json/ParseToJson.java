package com.joanmanera.reproductormusica.Json;

import com.google.gson.Gson;
import com.joanmanera.reproductormusica.Models.SongList;

import java.util.ArrayList;

public class ParseToJson {

    private Gson gson;
    private ArrayList<SongList> songLists;

    public ParseToJson(ArrayList<SongList> songLists) {
        gson = new Gson();
        this.songLists = songLists;
    }

    public boolean parse(){
        try {
            gson.toJson(songLists);
            return true;
        } catch (Exception e){
            return false;
        }

    }
}
