package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Resultado da calculadora: planos disponíveis para o cliente.
 * Não carrega dados do cliente, por isso pode ser reaproveitada via cache.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class Simulacao {

    private final MotorCalculo motor;
    private final Custodia custodia;
    private final List<PlanoParcelamento> planos;

    public Simulacao(MotorCalculo motor, Custodia custodia, List<PlanoParcelamento> planos) {
        if (planos == null || planos.isEmpty()) {
            throw new DomainException("A simulação deve conter ao menos um plano");
        }
        this.motor = Objects.requireNonNull(motor, "motor");
        this.custodia = Objects.requireNonNull(custodia, "custodia");
        this.planos = planos.stream()
                .sorted(Comparator.comparingInt(PlanoParcelamento::getQuantidadeParcelas))
                .toList();
    }
}