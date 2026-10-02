package br.com.itau.renegociacao.adapter.out.calculadora.modernizada;

import br.com.itau.renegociacao.adapter.out.calculadora.modernizada.CalculadoraModernizadaHttpAdapter.SimulacaoRequestDTO;
import br.com.itau.renegociacao.adapter.out.calculadora.modernizada.CalculadoraModernizadaHttpAdapter.SimulacaoResponseDTO;
import br.com.itau.renegociacao.domain.model.Custodia;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;

final class SimulacaoModernizadaMapper {

    private SimulacaoModernizadaMapper() {
    }

    static SimulacaoRequestDTO paraRequest(SolicitacaoCalculo solicitacao) {
        return new SimulacaoRequestDTO(
                solicitacao.getJornadaId().getValor(),
                solicitacao.getClienteId().getValor(),
                solicitacao.getPolitica().getTipoCalculadora().name(),
                solicitacao.getPolitica().getQuantidadeMaximaParcelas(),
                solicitacao.getContratos().stream().map(ContratoModernizadaMapper::paraRequest).toList());
    }

    static Simulacao paraDominio(SimulacaoResponseDTO resposta) {
        return new Simulacao(MotorCalculo.MODERNIZADA, Custodia.valueOf(resposta.getCustodia()),
                resposta.getPlanos().stream().map(PlanoParcelamentoModernizadaMapper::paraDominio).toList());
    }
}
