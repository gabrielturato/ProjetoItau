package br.com.itau.renegociacao.adapter.out.jornada;

import br.com.itau.renegociacao.application.port.out.CachePort;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;

import java.util.Optional;

/**
 * Para migrar para banco de dados, basta criar outra implementação de {@link JornadaRepositoryPort}
 * e trocar o bean na configuração.
 */
public final class JornadaCacheRepositoryAdapter implements JornadaRepositoryPort {

    private final CachePort<JornadaId, Jornada> cache;

    public JornadaCacheRepositoryAdapter(CachePort<JornadaId, Jornada> cache) {
        this.cache = cache;
    }

    @Override
    public Jornada salvar(Jornada jornada) {
        cache.guardar(jornada.getId(), jornada);
        return jornada;
    }

    @Override
    public Optional<Jornada> buscar(JornadaId id) {
        return cache.buscar(id);
    }
}