package com.example.papertrading;

import androidx.annotation.NonNull;

public class Favourites {

    private String name;

    public Favourites(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Favourite stock {'name': " + name + "}";
    }
}
