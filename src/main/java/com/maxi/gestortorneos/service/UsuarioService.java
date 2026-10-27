package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.model.Rol;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario registrar(String email, String password, String nombre, Rol rol) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictoException("Ya existe un usuario con el email " + email);
        }
        Usuario usuario = new Usuario(email, password, nombre, rol);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}