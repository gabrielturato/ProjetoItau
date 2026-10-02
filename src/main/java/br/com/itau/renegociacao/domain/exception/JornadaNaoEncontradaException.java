package br.com.itau.renegociacao.domain.exception;

import br.com.itau.renegociacao.domain.model.JornadaId;

public class JornadaNaoEncontradaException extends RecursoNaoEncontradoException {

    public JornadaNaoEncontradaException(JornadaId id) {
        super("Jornada %s não encontrada".formatted(id.getValor()));
    }
}