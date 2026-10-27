package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.TorneoResponse;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.FormatoTorneo;
import com.maxi.gestortorneos.model.Torneo;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.repository.TorneoRepository;
import com.maxi.gestortorneos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final UsuarioRepository usuarioRepository;

    public TorneoService(TorneoRepository torneoRepository, UsuarioRepository usuarioRepository) {
        this.torneoRepository = torneoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TorneoResponse crear(String nombre, FormatoTorneo formato, Long organizadorId) {
        Usuario organizador = usuarioRepository.findById(organizadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un organizador con id " + organizadorId));
        Torneo torneo = torneoRepository.save(new Torneo(nombre, formato, organizador));
        return TorneoResponse.desde(torneo);
    }

    @Transactional(readOnly = true)
    public List<TorneoResponse> listar() {
        return torneoRepository.findAll().stream()
                .map(TorneoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public TorneoResponse buscarPorId(Long id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un torneo con id " + id));
        return TorneoResponse.desde(torneo);
    }

}