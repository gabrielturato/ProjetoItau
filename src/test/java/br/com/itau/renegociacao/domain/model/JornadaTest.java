package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.itau.renegociacao.support.Fixtures.clienteId;
import static br.com.itau.renegociacao.support.Fixtures.contrato;
import static br.com.itau.renegociacao.support.Fixtures.politica;
import static br.com.itau.renegociacao.support.Fixtures.simulacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JornadaTest {

    private final Jornada iniciada = Jornada.iniciar(JornadaId.nova(), clienteId(), List.of(contrato("1"), contrato("2")));

    @Test
    void evoluiStatusAteSimulada() {
        Jornada comPolitica = iniciada.comPolitica(politica(6));
        Jornada simulada = comPolitica.comSimulacao(simulacao(MotorCalculo.MODERNIZADA, 6));

        assertThat(iniciada.getStatus()).isEqualTo(StatusJornada.INICIADA);
        assertThat(comPolitica.getStatus()).isEqualTo(StatusJornada.POLITICA_DEFINIDA);
        assertThat(simulada.getStatus()).isEqualTo(StatusJornada.SIMULADA);
    }

    @Test
    void naoSimulaSemPolitica() {
        assertThatThrownBy(() -> iniciada.comSimulacao(simulacao(MotorCalculo.MAINFRAME, 3)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejeitaSimulacaoComMaisParcelasQueAPolitica() {
        Jornada comPolitica = iniciada.comPolitica(politica(6));

        assertThatThrownBy(() -> comPolitica.comSimulacao(simulacao(MotorCalculo.MAINFRAME, 12)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void reiniciarSimulacaoMantemIdEClienteEDescartaResultadoAnterior() {
        Jornada simulada = iniciada.comPolitica(politica(6)).comSimulacao(simulacao(MotorCalculo.MAINFRAME, 6));

        Jornada reiniciada = simulada.reiniciarSimulacao(List.of(contrato("3")));

        assertThat(reiniciada.getId()).isEqualTo(simulada.getId());
        assertThat(reiniciada.getClienteId()).isEqualTo(simulada.getClienteId());
        assertThat(reiniciada.getContratos()).containsExactly(contrato("3"));
        assertThat(reiniciada.getStatus()).isEqualTo(StatusJornada.INICIADA);
        assertThat(reiniciada.getPolitica()).isEmpty();
        assertThat(reiniciada.getSimulacao()).isEmpty();
    }

    @Test
    void rejeitaContratosRepetidos() {
        assertThatThrownBy(() -> Jornada.iniciar(JornadaId.nova(), clienteId(), List.of(contrato("1"), contrato("1"))))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejeitaPoliticaAcimaDeDozeParcelas() {
        assertThatThrownBy(() -> politica(13)).isInstanceOf(DomainException.class);
    }
}