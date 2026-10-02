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
@RequestMapping("/simuladores/regras-negocio/v1/politicas")
@ConditionalOnProperty(name = "simuladores.habilitados", havingValue = "true")
class RegrasNegocioSimuladorController {

    private static final BigDecimal LIMITE_PRICE = new BigDecimal("150000.00");
    private static final int DIAS_ATRASO_CURTO = 30;

    @PostMapping
    PoliticaResponseDTO definir(@RequestBody PoliticaRequestDTO request) {
        BigDecimal total = request.getContratos().stream()
                .map(ContratoPoliticaDTO::getValorAtrasado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int maiorAtraso = request.getContratos().stream().mapToInt(ContratoPoliticaDTO::getDiasAtraso).max().orElse(0);

        String tipoCalculadora = total.compareTo(LIMITE_PRICE) > 0 ? "SAC" : "PRICE";
        int quantidadeMaximaParcelas = maiorAtraso <= DIAS_ATRASO_CURTO ? 6 : 12;
        return new PoliticaResponseDTO(tipoCalculadora, quantidadeMaximaParcelas);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class PoliticaRequestDTO {
        private UUID idJornada;
        private UUID idCliente;
        private List<ContratoPoliticaDTO> contratos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ContratoPoliticaDTO {
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
    static class PoliticaResponseDTO {
        private String tipoCalculadora;
        private int quantidadeMaximaParcelas;
    }
}