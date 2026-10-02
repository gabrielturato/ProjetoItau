package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import br.com.itau.renegociacao.application.exception.IntegracaoException;
import br.com.itau.renegociacao.domain.model.Custodia;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import br.com.itau.renegociacao.domain.model.TipoCalculadora;

/**
 * Anti-Corruption Layer: o formato do mainframe (campos do copybook, códigos de retorno, códigos de tipo)
 * fica contido aqui e nunca alcança o domínio. Ao desligar o mainframe, este pacote é removido inteiro.
 */
final class SimulacaoMainframeMapper {

    private static final String RETORNO_SUCESSO = "00";

    private SimulacaoMainframeMapper() {
    }

    static MainframeCopybook.RequisicaoDTO paraMainframe(SolicitacaoCalculo solicitacao) {
        return new MainframeCopybook.RequisicaoDTO(
                codigoTipo(solicitacao.getPolitica().getTipoCalculadora()),
                solicitacao.getPolitica().getQuantidadeMaximaParcelas(),
                solicitacao.getContratos().stream().map(ContratoMainframeMapper::paraMainframe).toList());
    }

    static Simulacao paraDominio(MainframeCopybook.RespostaDTO resposta) {
        if (!RETORNO_SUCESSO.equals(resposta.getCodigoRetorno())) {
            throw new IntegracaoException(CalculadoraMainframeHttpAdapter.SERVICO,
                    "código de retorno " + resposta.getCodigoRetorno());
        }
        return new Simulacao(MotorCalculo.MAINFRAME, Custodia.valueOf(resposta.getCustodia()),
                resposta.getPlanos().stream().map(PlanoParcelamentoMainframeMapper::paraDominio).toList());
    }

    private static String codigoTipo(TipoCalculadora tipo) {
        return switch (tipo) {
            case PRICE -> "P";
            case SAC -> "S";
        };
    }
}
