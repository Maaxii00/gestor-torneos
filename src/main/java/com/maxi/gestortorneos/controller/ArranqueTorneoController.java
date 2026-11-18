package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.ArranqueResponse;
import com.maxi.gestortorneos.dto.TorneoResponse;
import com.maxi.gestortorneos.service.ArranqueTorneoService;
import com.maxi.gestortorneos.service.FinalizacionTorneoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArranqueTorneoController {

    private final ArranqueTorneoService arranqueTorneoService;
    private final FinalizacionTorneoService finalizacionTorneoService;

    public ArranqueTorneoController(ArranqueTorneoService arranqueTorneoService, FinalizacionTorneoService finalizacionTorneoService) {
        this.arranqueTorneoService = arranqueTorneoService;
        this.finalizacionTorneoService = finalizacionTorneoService;
    }

    @PostMapping("/api/torneos/{torneoId}/iniciar")
    public ArranqueResponse iniciar(@PathVariable Long torneoId,
                                    @AuthenticationPrincipal Long solicitanteId) {
        return arranqueTorneoService.iniciar(torneoId, solicitanteId);
    }

    @PostMapping("/api/torneos/{torneoId}/finalizar")
    public TorneoResponse finalizar(@PathVariable Long torneoId,
                                    @AuthenticationPrincipal Long solicitanteId) {
        return finalizacionTorneoService.finalizar(torneoId, solicitanteId);
    }
}