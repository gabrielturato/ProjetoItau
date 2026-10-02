package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Política definida pelo motor de regras de negócio: como a renegociação deve ser simulada.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class PoliticaRenegociacao {

    public static final int LIMITE_PARCELAS = 12;

    private final TipoCalculadora tipoCalculadora;
    private final int quantidadeMaximaParcelas;

    public PoliticaRenegociacao(TipoCalculadora tipoCalculadora, int quantidadeMaximaParcelas) {
        if (quantidadeMaximaParcelas < 1 || quantidadeMaximaParcelas > LIMITE_PARCELAS) {
            throw new DomainException("Quantidade máxima de parcelas deve estar entre 1 e " + LIMITE_PARCELAS);
        }
        this.tipoCalculadora = Objects.requireNonNull(tipoCalculadora, "tipoCalculadora");
        this.quantidadeMaximaParcelas = quantidadeMaximaParcelas;
    }
}