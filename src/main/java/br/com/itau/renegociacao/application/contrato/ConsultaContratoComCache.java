package br.com.itau.renegociacao.application.contrato;

import br.com.itau.renegociacao.application.port.out.CachePort;
import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;

import java.util.Optional;

/**
 * Decorator: consulta primeiro o cache e só vai à fonte quando não encontra.
 * Contrato inexistente não é cacheado, para não esconder um contrato que passe a existir.
 */
public final class ConsultaContratoComCache implements ConsultaContratoPort {

    private final ConsultaContratoPort fonte;
    private final CachePort<ContratoId, Contrato> cache;

    public ConsultaContratoComCache(ConsultaContratoPort fonte, CachePort<ContratoId, Contrato> cache) {
        this.fonte = fonte;
        this.cache = cache;
    }

    @Override
    public Optional<Contrato> consultar(ContratoId id) {
        return cache.buscar(id).or(() -> fonte.consultar(id).map(contrato -> {
            cache.guardar(id, contrato);
            return contrato;
        }));
    }
}