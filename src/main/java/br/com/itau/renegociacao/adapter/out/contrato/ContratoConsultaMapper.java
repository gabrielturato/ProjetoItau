package br.com.itau.renegociacao.adapter.out.contrato;

import br.com.itau.renegociacao.adapter.out.contrato.ContratoHttpAdapter.ContratoResponseDTO;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.SistemaOrigem;

final class ContratoConsultaMapper {

    private ContratoConsultaMapper() {
    }

    static Contrato paraDominio(ContratoResponseDTO resposta) {
        return new Contrato(new ContratoId(resposta.getNumeroContrato(), resposta.getCodigoProduto()),
                resposta.getValorAtrasado(), resposta.getDiasAtraso(), SistemaOrigem.valueOf(resposta.getSistemaOrigem()));
    }
}
