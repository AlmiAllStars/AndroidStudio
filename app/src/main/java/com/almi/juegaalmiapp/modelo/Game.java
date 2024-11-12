package com.almi.juegaalmiapp.modelo;

import java.util.Arrays;

public class Game {
    private int id;
    private String name;
    private String description;
    private double price;
    private String[] genres;
    private String releaseDate;
    private int pegi;
    private String picture;
    private String platform;
    private int quantity;

    // Constructor con todos los campos necesarios
    public Game(int id, String name, String description, double price, String[] genres, String releaseDate, int pegi, String picture, String platform, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.pegi = pegi;
        this.picture = picture;
        this.platform = platform;
        this.quantity = quantity;
    }

    // Getters y setters para todos los campos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        this.pegi = pegi;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", genres=" + Arrays.toString(genres) +
                ", releaseDate='" + releaseDate + '\'' +
                ", pegi=" + pegi +
                ", picture='" + picture + '\'' +
                ", platform='" + platform + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
