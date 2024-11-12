package com.almi.juegaalmiapp.modelo;

public class CarritoItem {
    private int id; // ID del producto
    private String name; // Nombre del producto
    private String description; // Descripción del producto
    private double price; // Precio del producto
    private String picture; // Imagen del producto

    private int cantidad; // Cantidad de ítems en el carrito
    private String tipo; // 'videojuego', 'consola' o 'dispositivo'
    private String operationType; // 'order' o 'rent'

    // Constructor
    public CarritoItem(int id, String name, String description, double price, String picture,
                       int cantidad, String tipo, String operationType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.picture = picture;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.operationType = operationType;
    }

    public CarritoItem() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", picture='" + picture + '\'' +
                ", cantidad=" + cantidad +
                ", tipo='" + tipo + '\'' +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}
