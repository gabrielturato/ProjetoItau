package br.com.itau.renegociacao.application.calculadora;

import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Chave canônica (string) de uma simulação: independe da ordem dos contratos e serve para qualquer
 * armazenamento (Caffeine, Redis, coluna de banco). O contrato entra só por número e produto.
 * O id da jornada nunca entra na chave, pois cada simulação gera uma jornada nova.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class ChaveSimulacao {

    private final String valor;

    private ChaveSimulacao(String valor) {
        this.valor = valor;
    }

    /**
     * O motor entra na chave para que, ao virar a feature flag, a simulação não venha do cache
     * da outra calculadora.
     */
    public static ChaveSimulacao porCliente(SolicitacaoCalculo solicitacao, MotorCalculo motor) {
        return new ChaveSimulacao(motor + "|" + solicitacao.getClienteId().getValor() + "|" + parametros(solicitacao));
    }

    public static ChaveSimulacao porParametros(SolicitacaoCalculo solicitacao) {
        return new ChaveSimulacao(parametros(solicitacao));
    }

    private static String parametros(SolicitacaoCalculo solicitacao) {
        String contratos = solicitacao.getContratos().stream()
                .sorted(Comparator.comparing((Contrato c) -> c.getId().getNumeroContrato())
                        .thenComparing(c -> c.getId().getCodigoProduto()))
                .map(ChaveSimulacao::contrato)
                .collect(Collectors.joining(","));
        return solicitacao.getPolitica().getTipoCalculadora()
                + "|" + solicitacao.getPolitica().getQuantidadeMaximaParcelas()
                + "|" + contratos;
    }

    private static String contrato(Contrato contrato) {
        return contrato.getId().getNumeroContrato() + ":" + contrato.getId().getCodigoProduto();
    }
}