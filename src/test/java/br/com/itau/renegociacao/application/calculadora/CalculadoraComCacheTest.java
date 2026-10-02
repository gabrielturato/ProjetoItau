package br.com.itau.renegociacao.application.calculadora;

import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.SistemaOrigem;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import br.com.itau.renegociacao.support.CacheEmMemoria;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.itau.renegociacao.support.Fixtures.clienteId;
import static br.com.itau.renegociacao.support.Fixtures.contrato;
import static br.com.itau.renegociacao.support.Fixtures.simulacao;
import static br.com.itau.renegociacao.support.Fixtures.solicitacao;
import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraComCacheTest {

    private final AtomicInteger chamadas = new AtomicInteger();
    private final CalculadoraPort calculadoraContada = solicitacao -> {
        chamadas.incrementAndGet();
        return simulacao(MotorCalculo.MODERNIZADA, 12);
    };

    @Test
    void naoRecalculaAMesmaSimulacaoDoMesmoCliente() {
        CalculadoraPort calculadora = new CalculadoraComCache(calculadoraContada, new CacheEmMemoria<>(), s -> ChaveSimulacao.porCliente(s, MotorCalculo.MODERNIZADA));
        ClienteId cliente = clienteId();

        calculadora.calcular(solicitacao(cliente, contrato("1"), contrato("2")));
        calculadora.calcular(solicitacao(cliente, contrato("2"), contrato("1")));

        assertThat(chamadas).hasValue(1);
    }

    @Test
    void chavePorClienteSeparaClientesDiferentes() {
        CalculadoraPort calculadora = new CalculadoraComCache(calculadoraContada, new CacheEmMemoria<>(), s -> ChaveSimulacao.porCliente(s, MotorCalculo.MODERNIZADA));

        calculadora.calcular(solicitacao(clienteId(), contrato("1")));
        calculadora.calcular(solicitacao(clienteId(), contrato("1")));

        assertThat(chamadas).hasValue(2);
    }

    @Test
    void chavePorParametrosReaproveitaEntreClientes() {
        CalculadoraPort calculadora = new CalculadoraComCache(calculadoraContada, new CacheEmMemoria<>(), ChaveSimulacao::porParametros);

        calculadora.calcular(solicitacao(clienteId(), contrato("1")));
        calculadora.calcular(solicitacao(clienteId(), contrato("1")));

        assertThat(chamadas).hasValue(1);
    }

    @Test
    void chavePorClienteSeparaMotoresDiferentes() {
        SolicitacaoCalculo solicitacao = solicitacao(clienteId(), contrato("1"));

        assertThat(ChaveSimulacao.porCliente(solicitacao, MotorCalculo.MAINFRAME))
                .isNotEqualTo(ChaveSimulacao.porCliente(solicitacao, MotorCalculo.MODERNIZADA));
    }

    @Test
    void contratoEntraNaChaveSoPorNumeroEProduto() {
        ClienteId cliente = clienteId();
        Contrato original = new Contrato(new ContratoId("1", "PJ01"), new BigDecimal("100.00"), 10, SistemaOrigem.SF);
        Contrato atualizado = new Contrato(new ContratoId("1", "PJ01"), new BigDecimal("250"), 45, SistemaOrigem.CC);
        Contrato outroProduto = new Contrato(new ContratoId("1", "PJ02"), new BigDecimal("100.00"), 10, SistemaOrigem.SF);

        assertThat(ChaveSimulacao.porCliente(solicitacao(cliente, original), MotorCalculo.MAINFRAME))
                .isEqualTo(ChaveSimulacao.porCliente(solicitacao(cliente, atualizado), MotorCalculo.MAINFRAME))
                .isNotEqualTo(ChaveSimulacao.porCliente(solicitacao(cliente, outroProduto), MotorCalculo.MAINFRAME));
    }
}