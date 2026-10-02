package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import br.com.itau.renegociacao.domain.model.PlanoParcelamento;

final class PlanoParcelamentoMainframeMapper {

    private PlanoParcelamentoMainframeMapper() {
    }

    static PlanoParcelamento paraDominio(MainframeCopybook.PlanoDTO plano) {
        return new PlanoParcelamento(plano.getQuantidadeParcelas(), plano.getTaxaJurosMensal(), plano.getValorIof(),
                plano.getValorParcela(), plano.getValorTotal());
    }
}
