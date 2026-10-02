package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO.ContratoResponseDTO;
import br.com.itau.renegociacao.adapter.in.web.dto.SimularRenegociacaoRequestDTO.ContratoSelecionadoDTO;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;

final class ContratoWebMapper {

    private ContratoWebMapper() {
    }

    static ContratoId paraDominio(ContratoSelecionadoDTO contrato) {
        return new ContratoId(contrato.getNumeroContrato(), contrato.getCodigoProduto());
    }

    static ContratoResponseDTO paraResponse(Contrato contrato) {
        return new ContratoResponseDTO(contrato.getId().getNumeroContrato(), contrato.getId().getCodigoProduto(),
                contrato.getValorAtrasado(), contrato.getDiasAtraso(), contrato.getSistemaOrigem().name());
    }
}
