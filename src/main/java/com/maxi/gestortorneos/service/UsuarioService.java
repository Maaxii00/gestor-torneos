package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.model.Rol;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(String email, String password, String nombre) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictoException("Ya existe un usuario con el email " + email);
        }
        String hash = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(email, hash, nombre, Rol.USUARIO);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}