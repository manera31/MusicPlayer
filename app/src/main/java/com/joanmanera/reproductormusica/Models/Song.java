package com.joanmanera.reproductormusica.Models;

import android.net.Uri;

import java.io.Serializable;
import java.net.URI;

public class Song implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String duration;
    private long durationLong;
    private Uri portada;

    public Song(long id, String title, String artist, String duration, long durationLong) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.durationLong = durationLong;
        this.portada = portada;
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

    public Uri getPortada() {
        return portada;
    }
}
