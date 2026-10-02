package br.com.itau.renegociacao.adapter.out.featureflag;

import br.com.itau.renegociacao.application.port.out.Feature;
import br.com.itau.renegociacao.application.port.out.FeatureFlagPort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Substituível por um provedor real (AWS AppConfig, LaunchDarkly, Unleash) sem tocar no núcleo.
 */
public final class FeatureFlagEmMemoriaAdapter implements FeatureFlagPort {

    private final Map<Feature, Boolean> flags = new ConcurrentHashMap<>();

    public FeatureFlagEmMemoriaAdapter(Map<String, Boolean> valoresIniciais) {
        for (Feature feature : Feature.values()) {
            flags.put(feature, valoresIniciais.getOrDefault(feature.chave(), false));
        }
    }

    @Override
    public boolean estaHabilitada(Feature feature) {
        return flags.getOrDefault(feature, false);
    }

    public void alterar(Feature feature, boolean habilitada) {
        flags.put(feature, habilitada);
    }

    public Map<Feature, Boolean> todas() {
        return Map.copyOf(flags);
    }
}