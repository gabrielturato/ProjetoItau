package br.com.itau.renegociacao.application.port.in;

import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;

public interface ConsultarJornadaUseCase {

    Jornada consultar(JornadaId id);
}