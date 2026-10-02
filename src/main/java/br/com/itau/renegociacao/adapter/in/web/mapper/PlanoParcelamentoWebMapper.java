package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO.PlanoResponseDTO;
import br.com.itau.renegociacao.domain.model.PlanoParcelamento;

final class PlanoParcelamentoWebMapper {

    private PlanoParcelamentoWebMapper() {
    }

    static PlanoResponseDTO paraResponse(PlanoParcelamento plano) {
        return new PlanoResponseDTO(plano.getQuantidadeParcelas(), plano.getTaxaJurosMensal(), plano.getValorIof(),
                plano.getValorParcela(), plano.getValorTotal());
    }
}
