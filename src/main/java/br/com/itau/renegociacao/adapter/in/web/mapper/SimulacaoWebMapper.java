package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO.SimulacaoResponseDTO;
import br.com.itau.renegociacao.domain.model.Simulacao;

final class SimulacaoWebMapper {

    private SimulacaoWebMapper() {
    }

    static SimulacaoResponseDTO paraResponse(Simulacao simulacao) {
        return new SimulacaoResponseDTO(simulacao.getMotor().name(), simulacao.getCustodia().name(),
                simulacao.getPlanos().stream().map(PlanoParcelamentoWebMapper::paraResponse).toList());
    }
}
