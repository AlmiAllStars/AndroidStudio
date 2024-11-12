package com.almi.juegaalmiapp.modelo;

public class Reparation {
    private String name;
    private String status;
    private int imageResId;

    public Reparation(String name, String status, int imageResId) {
        this.name = name;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getImageResId() {
        return imageResId;
    }
}
