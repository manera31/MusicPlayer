package com.joanmanera.reproductormusica.Models;


import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.net.URI;

public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String duration;
    private long durationLong;
    private long image;

    public Song(long id, String title, String artist, String duration, long durationLong, long image) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.durationLong = durationLong;
        this.image = image;
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

    public long getImage() {
        return image;
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

    public void setImage(long image) {
        this.image = image;
    }
}
