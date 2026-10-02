package br.com.itau.renegociacao.adapter.out.calculadora.modernizada;

import br.com.itau.renegociacao.adapter.out.calculadora.modernizada.CalculadoraModernizadaHttpAdapter.PlanoResponseDTO;
import br.com.itau.renegociacao.domain.model.PlanoParcelamento;

final class PlanoParcelamentoModernizadaMapper {

    private PlanoParcelamentoModernizadaMapper() {
    }

    static PlanoParcelamento paraDominio(PlanoResponseDTO plano) {
        return new PlanoParcelamento(plano.getQuantidadeParcelas(), plano.getTaxaJurosMensal(), plano.getValorIof(),
                plano.getValorParcela(), plano.getValorTotal());
    }
}
