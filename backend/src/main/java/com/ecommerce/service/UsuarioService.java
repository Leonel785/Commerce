package com.ecommerce.service;

import com.ecommerce.model.Cliente;
import com.ecommerce.model.Usuario;
import com.ecommerce.repository.ClienteRepository;
import com.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Cliente> findClienteByUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId);
    }

    public Usuario registrarUsuario(Usuario usuario, Cliente cliente) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }
        if (clienteRepository.existsByCorreo(cliente.getCorreo())) {
            throw new RuntimeException("El correo ya esta registrado");
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        cliente.setUsuario(usuarioGuardado);
        clienteRepository.save(cliente);

        return usuarioGuardado;
    }
}
