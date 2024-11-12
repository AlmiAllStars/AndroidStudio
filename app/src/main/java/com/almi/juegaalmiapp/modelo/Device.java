package com.almi.juegaalmiapp.modelo;

public class Device {
    private int id;
    private String name;
    private String description;
    private String picture;
    private String type;

    public Device(int id, String name, String description, String picture, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.type = type;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPicture() { return picture; }
    public String getType() { return type; }
}
