package br.com.itau.renegociacao.application.calculadora;

import br.com.itau.renegociacao.application.port.out.CachePort;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;

import java.util.function.Function;

/**
 * Decorator: evita chamar a calculadora envolvida quando a simulação já está em cache.
 * A mesma classe serve tanto para o cache por cliente quanto para o da calculadora modernizada;
 * o que muda é a estratégia de chave.
 */
public final class CalculadoraComCache implements CalculadoraPort {

    private final CalculadoraPort calculadora;
    private final CachePort<ChaveSimulacao, Simulacao> cache;
    private final Function<SolicitacaoCalculo, ChaveSimulacao> estrategiaChave;

    public CalculadoraComCache(CalculadoraPort calculadora,
                               CachePort<ChaveSimulacao, Simulacao> cache,
                               Function<SolicitacaoCalculo, ChaveSimulacao> estrategiaChave) {
        this.calculadora = calculadora;
        this.cache = cache;
        this.estrategiaChave = estrategiaChave;
    }

    @Override
    public Simulacao calcular(SolicitacaoCalculo solicitacao) {
        return cache.buscarOuCarregar(estrategiaChave.apply(solicitacao), () -> calculadora.calcular(solicitacao));
    }
}