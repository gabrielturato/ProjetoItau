package br.com.itau.renegociacao.adapter.out.calculadora.modernizada;

import br.com.itau.renegociacao.adapter.out.calculadora.modernizada.CalculadoraModernizadaHttpAdapter.ContratoRequestDTO;
import br.com.itau.renegociacao.domain.model.Contrato;

final class ContratoModernizadaMapper {

    private ContratoModernizadaMapper() {
    }

    static ContratoRequestDTO paraRequest(Contrato contrato) {
        return new ContratoRequestDTO(contrato.getId().getNumeroContrato(), contrato.getId().getCodigoProduto(),
                contrato.getValorAtrasado(), contrato.getDiasAtraso(), contrato.getSistemaOrigem().name());
    }
}
