package br.com.itau.renegociacao.adapter.out.contrato;

import br.com.itau.renegociacao.adapter.out.http.ChamadaExterna;
import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

public final class ContratoHttpAdapter implements ConsultaContratoPort {

    private static final String SERVICO = "contratos";

    private final RestClient restClient;

    public ContratoHttpAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<Contrato> consultar(ContratoId id) {
        return ChamadaExterna.executar(SERVICO, () -> buscar(id).map(ContratoConsultaMapper::paraDominio));
    }

    private Optional<ContratoResponseDTO> buscar(ContratoId id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/v1/contratos/{numero}/produtos/{produto}", id.getNumeroContrato(), id.getCodigoProduto())
                    .retrieve()
                    .body(ContratoResponseDTO.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
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