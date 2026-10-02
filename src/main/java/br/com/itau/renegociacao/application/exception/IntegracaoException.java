package br.com.itau.renegociacao.application.exception;

/**
 * Falha ao se comunicar com um serviço externo (indisponibilidade, timeout, resposta inválida).
 */
public class IntegracaoException extends RuntimeException {

    private final String servico;

    public IntegracaoException(String servico, String detalhe) {
        super("Falha na integração com '%s': %s".formatted(servico, detalhe));
        this.servico = servico;
    }

    public IntegracaoException(String servico, Throwable causa) {
        super("Falha na integração com '%s'".formatted(servico), causa);
        this.servico = servico;
    }

    public String servico() {
        return servico;
    }
}