package br.com.itau.renegociacao.adapter.out.regras;

import br.com.itau.renegociacao.adapter.out.regras.RegrasNegocioHttpAdapter.ContratoPoliticaDTO;
import br.com.itau.renegociacao.domain.model.Contrato;

final class ContratoRegrasMapper {

    private ContratoRegrasMapper() {
    }

    static ContratoPoliticaDTO paraRequest(Contrato contrato) {
        return new ContratoPoliticaDTO(contrato.getId().getNumeroContrato(), contrato.getId().getCodigoProduto(),
                contrato.getValorAtrasado(), contrato.getDiasAtraso(), contrato.getSistemaOrigem().name());
    }
}
