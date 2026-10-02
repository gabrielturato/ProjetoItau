package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import br.com.itau.renegociacao.application.exception.IntegracaoException;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.Custodia;
import br.com.itau.renegociacao.domain.model.JornadaId;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SistemaOrigem;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import br.com.itau.renegociacao.domain.model.TipoCalculadora;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.itau.renegociacao.support.Fixtures.clienteId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulacaoMainframeMapperTest {

    @Test
    void enviaCadaContratoNaRequisicaoDoLegado() {
        SolicitacaoCalculo solicitacao = new SolicitacaoCalculo(JornadaId.nova(), clienteId(), List.of(
                new Contrato(new ContratoId("1", "PJ01"), new BigDecimal("1000.10"), 40, SistemaOrigem.CC),
                new Contrato(new ContratoId("2", "PJ02"), new BigDecimal("500.25"), 90, SistemaOrigem.QQ)),
                new PoliticaRenegociacao(TipoCalculadora.SAC, 10));

        MainframeCopybook.RequisicaoDTO requisicao = SimulacaoMainframeMapper.paraMainframe(solicitacao);

        assertThat(requisicao.getTipoCalculo()).isEqualTo("S");
        assertThat(requisicao.getQuantidadeMaximaParcelas()).isEqualTo(10);
        assertThat(requisicao.getContratos()).hasSize(2);
        assertThat(requisicao.getContratos().get(1)).satisfies(contrato -> {
            assertThat(contrato.getNumeroContrato()).isEqualTo("2");
            assertThat(contrato.getCodigoProduto()).isEqualTo("PJ02");
            assertThat(contrato.getSaldo()).isEqualByComparingTo("500.25");
            assertThat(contrato.getDiasAtraso()).isEqualTo(90);
            assertThat(contrato.getSistemaOrigem()).isEqualTo("QQ");
        });
    }

    @Test
    void traduzRespostaParaODominio() {
        MainframeCopybook.RespostaDTO resposta = new MainframeCopybook.RespostaDTO("00", "F5", List.of(
                new MainframeCopybook.PlanoDTO(1, new BigDecimal("1.99"), new BigDecimal("4.50"),
                        new BigDecimal("1025.00"), new BigDecimal("1025.00"))));

        Simulacao simulacao = SimulacaoMainframeMapper.paraDominio(resposta);

        assertThat(simulacao.getMotor()).isEqualTo(MotorCalculo.MAINFRAME);
        assertThat(simulacao.getCustodia()).isEqualTo(Custodia.F5);
        assertThat(simulacao.getPlanos()).singleElement().satisfies(plano -> {
            assertThat(plano.getTaxaJurosMensal()).isEqualByComparingTo("1.99");
            assertThat(plano.getValorIof()).isEqualByComparingTo("4.50");
            assertThat(plano.getValorParcela()).isEqualByComparingTo("1025.00");
        });
    }

    @Test
    void codigoDeRetornoDeErroViraFalhaDeIntegracao() {
        MainframeCopybook.RespostaDTO resposta = new MainframeCopybook.RespostaDTO("08", null, List.of());

        assertThatThrownBy(() -> SimulacaoMainframeMapper.paraDominio(resposta))
                .isInstanceOf(IntegracaoException.class)
                .hasMessageContaining("08");
    }
}