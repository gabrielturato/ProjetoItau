package br.com.itau.renegociacao.application.port.out;

import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;

public interface RegrasNegocioPort {

    PoliticaRenegociacao definirPolitica(Jornada jornada);
}