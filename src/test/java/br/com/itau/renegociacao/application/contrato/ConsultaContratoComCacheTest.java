package br.com.itau.renegociacao.application.contrato;

import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.domain.model.Contrato;
import br.com.itau.renegociacao.domain.model.ContratoId;
import br.com.itau.renegociacao.support.CacheEmMemoria;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.itau.renegociacao.support.Fixtures.contrato;
import static org.assertj.core.api.Assertions.assertThat;

class ConsultaContratoComCacheTest {

    private final Contrato existente = contrato("1");
    private final AtomicInteger consultasNaFonte = new AtomicInteger();
    private final CacheEmMemoria<ContratoId, Contrato> cache = new CacheEmMemoria<>();
    private final ConsultaContratoPort consulta = new ConsultaContratoComCache(id -> {
        consultasNaFonte.incrementAndGet();
        return id.equals(existente.getId()) ? Optional.of(existente) : Optional.empty();
    }, cache);

    @Test
    void segundaConsultaVemDoCache() {
        assertThat(consulta.consultar(existente.getId())).contains(existente);
        assertThat(consulta.consultar(existente.getId())).contains(existente);

        assertThat(consultasNaFonte).hasValue(1);
    }

    @Test
    void naoCacheiaContratoInexistente() {
        ContratoId inexistente = new ContratoId("999", "PJ01");

        assertThat(consulta.consultar(inexistente)).isEmpty();
        assertThat(consulta.consultar(inexistente)).isEmpty();

        assertThat(consultasNaFonte).hasValue(2);
        assertThat(cache.tamanho()).isZero();
    }
}