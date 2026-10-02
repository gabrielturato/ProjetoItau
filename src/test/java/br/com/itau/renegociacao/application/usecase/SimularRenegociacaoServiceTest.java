package br.com.itau.renegociacao.application.usecase;

import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoCommand;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.domain.exception.ContratoNaoEncontradoException;
import br.com.itau.renegociacao.domain.exception.JornadaNaoEncontradaException;
import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Cnpj;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;
import br.com.itau.renegociacao.domain.model.MotorCalculo;
import br.com.itau.renegociacao.domain.model.SolicitacaoCalculo;
import br.com.itau.renegociacao.domain.model.StatusJornada;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static br.com.itau.renegociacao.support.Fixtures.clienteId;
import static br.com.itau.renegociacao.support.Fixtures.contrato;
import static br.com.itau.renegociacao.support.Fixtures.politica;
import static br.com.itau.renegociacao.support.Fixtures.simulacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimularRenegociacaoServiceTest {

    private static final Cnpj CNPJ = new Cnpj("11222333000181");
    private static final Cnpj OUTRO_CNPJ = new Cnpj("12ABC34501DE35");

    private final ClienteId clienteTokenizado = clienteId();
    private final ClienteId outroClienteTokenizado = clienteId();
    private final Map<ContratoId, Contrato> baseContratos = new HashMap<>();
    private final List<SolicitacaoCalculo> solicitacoesRecebidas = new ArrayList<>();
    private final JornadaRepositoryEmMemoria jornadas = new JornadaRepositoryEmMemoria();

    private final SimularRenegociacaoService service = new SimularRenegociacaoService(
            cnpj -> cnpj.equals(CNPJ) ? clienteTokenizado : outroClienteTokenizado,
            id -> Optional.ofNullable(baseContratos.get(id)),
            jornada -> politica(6),
            solicitacao -> {
                solicitacoesRecebidas.add(solicitacao);
                return simulacao(MotorCalculo.MODERNIZADA, 6);
            },
            jornadas);

    @Test
    void simulaESalvaAJornada() {
        Contrato contrato = cadastrar(contrato("123"));

        Jornada jornada = service.executar(SimularRenegociacaoCommand.novaJornada(CNPJ, List.of(contrato.getId())));

        assertThat(jornada.getStatus()).isEqualTo(StatusJornada.SIMULADA);
        assertThat(jornada.getContratos()).containsExactly(contrato);
        assertThat(jornada.getSimulacao()).get().extracting(s -> s.getPlanos().size()).isEqualTo(6);
        assertThat(jornadas.buscar(jornada.getId())).contains(jornada);
    }

    @Test
    void calculadoraRecebeApenasOClienteTokenizado() {
        Contrato contrato = cadastrar(contrato("123"));

        Jornada jornada = service.executar(SimularRenegociacaoCommand.novaJornada(CNPJ, List.of(contrato.getId())));

        assertThat(solicitacoesRecebidas).singleElement().satisfies(solicitacao -> {
            assertThat(solicitacao.getClienteId()).isEqualTo(clienteTokenizado);
            assertThat(solicitacao.getJornadaId()).isEqualTo(jornada.getId());
            assertThat(solicitacao.getPolitica()).isEqualTo(politica(6));
        });
    }

    @Test
    void falhaQuandoContratoNaoExiste() {
        ContratoId inexistente = new ContratoId("999", "PJ01");

        assertThatThrownBy(() -> service.executar(SimularRenegociacaoCommand.novaJornada(CNPJ, List.of(inexistente))))
                .isInstanceOf(ContratoNaoEncontradoException.class);
        assertThat(solicitacoesRecebidas).isEmpty();
    }

    @Test
    void novaSimulacaoMantemOIdDaJornadaESubstituiOsContratos() {
        Contrato primeiro = cadastrar(contrato("123"));
        Contrato segundo = cadastrar(contrato("456"));
        Jornada original = service.executar(SimularRenegociacaoCommand.novaJornada(CNPJ, List.of(primeiro.getId())));

        Jornada resimulada = service.executar(SimularRenegociacaoCommand.jornadaExistente(
                original.getId(), CNPJ, List.of(primeiro.getId(), segundo.getId())));

        assertThat(resimulada.getId()).isEqualTo(original.getId());
        assertThat(resimulada.getContratos()).containsExactly(primeiro, segundo);
        assertThat(resimulada.getStatus()).isEqualTo(StatusJornada.SIMULADA);
        assertThat(jornadas.buscar(original.getId())).get().satisfies(salva -> assertThat(salva.getContratos()).hasSize(2));
        assertThat(solicitacoesRecebidas).extracting(SolicitacaoCalculo::getJornadaId).containsOnly(original.getId());
    }

    @Test
    void jornadaInexistenteNaoPodeSerSimuladaNovamente() {
        Contrato contrato = cadastrar(contrato("123"));

        assertThatThrownBy(() -> service.executar(
                SimularRenegociacaoCommand.jornadaExistente(JornadaId.nova(), CNPJ, List.of(contrato.getId()))))
                .isInstanceOf(JornadaNaoEncontradaException.class);
    }

    @Test
    void jornadaDeOutroClienteETratadaComoInexistente() {
        Contrato contrato = cadastrar(contrato("123"));
        Jornada original = service.executar(SimularRenegociacaoCommand.novaJornada(CNPJ, List.of(contrato.getId())));

        assertThatThrownBy(() -> service.executar(
                SimularRenegociacaoCommand.jornadaExistente(original.getId(), OUTRO_CNPJ, List.of(contrato.getId()))))
                .isInstanceOf(JornadaNaoEncontradaException.class);
        assertThat(jornadas.buscar(original.getId())).contains(original);
    }

    private Contrato cadastrar(Contrato contrato) {
        baseContratos.put(contrato.getId(), contrato);
        return contrato;
    }

    private static final class JornadaRepositoryEmMemoria implements JornadaRepositoryPort {

        private final Map<JornadaId, Jornada> jornadas = new HashMap<>();

        @Override
        public Jornada salvar(Jornada jornada) {
            jornadas.put(jornada.getId(), jornada);
            return jornada;
        }

        @Override
        public Optional<Jornada> buscar(JornadaId id) {
            return Optional.ofNullable(jornadas.get(id));
        }
    }
}