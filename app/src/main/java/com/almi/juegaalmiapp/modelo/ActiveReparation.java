package com.almi.juegaalmiapp.modelo;

public class ActiveReparation {
    private String description;
    private String status;

    public ActiveReparation(String description, String status) {
        this.description = description;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
