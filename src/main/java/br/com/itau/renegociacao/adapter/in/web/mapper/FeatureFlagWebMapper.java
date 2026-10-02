package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.application.port.out.Feature;

import java.util.Map;
import java.util.TreeMap;

public final class FeatureFlagWebMapper {

    private FeatureFlagWebMapper() {
    }

    public static Map<String, Boolean> paraResponse(Map<Feature, Boolean> flags) {
        Map<String, Boolean> resultado = new TreeMap<>();
        flags.forEach((feature, habilitada) -> resultado.put(feature.chave(), habilitada));
        return resultado;
    }
}
