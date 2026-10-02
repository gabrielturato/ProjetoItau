package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Contrato em atraso selecionado pelo cliente para renegociação.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class Contrato {

    private final ContratoId id;
    private final BigDecimal valorAtrasado;
    private final int diasAtraso;
    private final SistemaOrigem sistemaOrigem;

    public Contrato(ContratoId id, BigDecimal valorAtrasado, int diasAtraso, SistemaOrigem sistemaOrigem) {
        if (valorAtrasado == null || valorAtrasado.signum() <= 0) {
            throw new DomainException("Valor atrasado do contrato deve ser positivo");
        }
        if (diasAtraso < 0) {
            throw new DomainException("Dias de atraso não pode ser negativo");
        }
        this.id = Objects.requireNonNull(id, "id");
        this.valorAtrasado = valorAtrasado;
        this.diasAtraso = diasAtraso;
        this.sistemaOrigem = Objects.requireNonNull(sistemaOrigem, "sistemaOrigem");
    }
}