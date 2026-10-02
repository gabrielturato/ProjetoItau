package br.com.itau.renegociacao.application.port.out;

import java.util.Arrays;
import java.util.Optional;

public enum Feature {

    CALCULADORA_MODERNIZADA("calculadora-modernizada");

    private final String chave;

    Feature(String chave) {
        this.chave = chave;
    }

    public String chave() {
        return chave;
    }

    public static Optional<Feature> porChave(String chave) {
        return Arrays.stream(values())
                .filter(feature -> feature.chave.equals(chave))
                .findFirst();
    }
}