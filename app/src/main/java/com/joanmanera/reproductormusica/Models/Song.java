package com.joanmanera.reproductormusica.Models;


import java.io.Serializable;

public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String duration;
    private long durationLong;
    private String nameList;

    public Song(long id, String title, String artist, String duration, long durationLong) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.durationLong = durationLong;
    }

    public Song(long id, String title, String artist, String duration, long durationLong, String nameList) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.durationLong = durationLong;
        this.nameList = nameList;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public long getDurationLong() {
        return durationLong;
    }

    public String getNameList() {
        return nameList;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDurationLong(long durationLong) {
        this.durationLong = durationLong;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }
}
