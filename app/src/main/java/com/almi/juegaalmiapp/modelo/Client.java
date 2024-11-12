package com.almi.juegaalmiapp.modelo;

public class Client {
    private String email;
    private String password;
    private int id;
    private String name;
    private String surname;
    private String phone;
    private String registration_date;
    private String picture;
    private int postal_code;
    private String chart; // JSON string del carrito
    private String wishlist; // JSON string de la wishlist
    private String address;

    private float latitude;
    private float longitude;

    // Constructor
    public Client(String name, String surname, String email,String phone) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.picture = picture;
        this.postal_code = postal_code;
        this.chart = chart;
        this.wishlist = wishlist;
    }
    public Client() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRegistration_date() { return registration_date; }
    public void setRegistration_date(String registration_date) { this.registration_date = registration_date; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public int getPostal_code() { return postal_code; }
    public void setPostal_code(int postal_code) { this.postal_code = postal_code; }

    public String getChart() { return chart; }
    public void setChart(String chart) { this.chart = chart; }

    public String getWishlist() { return wishlist; }
    public void setWishlist(String wishlist) { this.wishlist = wishlist; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public float getLatitude() { return latitude; }
    public void setLatitude(float latitude) { this.latitude = latitude; }

    public float getLongitude() { return longitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }
}
