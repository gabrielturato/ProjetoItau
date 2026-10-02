package br.com.itau.renegociacao.simulador;

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
import java.util.UUID;

@RestController
@RequestMapping("/simuladores/calculadora-modernizada/v1/simulacoes")
@ConditionalOnProperty(name = "simuladores.habilitados", havingValue = "true")
class CalculadoraModernizadaSimuladorController {

    @PostMapping
    SimulacaoResponseDTO simular(@RequestBody SimulacaoRequestDTO request) {
        BigDecimal saldo = request.getContratos().stream()
                .map(ContratoRequestDTO::getValorAtrasado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int maiorAtraso = request.getContratos().stream().mapToInt(ContratoRequestDTO::getDiasAtraso).max().orElse(0);

        List<PlanoResponseDTO> planos = MotorCalculoSimulado.calcular("SAC".equals(request.getTipoCalculadora()),
                        request.getQuantidadeMaximaParcelas(), saldo, maiorAtraso)
                .stream()
                .map(p -> new PlanoResponseDTO(p.getQuantidadeParcelas(), p.getTaxaJurosMensal(), p.getValorIof(),
                        p.getValorParcela(), p.getValorTotal()))
                .toList();
        return new SimulacaoResponseDTO("SF", planos);
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