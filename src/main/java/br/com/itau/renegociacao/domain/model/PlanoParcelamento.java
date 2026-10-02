package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Um plano de pagamento em N parcelas retornado pela calculadora.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class PlanoParcelamento {

    private final int quantidadeParcelas;
    /** Percentual ao mês (ex.: 1.99 = 1,99% a.m.). */
    private final BigDecimal taxaJurosMensal;
    private final BigDecimal valorIof;
    private final BigDecimal valorParcela;
    private final BigDecimal valorTotal;

    public PlanoParcelamento(int quantidadeParcelas, BigDecimal taxaJurosMensal, BigDecimal valorIof,
                             BigDecimal valorParcela, BigDecimal valorTotal) {
        if (quantidadeParcelas < 1) {
            throw new DomainException("Plano deve ter ao menos uma parcela");
        }
        this.quantidadeParcelas = quantidadeParcelas;
        this.taxaJurosMensal = Objects.requireNonNull(taxaJurosMensal, "taxaJurosMensal");
        this.valorIof = Objects.requireNonNull(valorIof, "valorIof");
        this.valorParcela = Objects.requireNonNull(valorParcela, "valorParcela");
        this.valorTotal = Objects.requireNonNull(valorTotal, "valorTotal");
    }
}