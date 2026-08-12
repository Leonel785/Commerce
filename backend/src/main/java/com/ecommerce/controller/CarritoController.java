package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CarritoItemRequest;
import com.ecommerce.model.Carrito;
import com.ecommerce.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    private static final String CLIENTE_ID_SESSION = "clienteId";

    @GetMapping
    public ResponseEntity<ApiResponse> obtenerCarrito(HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        Carrito carrito = carritoService.obtenerCarritoPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.ok("Carrito obtenido", carrito));
    }

    @PostMapping("/agregar")
    public ResponseEntity<ApiResponse> agregar(@RequestBody CarritoItemRequest request, HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        try {
            Carrito carrito = carritoService.agregarProducto(clienteId, request);
            return ResponseEntity.ok(ApiResponse.ok("Producto agregado al carrito", carrito));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long productoId,
                                                     @RequestParam Integer cantidad,
                                                     HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        try {
            Carrito carrito = carritoService.actualizarCantidad(clienteId, productoId, cantidad);
            return ResponseEntity.ok(ApiResponse.ok("Cantidad actualizada", carrito));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long productoId, HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        try {
            Carrito carrito = carritoService.eliminarProducto(clienteId, productoId);
            return ResponseEntity.ok(ApiResponse.ok("Producto eliminado del carrito", carrito));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<ApiResponse> vaciar(HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.ok(ApiResponse.ok("Carrito vaciado"));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse> calcularTotal(HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Total calculado",
                carritoService.calcularTotal(clienteId)));
    }

    private Long obtenerClienteId(HttpSession session) {
        return (Long) session.getAttribute(CLIENTE_ID_SESSION);
    }
}
