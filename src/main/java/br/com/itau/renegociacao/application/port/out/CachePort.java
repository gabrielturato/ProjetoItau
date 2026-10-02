package br.com.itau.renegociacao.application.port.out;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Abstração genérica de chave/valor. Reutilizada para contratos, simulações e jornadas;
 * a implementação (Caffeine, Redis, banco...) é escolhida apenas na configuração.
 */
public interface CachePort<K, V> {

    Optional<V> buscar(K chave);

    void guardar(K chave, V valor);

    default V buscarOuCarregar(K chave, Supplier<V> carregador) {
        return buscar(chave).orElseGet(() -> {
            V valor = carregador.get();
            guardar(chave, valor);
            return valor;
        });
    }
}