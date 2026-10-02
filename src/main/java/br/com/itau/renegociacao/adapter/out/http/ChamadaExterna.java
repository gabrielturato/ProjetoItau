package br.com.itau.renegociacao.adapter.out.http;

import br.com.itau.renegociacao.application.exception.IntegracaoException;

import java.util.function.Supplier;

/**
 * Padroniza a tradução de qualquer falha de um serviço externo (HTTP, timeout, payload inválido)
 * para {@link IntegracaoException}, para que nenhum detalhe técnico vaze para o núcleo.
 */
public final class ChamadaExterna {

    private ChamadaExterna() {
    }

    public static <T> T executar(String servico, Supplier<T> chamada) {
        T resultado;
        try {
            resultado = chamada.get();
        } catch (IntegracaoException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IntegracaoException(servico, e);
        }
        if (resultado == null) {
            throw new IntegracaoException(servico, "resposta vazia");
        }
        return resultado;
    }
}