package br.com.itau.renegociacao.domain.exception;

import br.com.itau.renegociacao.domain.model.ContratoId;

public class ContratoNaoEncontradoException extends RecursoNaoEncontradoException {

    public ContratoNaoEncontradoException(ContratoId id) {
        super("Contrato %s do produto %s não encontrado".formatted(id.getNumeroContrato(), id.getCodigoProduto()));
    }
}