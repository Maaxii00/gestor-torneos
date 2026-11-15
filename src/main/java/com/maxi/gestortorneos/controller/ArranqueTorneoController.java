package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.ArranqueResponse;
import com.maxi.gestortorneos.service.ArranqueTorneoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArranqueTorneoController {

    private final ArranqueTorneoService arranqueTorneoService;

    public ArranqueTorneoController(ArranqueTorneoService arranqueTorneoService) {
        this.arranqueTorneoService = arranqueTorneoService;
    }

    @PostMapping("/api/torneos/{torneoId}/iniciar")
    public ArranqueResponse iniciar(@PathVariable Long torneoId,
                                    @AuthenticationPrincipal Long solicitanteId) {
        return arranqueTorneoService.iniciar(torneoId, solicitanteId);
    }
}