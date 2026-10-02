package br.com.itau.renegociacao.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador tokenizado do cliente. É o único identificador de cliente que trafega no sistema.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class ClienteId {

    private final UUID valor;

    public ClienteId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "valor");
    }
}