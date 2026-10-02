package br.com.itau.renegociacao.application.usecase;

import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoCommand;
import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoUseCase;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.application.port.out.RegrasNegocioPort;
import br.com.itau.renegociacao.application.port.out.TokenizacaoClientePort;
import br.com.itau.renegociacao.domain.exception.ContratoNaoEncontradoException;
import br.com.itau.renegociacao.domain.exception.JornadaNaoEncontradaException;
import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;

import java.util.List;
import java.util.Optional;

public final class SimularRenegociacaoService implements SimularRenegociacaoUseCase {

    private final TokenizacaoClientePort tokenizacao;
    private final ConsultaContratoPort consultaContrato;
    private final RegrasNegocioPort regrasNegocio;
    private final CalculadoraPort calculadora;
    private final JornadaRepositoryPort jornadas;

    public SimularRenegociacaoService(TokenizacaoClientePort tokenizacao,
                                      ConsultaContratoPort consultaContrato,
                                      RegrasNegocioPort regrasNegocio,
                                      CalculadoraPort calculadora,
                                      JornadaRepositoryPort jornadas) {
        this.tokenizacao = tokenizacao;
        this.consultaContrato = consultaContrato;
        this.regrasNegocio = regrasNegocio;
        this.calculadora = calculadora;
        this.jornadas = jornadas;
    }

    @Override
    public Jornada executar(SimularRenegociacaoCommand comando) {
        ClienteId clienteId = tokenizacao.tokenizar(comando.getCnpj());
        Optional<Jornada> existente = comando.getJornadaId().map(id -> jornadaDoCliente(id, clienteId));
        List<Contrato> contratos = consultarContratos(comando.getContratos());

        Jornada jornada = existente
                .map(j -> j.reiniciarSimulacao(contratos))
                .orElseGet(() -> Jornada.iniciar(JornadaId.nova(), clienteId, contratos));
        jornada = jornada.comPolitica(regrasNegocio.definirPolitica(jornada));
        jornada = jornada.comSimulacao(calculadora.calcular(jornada.gerarSolicitacaoCalculo()));

        return jornadas.salvar(jornada);
    }

    /** Jornada de outro cliente é tratada como inexistente, para não revelar que ela existe. */
    private Jornada jornadaDoCliente(JornadaId id, ClienteId clienteId) {
        return jornadas.buscar(id)
                .filter(jornada -> jornada.pertenceA(clienteId))
                .orElseThrow(() -> new JornadaNaoEncontradaException(id));
    }

    private List<Contrato> consultarContratos(List<ContratoId> ids) {
        return ids.stream()
                .map(id -> consultaContrato.consultar(id).orElseThrow(() -> new ContratoNaoEncontradoException(id)))
                .toList();
    }
}