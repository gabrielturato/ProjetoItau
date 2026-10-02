package br.com.itau.renegociacao.adapter.out.tokenizacao;

import br.com.itau.renegociacao.adapter.out.tokenizacao.TokenizacaoHttpAdapter.TokenRequestDTO;
import br.com.itau.renegociacao.adapter.out.tokenizacao.TokenizacaoHttpAdapter.TokenResponseDTO;
import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Cnpj;

final class ClienteTokenizacaoMapper {

    private ClienteTokenizacaoMapper() {
    }

    static TokenRequestDTO paraRequest(Cnpj cnpj) {
        return new TokenRequestDTO(cnpj.getValor());
    }

    static ClienteId paraDominio(TokenResponseDTO resposta) {
        return new ClienteId(resposta.getToken());
    }
}
