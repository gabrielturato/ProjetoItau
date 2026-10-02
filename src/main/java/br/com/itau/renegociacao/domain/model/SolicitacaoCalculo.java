package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * Tudo o que a calculadora precisa: dados do canal (cliente tokenizado, contratos) e da jornada (política).
 */
@Getter
@EqualsAndHashCode
@ToString
public final class SolicitacaoCalculo {

    private final JornadaId jornadaId;
    private final ClienteId clienteId;
    private final List<Contrato> contratos;
    private final PoliticaRenegociacao politica;

    public SolicitacaoCalculo(JornadaId jornadaId, ClienteId clienteId, List<Contrato> contratos,
                              PoliticaRenegociacao politica) {
        if (contratos == null || contratos.isEmpty()) {
            throw new DomainException("A solicitação de cálculo precisa de ao menos um contrato");
        }
        this.jornadaId = Objects.requireNonNull(jornadaId, "jornadaId");
        this.clienteId = Objects.requireNonNull(clienteId, "clienteId");
        this.contratos = List.copyOf(contratos);
        this.politica = Objects.requireNonNull(politica, "politica");
    }
}