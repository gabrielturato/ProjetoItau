package br.com.itau.renegociacao.domain.exception;

public abstract class RecursoNaoEncontradoException extends DomainException {

    protected RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}