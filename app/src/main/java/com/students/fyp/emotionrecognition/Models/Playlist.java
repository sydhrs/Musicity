package com.students.fyp.emotionrecognition.Models;

public class Playlist {
    private String name;
    private String key;

    public Playlist() {
    }

    public Playlist(String name) {
        this.name = name;
        this.key = key;
    }

    public Playlist(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
