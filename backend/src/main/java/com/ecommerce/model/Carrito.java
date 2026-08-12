package com.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, unique = true)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleCarrito> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        if (total == null) total = BigDecimal.ZERO;
    }

    public void calcularTotal() {
        this.total = detalles.stream()
                .map(DetalleCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Constructores
    public Carrito() {}

    public Carrito(Cliente cliente) {
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<DetalleCarrito> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCarrito> detalles) { this.detalles = detalles; }

    public void addDetalle(DetalleCarrito detalle) {
        detalles.add(detalle);
        detalle.setCarrito(this);
        calcularTotal();
    }

    public void removeDetalle(DetalleCarrito detalle) {
        detalles.remove(detalle);
        detalle.setCarrito(null);
        calcularTotal();
    }
}
