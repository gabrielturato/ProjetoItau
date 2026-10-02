package br.com.itau.renegociacao.application.port.out;

import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;

import java.util.Optional;

public interface JornadaRepositoryPort {

    Jornada salvar(Jornada jornada);

    Optional<Jornada> buscar(JornadaId id);
}