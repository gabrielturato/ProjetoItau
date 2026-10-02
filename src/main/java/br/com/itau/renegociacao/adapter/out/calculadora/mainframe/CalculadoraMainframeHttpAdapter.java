package br.com.itau.renegociacao.adapter.out.calculadora.mainframe;

import br.com.itau.renegociacao.adapter.out.http.ChamadaExterna;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.domain.model.Simulacao;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import org.springframework.web.client.RestClient;

public final class CalculadoraMainframeHttpAdapter implements CalculadoraPort {

    static final String SERVICO = "calculadora-mainframe";

    private final RestClient restClient;

    public CalculadoraMainframeHttpAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Simulacao calcular(SolicitacaoCalculo solicitacao) {
        return ChamadaExterna.executar(SERVICO, () -> {
            MainframeCopybook.RespostaDTO resposta = restClient.post()
                    .uri("/v1/calculos")
                    .body(SimulacaoMainframeMapper.paraMainframe(solicitacao))
                    .retrieve()
                    .body(MainframeCopybook.RespostaDTO.class);
            return SimulacaoMainframeMapper.paraDominio(resposta);
        });
    }
}
