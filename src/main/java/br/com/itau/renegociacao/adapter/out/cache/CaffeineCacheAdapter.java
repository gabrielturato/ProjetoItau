package br.com.itau.renegociacao.adapter.out.cache;

import br.com.itau.renegociacao.application.port.out.CachePort;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Optional;

/**
 * Não sobrescreve {@code buscarOuCarregar} com {@code cache.get(chave, loader)} de propósito:
 * o loader roda dentro de um lock do ConcurrentHashMap, e segurar um lock durante uma chamada HTTP
 * bloqueia outras chaves e prende a carrier thread das virtual threads no Java 21.
 */
public final class CaffeineCacheAdapter<K, V> implements CachePort<K, V> {

    private final Cache<K, V> cache;

    public CaffeineCacheAdapter(Duration ttl, long tamanhoMaximo) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(tamanhoMaximo)
                .build();
    }

    @Override
    public Optional<V> buscar(K chave) {
        return Optional.ofNullable(cache.getIfPresent(chave));
    }

    @Override
    public void guardar(K chave, V valor) {
        cache.put(chave, valor);
    }
}