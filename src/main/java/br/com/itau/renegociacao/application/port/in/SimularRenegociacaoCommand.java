package br.com.itau.renegociacao.application.port.in;

import br.com.itau.renegociacao.domain.exception.DomainException;
import br.com.itau.renegociacao.domain.model.Cnpj;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.JornadaId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Sem {@code jornadaId} uma nova jornada é criada; com ele, a jornada existente é simulada novamente.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class SimularRenegociacaoCommand {

    private final Optional<JornadaId> jornadaId;
    private final Cnpj cnpj;
    private final List<ContratoId> contratos;

    private SimularRenegociacaoCommand(Optional<JornadaId> jornadaId, Cnpj cnpj, List<ContratoId> contratos) {
        if (contratos == null || contratos.isEmpty()) {
            throw new DomainException("Informe ao menos um contrato");
        }
        this.jornadaId = Objects.requireNonNull(jornadaId, "jornadaId");
        this.cnpj = Objects.requireNonNull(cnpj, "cnpj");
        this.contratos = List.copyOf(contratos);
    }

    public static SimularRenegociacaoCommand novaJornada(Cnpj cnpj, List<ContratoId> contratos) {
        return new SimularRenegociacaoCommand(Optional.empty(), cnpj, contratos);
    }

    public static SimularRenegociacaoCommand jornadaExistente(JornadaId jornadaId, Cnpj cnpj, List<ContratoId> contratos) {
        return new SimularRenegociacaoCommand(Optional.of(jornadaId), cnpj, contratos);
    }
}