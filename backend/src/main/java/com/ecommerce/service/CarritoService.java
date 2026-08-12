package com.ecommerce.service;

import com.ecommerce.dto.CarritoItemRequest;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Carrito obtenerCarritoPorCliente(Long clienteId) {
        return carritoRepository.findByClienteIdWithDetalles(clienteId)
                .orElseGet(() -> {
                    Cliente cliente = clienteRepository.findById(clienteId)
                            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                    Carrito nuevoCarrito = new Carrito(cliente);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    public Carrito agregarProducto(Long clienteId, CarritoItemRequest request) {
        Carrito carrito = obtenerCarritoPorCliente(clienteId);
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        Optional<DetalleCarrito> existente = detalleCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId());

        if (existente.isPresent()) {
            DetalleCarrito detalle = existente.get();
            int nuevaCantidad = detalle.getCantidad() + request.getCantidad();
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }
            detalle.setCantidad(nuevaCantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.calcularSubtotal();
            detalleCarritoRepository.save(detalle);
        } else {
            DetalleCarrito nuevoDetalle = new DetalleCarrito(carrito, producto, request.getCantidad());
            carrito.addDetalle(nuevoDetalle);
            detalleCarritoRepository.save(nuevoDetalle);
        }

        carrito.calcularTotal();
        return carritoRepository.save(carrito);
    }

    public Carrito actualizarCantidad(Long clienteId, Long productoId, Integer cantidad) {
        Carrito carrito = obtenerCarritoPorCliente(clienteId);
        DetalleCarrito detalle = detalleCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        Producto producto = detalle.getProducto();
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        if (cantidad <= 0) {
            carrito.removeDetalle(detalle);
            detalleCarritoRepository.delete(detalle);
        } else {
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.calcularSubtotal();
            detalleCarritoRepository.save(detalle);
        }

        carrito.calcularTotal();
        return carritoRepository.save(carrito);
    }

    public Carrito eliminarProducto(Long clienteId, Long productoId) {
        Carrito carrito = obtenerCarritoPorCliente(clienteId);
        DetalleCarrito detalle = detalleCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        carrito.removeDetalle(detalle);
        detalleCarritoRepository.delete(detalle);
        return carritoRepository.save(carrito);
    }

    public void vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoPorCliente(clienteId);
        carrito.getDetalles().clear();
        carrito.setTotal(BigDecimal.ZERO);
        carritoRepository.save(carrito);
    }

    public BigDecimal calcularTotal(Long clienteId) {
        Carrito carrito = obtenerCarritoPorCliente(clienteId);
        return carrito.getTotal();
    }
}
