package com.maxi.gestortorneos.clasificacion.desempate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfiguracionDesempates {

    @Bean
    public ResolutorDeEmpates resolutorDeEmpates() {
        return new ResolutorDeEmpates(List.of(
                new SeriesGanadasEntreEmpatados(),
                new DiferenciaDePartidasEntreEmpatados(),
                new DiferenciaDePartidasGlobal(),
                new StrengthOfVictory(),
                new ResultadoDesempate()));
    }
}