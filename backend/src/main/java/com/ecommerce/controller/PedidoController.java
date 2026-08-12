package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Pedido;
import com.ecommerce.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    private static final String CLIENTE_ID_SESSION = "clienteId";

    @PostMapping
    public ResponseEntity<ApiResponse> crear(HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        try {
            Pedido pedido = pedidoService.crearPedido(clienteId);
            return ResponseEntity.status(201).body(ApiResponse.ok("Pedido registrado exitosamente", pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<ApiResponse> misPedidos(HttpSession session) {
        Long clienteId = obtenerClienteId(session);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("No ha iniciado sesion"));
        }
        List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.ok("Pedidos obtenidos", pedidos));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok("Todos los pedidos", pedidoService.listarTodos()));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse> actualizarEstado(@PathVariable Long id,
                                                          @RequestParam String estado) {
        try {
            Pedido.Estado nuevoEstado = Pedido.Estado.valueOf(estado.toUpperCase());
            Pedido actualizado = pedidoService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", actualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Estado invalido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    private Long obtenerClienteId(HttpSession session) {
        return (Long) session.getAttribute(CLIENTE_ID_SESSION);
    }
}
