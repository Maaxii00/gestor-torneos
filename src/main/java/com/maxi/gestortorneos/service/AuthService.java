package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.LoginResponse;
import com.maxi.gestortorneos.exception.CredencialesInvalidasException;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.repository.UsuarioRepository;
import com.maxi.gestortorneos.security.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        return LoginResponse.de(jwtService.generarToken(usuario), usuario);
    }
}
