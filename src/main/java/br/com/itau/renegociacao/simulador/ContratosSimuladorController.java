package br.com.itau.renegociacao.simulador;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Gera dados determinísticos a partir do número do contrato. Contratos iniciados por "999" não existem.
 */
@RestController
@RequestMapping("/simuladores/contratos/v1/contratos")
@ConditionalOnProperty(name = "simuladores.habilitados", havingValue = "true")
class ContratosSimuladorController {

    private static final String PREFIXO_INEXISTENTE = "999";
    private static final String[] SISTEMAS_ORIGEM = {"CC", "SF", "QQ"};

    @GetMapping("/{numeroContrato}/produtos/{codigoProduto}")
    ResponseEntity<ContratoResponseDTO> consultar(@PathVariable String numeroContrato, @PathVariable String codigoProduto) {
        if (numeroContrato.startsWith(PREFIXO_INEXISTENTE)) {
            return ResponseEntity.notFound().build();
        }
        int semente = (numeroContrato + "-" + codigoProduto).hashCode();
        BigDecimal valorAtrasado = BigDecimal.valueOf(100_000L + Math.floorMod(semente, 9_900_000), 2);
        int diasAtraso = 1 + Math.floorMod(semente / 7, 400);
        String sistemaOrigem = SISTEMAS_ORIGEM[Math.floorMod(semente, SISTEMAS_ORIGEM.length)];
        return ResponseEntity.ok(new ContratoResponseDTO(numeroContrato, codigoProduto, valorAtrasado, diasAtraso, sistemaOrigem));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ContratoResponseDTO {
        private String numeroContrato;
        private String codigoProduto;
        private BigDecimal valorAtrasado;
        private int diasAtraso;
        private String sistemaOrigem;
    }
}