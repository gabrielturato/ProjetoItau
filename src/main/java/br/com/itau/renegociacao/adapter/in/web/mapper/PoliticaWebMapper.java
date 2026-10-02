package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO.PoliticaResponseDTO;
import br.com.itau.renegociacao.domain.model.PoliticaRenegociacao;

final class PoliticaWebMapper {

    private PoliticaWebMapper() {
    }

    static PoliticaResponseDTO paraResponse(PoliticaRenegociacao politica) {
        return new PoliticaResponseDTO(politica.getTipoCalculadora().name(), politica.getQuantidadeMaximaParcelas());
    }
}
