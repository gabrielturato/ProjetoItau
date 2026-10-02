package br.com.itau.renegociacao.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JornadaResponseDTO {

    private UUID idJornada;
    private UUID idCliente;
    private String status;
    private List<ContratoResponseDTO> contratos;
    private PoliticaResponseDTO politica;
    private SimulacaoResponseDTO simulacao;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContratoResponseDTO {
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
    public static class PoliticaResponseDTO {
        private String tipoCalculadora;
        private int quantidadeMaximaParcelas;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulacaoResponseDTO {
        private String motorCalculo;
        private String custodia;
        private List<PlanoResponseDTO> planos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanoResponseDTO {
        private int quantidadeParcelas;
        private BigDecimal taxaJurosMensal;
        private BigDecimal valorIof;
        private BigDecimal valorParcela;
        private BigDecimal valorTotal;
    }
}