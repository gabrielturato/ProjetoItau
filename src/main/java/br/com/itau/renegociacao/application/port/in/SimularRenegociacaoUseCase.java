package br.com.itau.renegociacao.application.port.in;

import br.com.itau.renegociacao.domain.model.Jornada;

public interface SimularRenegociacaoUseCase {

    Jornada executar(SimularRenegociacaoCommand comando);
}