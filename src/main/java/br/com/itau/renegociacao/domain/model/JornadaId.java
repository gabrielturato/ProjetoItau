package br.com.itau.renegociacao.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public final class JornadaId {

    private final UUID valor;

    public JornadaId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "valor");
    }

    public static JornadaId nova() {
        return new JornadaId(UUID.randomUUID());
    }
}