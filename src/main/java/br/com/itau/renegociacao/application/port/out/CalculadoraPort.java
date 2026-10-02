package br.com.itau.renegociacao.application.port.out;

import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;

/**
 * Contrato único para qualquer calculadora (mainframe, modernizada) e para os decorators
 * que as envolvem (cache, roteamento por feature flag).
 */
public interface CalculadoraPort {

    Simulacao calcular(SolicitacaoCalculo solicitacao);
}