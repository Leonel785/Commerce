package com.ecommerce.dto;

public class CarritoItemRequest {
    private Long productoId;
    private Integer cantidad;

    public CarritoItemRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
