package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.RondaResponse;
import com.maxi.gestortorneos.service.CalendarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CalendarioController {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    @GetMapping("/api/torneos/{torneoId}/calendario")
    public List<RondaResponse> calendario(@PathVariable Long torneoId) {
        return calendarioService.deTorneo(torneoId);
    }
}
