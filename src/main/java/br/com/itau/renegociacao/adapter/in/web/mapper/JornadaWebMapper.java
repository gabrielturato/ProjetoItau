package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO;
import br.com.itau.renegociacao.domain.model.Jornada;

public final class JornadaWebMapper {

    private JornadaWebMapper() {
    }

    public static JornadaResponseDTO paraResponse(Jornada jornada) {
        return new JornadaResponseDTO(
                jornada.getId().getValor(),
                jornada.getClienteId().getValor(),
                jornada.getStatus().name(),
                jornada.getContratos().stream().map(ContratoWebMapper::paraResponse).toList(),
                jornada.getPolitica().map(PoliticaWebMapper::paraResponse).orElse(null),
                jornada.getSimulacao().map(SimulacaoWebMapper::paraResponse).orElse(null));
    }
}
