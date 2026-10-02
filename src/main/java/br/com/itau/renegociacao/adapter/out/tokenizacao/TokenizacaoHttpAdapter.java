package br.com.itau.renegociacao.adapter.out.tokenizacao;

import br.com.itau.renegociacao.adapter.out.http.ChamadaExterna;
import br.com.itau.renegociacao.application.port.out.TokenizacaoClientePort;
import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Cnpj;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.client.RestClient;

import java.util.UUID;

public final class TokenizacaoHttpAdapter implements TokenizacaoClientePort {

    private static final String SERVICO = "tokenizacao";

    private final RestClient restClient;

    public TokenizacaoHttpAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ClienteId tokenizar(Cnpj cnpj) {
        return ChamadaExterna.executar(SERVICO, () -> {
            TokenResponseDTO resposta = restClient.post()
                    .uri("/v1/tokens")
                    .body(ClienteTokenizacaoMapper.paraRequest(cnpj))
                    .retrieve()
                    .body(TokenResponseDTO.class);
            return ClienteTokenizacaoMapper.paraDominio(resposta);
        });
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenRequestDTO {
        private String documento;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenResponseDTO {
        private UUID token;
    }
}