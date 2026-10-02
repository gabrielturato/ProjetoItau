package br.com.itau.renegociacao.domain.exception;

/**
 * Violação de uma regra de negócio ou invariante do domínio.
 */
public class DomainException extends RuntimeException {

    public DomainException(String mensagem) {
        super(mensagem);
    }
}