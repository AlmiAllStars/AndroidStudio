package com.almi.juegaalmiapp.modelo;

public class Console {
    private int id;
    private String name;
    private String description;
    private String picture;
    private String brand;

    public Console(int id, String name, String description, String picture, String brand) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.brand = brand;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getBrand() {
        return brand;
    }

    // Setters (opcional)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Console{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
