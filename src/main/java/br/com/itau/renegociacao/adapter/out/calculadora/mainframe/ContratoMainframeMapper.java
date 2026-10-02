package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import br.com.itau.renegociacao.domain.model.Contrato;

final class ContratoMainframeMapper {

    private ContratoMainframeMapper() {
    }

    static MainframeCopybook.ContratoDTO paraMainframe(Contrato contrato) {
        return new MainframeCopybook.ContratoDTO(contrato.getId().getNumeroContrato(), contrato.getId().getCodigoProduto(),
                contrato.getValorAtrasado(), contrato.getDiasAtraso(), contrato.getSistemaOrigem().name());
    }
}