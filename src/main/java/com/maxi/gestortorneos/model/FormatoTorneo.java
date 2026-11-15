package com.maxi.gestortorneos.model;

public enum FormatoTorneo {
    LIGA_RAPIDA(1),
    LIGA_CLASICA(2),
    SUIZO(1);

    private final int victoriasEnLiguilla;

    FormatoTorneo(int victoriasEnLiguilla){
        this.victoriasEnLiguilla=victoriasEnLiguilla;
    }

    public int getVictoriasEnLiguilla() {
        return victoriasEnLiguilla;
    }
}
