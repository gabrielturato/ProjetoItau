package br.com.itau.renegociacao.application.port.out;

public interface FeatureFlagPort {

    boolean estaHabilitada(Feature feature);
}