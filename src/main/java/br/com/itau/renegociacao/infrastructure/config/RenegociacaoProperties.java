package br.com.itau.renegociacao.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@ConfigurationProperties(prefix = "renegociacao")
public class RenegociacaoProperties {

    private Map<String, Cache> caches = new HashMap<>();
    private Map<String, Integracao> integracoes = new HashMap<>();
    private Map<String, Boolean> featureFlags = new HashMap<>();

    public Cache cache(String nome) {
        return Optional.ofNullable(caches.get(nome))
                .orElseThrow(() -> new IllegalStateException("Cache não configurado: " + nome));
    }

    public Integracao integracao(String nome) {
        return Optional.ofNullable(integracoes.get(nome))
                .orElseThrow(() -> new IllegalStateException("Integração não configurada: " + nome));
    }

    @Getter
    @Setter
    public static class Cache {
        private Duration ttl;
        private long tamanhoMaximo;
    }

    @Getter
    @Setter
    public static class Integracao {
        private String url;
        private Duration timeout;
    }
}