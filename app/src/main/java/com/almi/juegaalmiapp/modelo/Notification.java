package com.almi.juegaalmiapp.modelo;

public class Notification {
    private String title;
    private int iconResId;

    public Notification(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
