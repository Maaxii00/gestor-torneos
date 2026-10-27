package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.EquipoResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.repository.EquipoRepository;
import com.maxi.gestortorneos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    public EquipoService(EquipoRepository equipoRepository, UsuarioRepository usuarioRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EquipoResponse crear(String nombre, String tag, Long capitanId) {
        Usuario capitan = usuarioRepository.findById(capitanId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con id " + capitanId));

        if (equipoRepository.existsByNombre(nombre)) {
            throw new ConflictoException("Ya existe un equipo con el nombre " + nombre);
        }
        if (equipoRepository.existsByTag(tag)) {
            throw new ConflictoException("Ya existe un equipo con el tag " + tag);
        }

        Equipo equipo = equipoRepository.save(new Equipo(nombre, tag, capitan));
        return EquipoResponse.desde(equipo);
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAll().stream()
                .map(EquipoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipoResponse buscarPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un equipo con id " + id));
        return EquipoResponse.desde(equipo);
    }

    @Transactional(readOnly = true)
    public EquipoResponse buscarPorTag(String tag) {
        Equipo equipo = equipoRepository.findByTag(tag)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un equipo con el tag " + tag));
        return EquipoResponse.desde(equipo);
    }
}