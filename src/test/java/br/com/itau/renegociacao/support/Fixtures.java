package br.com.itau.renegociacao.support;

import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.Custodia;
import br.com.itau.renegociacao.domain.model.JornadaId;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.PlanoParcelamento;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SistemaOrigem;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import br.com.itau.renegociacao.domain.model.TipoCalculadora;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public final class Fixtures {

    private Fixtures() {
    }

    public static ClienteId clienteId() {
        return new ClienteId(UUID.randomUUID());
    }

    public static Contrato contrato(String numero) {
        return new Contrato(new ContratoId(numero, "PJ01"), new BigDecimal("10000.00"), 45, SistemaOrigem.CC);
    }

    public static PoliticaRenegociacao politica(int quantidadeMaximaParcelas) {
        return new PoliticaRenegociacao(TipoCalculadora.PRICE, quantidadeMaximaParcelas);
    }

    public static Simulacao simulacao(MotorCalculo motor, int quantidadePlanos) {
        List<PlanoParcelamento> planos = IntStream.rangeClosed(1, quantidadePlanos)
                .mapToObj(n -> new PlanoParcelamento(n, new BigDecimal("1.99"), new BigDecimal("50.00"),
                        new BigDecimal("1000.00"), new BigDecimal("1000.00").multiply(BigDecimal.valueOf(n))))
                .toList();
        return new Simulacao(motor, motor == MotorCalculo.MAINFRAME ? Custodia.F5 : Custodia.SF, planos);
    }

    public static SolicitacaoCalculo solicitacao(ClienteId clienteId, Contrato... contratos) {
        return new SolicitacaoCalculo(JornadaId.nova(), clienteId, List.of(contratos), politica(12));
    }
}