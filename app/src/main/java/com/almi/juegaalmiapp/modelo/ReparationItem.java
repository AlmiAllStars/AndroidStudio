package com.almi.juegaalmiapp.modelo;

public class ReparationItem {
    private String title;
    private int imageResId;

    public ReparationItem(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
