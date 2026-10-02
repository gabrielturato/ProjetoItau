package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Um contrato é identificado pela combinação número do contrato + código do produto.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class ContratoId {

    private final String numeroContrato;
    private final String codigoProduto;

    public ContratoId(String numeroContrato, String codigoProduto) {
        if (vazio(numeroContrato) || vazio(codigoProduto)) {
            throw new DomainException("Número do contrato e código do produto são obrigatórios");
        }
        this.numeroContrato = numeroContrato.trim();
        this.codigoProduto = codigoProduto.trim();
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}