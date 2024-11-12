package com.almi.juegaalmiapp.modelo;

public class Operation {
    private int operation_id;
    private double charge;
    private ProductPedido product;

    public int getOperationId() {
        return operation_id;
    }

    public void setOperationId(int operation_id) {
        this.operation_id = operation_id;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public ProductPedido getProduct() {
        return product;
    }

    public void setProduct(ProductPedido product) {
        this.product = product;
    }
}
