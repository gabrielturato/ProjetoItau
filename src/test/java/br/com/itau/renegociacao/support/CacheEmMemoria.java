package br.com.itau.renegociacao.support;

import br.com.itau.renegociacao.application.port.out.CachePort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CacheEmMemoria<K, V> implements CachePort<K, V> {

    private final Map<K, V> valores = new HashMap<>();

    @Override
    public Optional<V> buscar(K chave) {
        return Optional.ofNullable(valores.get(chave));
    }

    @Override
    public void guardar(K chave, V valor) {
        valores.put(chave, valor);
    }

    public int tamanho() {
        return valores.size();
    }
}