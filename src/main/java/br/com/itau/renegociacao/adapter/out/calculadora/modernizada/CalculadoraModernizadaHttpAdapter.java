package br.com.itau.renegociacao.adapter.out.calculadora.modernizada;

import br.com.itau.renegociacao.adapter.out.http.ChamadaExterna;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class CalculadoraModernizadaHttpAdapter implements CalculadoraPort {

    private static final String SERVICO = "calculadora-modernizada";

    private final RestClient restClient;

    public CalculadoraModernizadaHttpAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Simulacao calcular(SolicitacaoCalculo solicitacao) {
        return ChamadaExterna.executar(SERVICO, () -> {
            SimulacaoResponseDTO resposta = restClient.post()
                    .uri("/v1/simulacoes")
                    .body(SimulacaoModernizadaMapper.paraRequest(solicitacao))
                    .retrieve()
                    .body(SimulacaoResponseDTO.class);
            return SimulacaoModernizadaMapper.paraDominio(resposta);
        });
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SimulacaoRequestDTO {
        private UUID idJornada;
        private UUID idCliente;
        private String tipoCalculadora;
        private int quantidadeMaximaParcelas;
        private List<ContratoRequestDTO> contratos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ContratoRequestDTO {
        private String numeroContrato;
        private String codigoProduto;
        private BigDecimal valorAtrasado;
        private int diasAtraso;
        private String sistemaOrigem;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SimulacaoResponseDTO {
        private String custodia;
        private List<PlanoResponseDTO> planos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class PlanoResponseDTO {
        private int quantidadeParcelas;
        private BigDecimal taxaJurosMensal;
        private BigDecimal valorIof;
        private BigDecimal valorParcela;
        private BigDecimal valorTotal;
    }
}