package com.almi.juegaalmiapp.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("picture")
    private String picture;

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("stock")
    private int stock;

    @SerializedName("productType")
    private String productType;

    // Campos opcionales
    @SerializedName("model")
    private String model;

    @SerializedName("brand")
    private String brand;

    @SerializedName("generation")
    private Integer generation;

    @SerializedName("disk")
    private Integer disk;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("pegi")
    private Integer pegi;

    @SerializedName("genres")
    private List<String> genres;

    @SerializedName("type")
    private String type;

    @SerializedName("processor")
    private String processor;

    @SerializedName("memory")
    private Integer memory;

    @SerializedName("screen")
    private String screen;

    @SerializedName("camera")
    private String camera;

    @SerializedName("battery")
    private Integer battery;

    // Getters y Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getPicture() { return picture; }
    public boolean isEnabled() { return enabled; }
    public int getStock() { return stock; }
    public String getProductType() { return productType; }

    public String getModel() { return model; }
    public String getBrand() { return brand; }
    public Integer getGeneration() { return generation; }
    public Integer getDisk() { return disk; }

    public String getReleaseDate() { return releaseDate; }
    public Integer getPegi() { return pegi; }
    public List<String> getGenres() { return genres; }

    public String getType() { return type; }
    public String getProcessor() { return processor; }
    public Integer getMemory() { return memory; }
    public String getScreen() { return screen; }
    public String getCamera() { return camera; }
    public Integer getBattery() { return battery; }
}
