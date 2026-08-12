package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.model.Cliente;
import com.ecommerce.model.Usuario;
import com.ecommerce.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Usuario> usuarioOpt = usuarioService.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Usuario o contrasena incorrectos"));
        }

        Usuario usuario = usuarioOpt.get();

        // Soporta tanto passwords en BCrypt como en texto plano (útil durante el desarrollo
        // inicial con los datos de prueba del schema.sql).
        boolean ok;
        try {
            ok = passwordEncoder.matches(request.getPassword(), usuario.getPassword());
        } catch (IllegalArgumentException ex) {
            // El hash guardado no es BCrypt (p.ej. datos del schema.sql: '123456' plano)
            ok = usuario.getPassword().equals(request.getPassword());
        }
        if (!ok && usuario.getPassword().equals(request.getPassword())) {
            ok = true;
        }

        if (!ok) {
            return ResponseEntity.status(401).body(ApiResponse.error("Usuario o contrasena incorrectos"));
        }

        Optional<Cliente> clienteOpt = usuarioService.findClienteByUsuarioId(usuario.getId());
        Long clienteId = clienteOpt.map(Cliente::getId).orElse(null);

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("clienteId", clienteId);
        session.setAttribute("rol", usuario.getRol().name());
        session.setAttribute("username", usuario.getUsername());

        Map<String, Object> datos = new HashMap<>();
        datos.put("username", usuario.getUsername());
        datos.put("rol", usuario.getRol().name());
        datos.put("clienteId", clienteId);
        datos.put("sessionId", session.getId());

        return ResponseEntity.ok(ApiResponse.ok("Sesion iniciada exitosamente", datos));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Sesion cerrada"));
    }

    @GetMapping("/sesion")
    public ResponseEntity<ApiResponse> verSesion(HttpSession session) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("usuarioId", session.getAttribute("usuarioId"));
        datos.put("clienteId", session.getAttribute("clienteId"));
        datos.put("rol", session.getAttribute("rol"));
        datos.put("username", session.getAttribute("username"));
        return ResponseEntity.ok(ApiResponse.ok("Datos de sesion", datos));
    }

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse> registro(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            String nombres = (String) payload.get("nombres");
            String correo = (String) payload.get("correo");
            String direccion = (String) payload.get("direccion");
            String telefono = (String) payload.get("telefono");

            // Hashear la password antes de guardar (BCrypt)
            String hash = new BCryptPasswordEncoder().encode(password);
            Usuario usuario = new Usuario(username, hash, Usuario.Rol.CLIENTE);
            Cliente cliente = new Cliente();
            cliente.setNombres(nombres);
            cliente.setCorreo(correo);
            cliente.setDireccion(direccion);
            cliente.setTelefono(telefono);

            Usuario guardado = usuarioService.registrarUsuario(usuario, cliente);
            return ResponseEntity.status(201).body(ApiResponse.ok("Usuario registrado", guardado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
