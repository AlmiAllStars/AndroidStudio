package com.almi.juegaalmiapp.modelo;
import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private Sale sale;
    private List<Operation> operations;

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}

