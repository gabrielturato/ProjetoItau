package br.com.itau.renegociacao.domain.exception;

public class CnpjInvalidoException extends DomainException {

    public CnpjInvalidoException() {
        super("CNPJ inválido");
    }
}