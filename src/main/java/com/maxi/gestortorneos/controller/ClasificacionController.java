package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.FilaClasificacionResponse;
import com.maxi.gestortorneos.service.ClasificacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClasificacionController {

    private final ClasificacionService clasificacionService;

    public ClasificacionController(ClasificacionService clasificacionService) {
        this.clasificacionService = clasificacionService;
    }

    @GetMapping("/api/torneos/{torneoId}/clasificacion")
    public List<FilaClasificacionResponse> clasificacion(@PathVariable Long torneoId) {
        return clasificacionService.deTorneo(torneoId);
    }
}
