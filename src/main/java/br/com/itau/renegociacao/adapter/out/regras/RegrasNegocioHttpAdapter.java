package br.com.itau.renegociacao.adapter.out.regras;

import br.com.itau.renegociacao.adapter.out.http.ChamadaExterna;
import br.com.itau.renegociacao.application.port.out.RegrasNegocioPort;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class RegrasNegocioHttpAdapter implements RegrasNegocioPort {

    private static final String SERVICO = "regras-negocio";

    private final RestClient restClient;

    public RegrasNegocioHttpAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PoliticaRenegociacao definirPolitica(Jornada jornada) {
        return ChamadaExterna.executar(SERVICO, () -> {
            PoliticaResponseDTO resposta = restClient.post()
                    .uri("/v1/politicas")
                    .body(PoliticaRegrasMapper.paraRequest(jornada))
                    .retrieve()
                    .body(PoliticaResponseDTO.class);
            return PoliticaRegrasMapper.paraDominio(resposta);
        });
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