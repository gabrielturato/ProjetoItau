package br.com.itau.renegociacao.adapter.in.web.mapper;

import br.com.itau.renegociacao.adapter.in.web.dto.SimularRenegociacaoRequestDTO;
import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoCommand;
import br.com.itau.renegociacao.domain.model.Cnpj;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.JornadaId;

import java.util.List;

public final class SimularRenegociacaoWebMapper {

    private SimularRenegociacaoWebMapper() {
    }

    public static SimularRenegociacaoCommand paraNovaJornada(SimularRenegociacaoRequestDTO request) {
        return SimularRenegociacaoCommand.novaJornada(new Cnpj(request.getCnpj()), contratoIds(request));
    }

    public static SimularRenegociacaoCommand paraJornadaExistente(JornadaId jornadaId,
                                                                  SimularRenegociacaoRequestDTO request) {
        return SimularRenegociacaoCommand.jornadaExistente(jornadaId, new Cnpj(request.getCnpj()), contratoIds(request));
    }

    private static List<ContratoId> contratoIds(SimularRenegociacaoRequestDTO request) {
        return request.getContratos().stream().map(ContratoWebMapper::paraDominio).toList();
    }
}
