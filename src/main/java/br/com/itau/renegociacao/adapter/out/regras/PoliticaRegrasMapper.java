package br.com.itau.renegociacao.adapter.out.regras;

import br.com.itau.renegociacao.adapter.out.regras.RegrasNegocioHttpAdapter.PoliticaRequestDTO;
import br.com.itau.renegociacao.adapter.out.regras.RegrasNegocioHttpAdapter.PoliticaResponseDTO;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;
import br.com.itau.renegociacao.domain.model.TipoCalculadora;

final class PoliticaRegrasMapper {

    private PoliticaRegrasMapper() {
    }

    static PoliticaRequestDTO paraRequest(Jornada jornada) {
        return new PoliticaRequestDTO(jornada.getId().getValor(), jornada.getClienteId().getValor(),
                jornada.getContratos().stream().map(ContratoRegrasMapper::paraRequest).toList());
    }

    static PoliticaRenegociacao paraDominio(PoliticaResponseDTO resposta) {
        return new PoliticaRenegociacao(TipoCalculadora.valueOf(resposta.getTipoCalculadora()),
                resposta.getQuantidadeMaximaParcelas());
    }
}
