package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.*;
import com.maxi.gestortorneos.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TorneoResponse crear(
            @Valid @RequestBody CrearTorneoRequest peticion,
            @AuthenticationPrincipal Long organizadorId) {

        return torneoService.crear(peticion.nombre(), peticion.formato(), organizadorId);
    }

    @GetMapping
    public List<TorneoResponse> listar() {
        return torneoService.listar();
    }

    @GetMapping("/{id}")
    public TorneoResponse porId(@PathVariable Long id) {
        return torneoService.buscarPorId(id);
    }
}