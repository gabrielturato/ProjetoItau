package br.com.itau.renegociacao.application.calculadora;

import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static br.com.itau.renegociacao.support.Fixtures.clienteId;
import static br.com.itau.renegociacao.support.Fixtures.contrato;
import static br.com.itau.renegociacao.support.Fixtures.simulacao;
import static br.com.itau.renegociacao.support.Fixtures.solicitacao;
import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraRoteadaPorFeatureFlagTest {

    private final AtomicBoolean modernizadaHabilitada = new AtomicBoolean();
    private final CalculadoraPort calculadora = new CalculadoraRoteadaPorFeatureFlag(
            s -> simulacao(MotorCalculo.MODERNIZADA, 3),
            s -> simulacao(MotorCalculo.MAINFRAME, 3),
            feature -> modernizadaHabilitada.get());
    private final SolicitacaoCalculo solicitacao = solicitacao(clienteId(), contrato("1"));

    @Test
    void usaMainframeComFlagDesligada() {
        modernizadaHabilitada.set(false);

        assertThat(calculadora.calcular(solicitacao).getMotor()).isEqualTo(MotorCalculo.MAINFRAME);
    }

    @Test
    void usaModernizadaComFlagLigadaERespeitaRollback() {
        modernizadaHabilitada.set(true);
        assertThat(calculadora.calcular(solicitacao).getMotor()).isEqualTo(MotorCalculo.MODERNIZADA);

        modernizadaHabilitada.set(false);
        assertThat(calculadora.calcular(solicitacao).getMotor()).isEqualTo(MotorCalculo.MAINFRAME);
    }
}