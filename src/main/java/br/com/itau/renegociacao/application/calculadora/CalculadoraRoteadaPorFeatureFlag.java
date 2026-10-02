package br.com.itau.renegociacao.application.calculadora;

import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.application.port.out.Feature;
import br.com.itau.renegociacao.application.port.out.FeatureFlagPort;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;

/**
 * Strangler Fig: a cada chamada decide, pela feature flag, se usa a calculadora modernizada
 * ou a legada do mainframe. Desligar o mainframe vira mudança de configuração, e o rollback é imediato.
 */
public final class CalculadoraRoteadaPorFeatureFlag implements CalculadoraPort {

    private final CalculadoraPort modernizada;
    private final CalculadoraPort legada;
    private final FeatureFlagPort featureFlags;

    public CalculadoraRoteadaPorFeatureFlag(CalculadoraPort modernizada,
                                            CalculadoraPort legada,
                                            FeatureFlagPort featureFlags) {
        this.modernizada = modernizada;
        this.legada = legada;
        this.featureFlags = featureFlags;
    }

    @Override
    public Simulacao calcular(SolicitacaoCalculo solicitacao) {
        CalculadoraPort alvo = motorAtivo() == MotorCalculo.MODERNIZADA ? modernizada : legada;
        return alvo.calcular(solicitacao);
    }

    public MotorCalculo motorAtivo() {
        return featureFlags.estaHabilitada(Feature.CALCULADORA_MODERNIZADA) ? MotorCalculo.MODERNIZADA : MotorCalculo.MAINFRAME;
    }
}