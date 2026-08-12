package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Producto;
import com.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse> listar() {
        List<Producto> productos = productoService.listarTodos();
        return ResponseEntity.ok(ApiResponse.ok("Productos obtenidos exitosamente", productos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok("Producto encontrado", p)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("Producto no encontrado")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> crear(@RequestBody Producto producto) {
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.status(201).body(ApiResponse.ok("Producto creado exitosamente", guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto actualizado = productoService.actualizar(id, producto);
            return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.ok(ApiResponse.ok("Producto eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error al eliminar: " + e.getMessage()));
        }
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(ApiResponse.ok("Productos filtrados",
                productoService.buscarPorCategoria(categoria)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(ApiResponse.ok("Resultados de busqueda",
                productoService.buscarPorNombre(nombre)));
    }
}
