package br.com.itau.renegociacao.application.usecase;

import br.com.itau.renegociacao.application.port.in.ConsultarJornadaUseCase;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.domain.exception.JornadaNaoEncontradaException;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;

public final class ConsultarJornadaService implements ConsultarJornadaUseCase {

    private final JornadaRepositoryPort jornadas;

    public ConsultarJornadaService(JornadaRepositoryPort jornadas) {
        this.jornadas = jornadas;
    }

    @Override
    public Jornada consultar(JornadaId id) {
        return jornadas.buscar(id).orElseThrow(() -> new JornadaNaoEncontradaException(id));
    }
}