package br.com.itau.renegociacao.simulador;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/simuladores/calculadora-mainframe/v1/calculos")
@ConditionalOnProperty(name = "simuladores.habilitados", havingValue = "true")
class CalculadoraMainframeSimuladorController {

    private static final String RETORNO_SUCESSO = "00";
    private static final String RETORNO_TIPO_INVALIDO = "08";

    @PostMapping
    RespostaDTO calcular(@RequestBody RequisicaoDTO requisicao) {
        if (!"P".equals(requisicao.getTipoCalculo()) && !"S".equals(requisicao.getTipoCalculo())) {
            return new RespostaDTO(RETORNO_TIPO_INVALIDO, null, List.of());
        }
        BigDecimal saldo = requisicao.getContratos().stream()
                .map(ContratoDTO::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int maiorAtraso = requisicao.getContratos().stream().mapToInt(ContratoDTO::getDiasAtraso).max().orElse(0);

        List<PlanoDTO> planos = MotorCalculoSimulado.calcular("S".equals(requisicao.getTipoCalculo()),
                        requisicao.getQuantidadeMaximaParcelas(), saldo, maiorAtraso)
                .stream()
                .map(p -> new PlanoDTO(p.getQuantidadeParcelas(), p.getTaxaJurosMensal(), p.getValorIof(),
                        p.getValorParcela(), p.getValorTotal()))
                .toList();
        return new RespostaDTO(RETORNO_SUCESSO, "F5", planos);
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