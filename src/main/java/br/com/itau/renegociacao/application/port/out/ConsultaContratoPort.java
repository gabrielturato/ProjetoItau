package br.com.itau.renegociacao.application.port.out;

import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;

import java.util.Optional;

public interface ConsultaContratoPort {

    Optional<Contrato> consultar(ContratoId id);
}