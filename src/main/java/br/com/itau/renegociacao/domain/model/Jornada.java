package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.DomainException;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Agregado raiz da renegociação. Imutável: cada transição devolve uma nova instância,
 * o que torna seguro mantê-la em cache e facilita trocar o armazenamento no futuro.
 */
public final class Jornada {

    @Getter
    private final JornadaId id;
    @Getter
    private final ClienteId clienteId;
    @Getter
    private final List<Contrato> contratos;
    private final PoliticaRenegociacao politica;
    private final Simulacao simulacao;

    private Jornada(JornadaId id, ClienteId clienteId, List<Contrato> contratos,
                    PoliticaRenegociacao politica, Simulacao simulacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.contratos = contratos;
        this.politica = politica;
        this.simulacao = simulacao;
    }

    public static Jornada iniciar(JornadaId id, ClienteId clienteId, List<Contrato> contratos) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(clienteId, "clienteId");
        return new Jornada(id, clienteId, validarContratos(contratos), null, null);
    }

    /**
     * Nova rodada de simulação na mesma jornada: mantém o id e o cliente, descarta política e simulação anteriores.
     */
    public Jornada reiniciarSimulacao(List<Contrato> novosContratos) {
        return new Jornada(id, clienteId, validarContratos(novosContratos), null, null);
    }

    public boolean pertenceA(ClienteId cliente) {
        return clienteId.equals(cliente);
    }

    public Jornada comPolitica(PoliticaRenegociacao novaPolitica) {
        Objects.requireNonNull(novaPolitica, "politica");
        if (simulacao != null) {
            throw new DomainException("A política não pode ser alterada após a simulação");
        }
        return new Jornada(id, clienteId, contratos, novaPolitica, null);
    }

    public Jornada comSimulacao(Simulacao novaSimulacao) {
        Objects.requireNonNull(novaSimulacao, "simulacao");
        if (politica == null) {
            throw new DomainException("A jornada precisa de uma política antes de ser simulada");
        }
        boolean excedeLimite = novaSimulacao.getPlanos().stream()
                .anyMatch(plano -> plano.getQuantidadeParcelas() > politica.getQuantidadeMaximaParcelas());
        if (excedeLimite) {
            throw new DomainException("A simulação contém planos acima do limite de parcelas da política");
        }
        return new Jornada(id, clienteId, contratos, politica, novaSimulacao);
    }

    public SolicitacaoCalculo gerarSolicitacaoCalculo() {
        if (politica == null) {
            throw new DomainException("A jornada ainda não possui política de renegociação");
        }
        return new SolicitacaoCalculo(id, clienteId, contratos, politica);
    }

    private static List<Contrato> validarContratos(List<Contrato> contratos) {
        if (contratos == null || contratos.isEmpty()) {
            throw new DomainException("A jornada precisa de ao menos um contrato");
        }
        long distintos = contratos.stream().map(Contrato::getId).distinct().count();
        if (distintos != contratos.size()) {
            throw new DomainException("A jornada não pode conter contratos repetidos");
        }
        return List.copyOf(contratos);
    }

    public StatusJornada getStatus() {
        if (simulacao != null) {
            return StatusJornada.SIMULADA;
        }
        return politica != null ? StatusJornada.POLITICA_DEFINIDA : StatusJornada.INICIADA;
    }

    /** Getter manual: a política só existe a partir de {@link StatusJornada#POLITICA_DEFINIDA}. */
    public Optional<PoliticaRenegociacao> getPolitica() {
        return Optional.ofNullable(politica);
    }

    /** Getter manual: a simulação só existe em {@link StatusJornada#SIMULADA}. */
    public Optional<Simulacao> getSimulacao() {
        return Optional.ofNullable(simulacao);
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Jornada jornada && id.equals(jornada.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}