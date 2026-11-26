package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Ronda;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.model.TipoFase;

import java.util.List;

public record RondaResponse(
        Long id,
        int numero,
        String nombre,
        int victoriasParaGanarSerie,
        String fase,
        TipoFase tipoFase,
        List<SerieResumen> series) {

    public static RondaResponse desde(Ronda ronda, List<Serie> series) {
        return new RondaResponse(
                ronda.getId(),
                ronda.getNumero(),
                ronda.getNombre(),
                ronda.getVictoriasParaGanarSerie(),
                ronda.getFase().getNombre(),
                ronda.getFase().getTipo(),
                series.stream().map(SerieResumen::desde).toList());
    }
}
