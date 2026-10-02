package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Layout de troca da calculadora legada, espelhando a nomenclatura do copybook COBOL.
 */
final class MainframeCopybook {

    private MainframeCopybook() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class RequisicaoDTO {
        @JsonProperty("CD-TIPO-CALC")
        private String tipoCalculo;
        @JsonProperty("QT-MAX-PARC")
        private int quantidadeMaximaParcelas;
        @JsonProperty("LS-CONTRATOS")
        private List<ContratoDTO> contratos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ContratoDTO {
        @JsonProperty("NR-CONTRATO")
        private String numeroContrato;
        @JsonProperty("CD-PRODUTO")
        private String codigoProduto;
        @JsonProperty("VL-SALDO")
        private BigDecimal saldo;
        @JsonProperty("QT-DIAS-ATRASO")
        private int diasAtraso;
        @JsonProperty("CD-SIST-ORIGEM")
        private String sistemaOrigem;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class RespostaDTO {
        @JsonProperty("CD-RETORNO")
        private String codigoRetorno;
        @JsonProperty("CD-CUSTODIA")
        private String custodia;
        @JsonProperty("LS-PLANOS")
        private List<PlanoDTO> planos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class PlanoDTO {
        @JsonProperty("QT-PARC")
        private int quantidadeParcelas;
        @JsonProperty("PC-JUROS")
        private BigDecimal taxaJurosMensal;
        @JsonProperty("VL-IOF")
        private BigDecimal valorIof;
        @JsonProperty("VL-PARC")
        private BigDecimal valorParcela;
        @JsonProperty("VL-TOTAL")
        private BigDecimal valorTotal;
    }
}