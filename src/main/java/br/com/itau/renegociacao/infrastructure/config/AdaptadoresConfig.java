package br.com.itau.renegociacao.infrastructure.config;

import br.com.itau.renegociacao.adapter.out.cache.CaffeineCacheAdapter;
import br.com.itau.renegociacao.adapter.out.calculadora.mainframe.CalculadoraMainframeHttpAdapter;
import br.com.itau.renegociacao.adapter.out.calculadora.modernizada.CalculadoraModernizadaHttpAdapter;
import br.com.itau.renegociacao.adapter.out.contrato.ContratoHttpAdapter;
import br.com.itau.renegociacao.adapter.out.featureflag.FeatureFlagEmMemoriaAdapter;
import br.com.itau.renegociacao.adapter.out.jornada.JornadaCacheRepositoryAdapter;
import br.com.itau.renegociacao.adapter.out.regras.RegrasNegocioHttpAdapter;
import br.com.itau.renegociacao.adapter.out.tokenizacao.TokenizacaoHttpAdapter;
import br.com.itau.renegociacao.application.calculadora.CalculadoraComCache;
import br.com.itau.renegociacao.application.calculadora.CalculadoraRoteadaPorFeatureFlag;
import br.com.itau.renegociacao.application.calculadora.ChaveSimulacao;
import br.com.itau.renegociacao.application.contrato.ConsultaContratoComCache;
import br.com.itau.renegociacao.application.port.out.CachePort;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.application.port.out.FeatureFlagPort;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.application.port.out.RegrasNegocioPort;
import br.com.itau.renegociacao.application.port.out.TokenizacaoClientePort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Único ponto que conhece as implementações concretas. Trocar Caffeine por banco, ou HTTP por
 * mensageria, é uma mudança restrita a este arquivo.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RenegociacaoProperties.class)
class AdaptadoresConfig {

    private final RenegociacaoProperties propriedades;
    private final RestClient.Builder restClientBuilder;

    AdaptadoresConfig(RenegociacaoProperties propriedades, RestClient.Builder restClientBuilder) {
        this.propriedades = propriedades;
        this.restClientBuilder = restClientBuilder;
    }

    @Bean
    TokenizacaoClientePort tokenizacaoCliente() {
        return new TokenizacaoHttpAdapter(restClient("tokenizacao"));
    }

    @Bean
    ConsultaContratoPort consultaContrato() {
        return new ConsultaContratoComCache(new ContratoHttpAdapter(restClient("contratos")), cache("contratos"));
    }

    @Bean
    RegrasNegocioPort regrasNegocio() {
        return new RegrasNegocioHttpAdapter(restClient("regras-negocio"));
    }

    @Bean
    FeatureFlagEmMemoriaAdapter featureFlags() {
        return new FeatureFlagEmMemoriaAdapter(propriedades.getFeatureFlags());
    }

    /**
     * simulacoes (por cliente + motor ativo)
     *   -> roteador por feature flag
     *        -> modernizada: cache por parâmetros -> HTTP
     *        -> legada: mainframe HTTP
     */
    @Bean
    CalculadoraPort calculadora(FeatureFlagPort featureFlags) {
        CalculadoraPort legada = new CalculadoraMainframeHttpAdapter(restClient("calculadora-mainframe"));
        CalculadoraPort modernizada = new CalculadoraComCache(
                new CalculadoraModernizadaHttpAdapter(restClient("calculadora-modernizada")),
                cache("calculadora-modernizada"),
                ChaveSimulacao::porParametros);
        CalculadoraRoteadaPorFeatureFlag roteada = new CalculadoraRoteadaPorFeatureFlag(modernizada, legada, featureFlags);
        return new CalculadoraComCache(roteada, cache("simulacoes"),
                solicitacao -> ChaveSimulacao.porCliente(solicitacao, roteada.motorAtivo()));
    }

    @Bean
    JornadaRepositoryPort jornadaRepository() {
        return new JornadaCacheRepositoryAdapter(cache("jornadas"));
    }

    private <K, V> CachePort<K, V> cache(String nome) {
        RenegociacaoProperties.Cache configuracao = propriedades.cache(nome);
        return new CaffeineCacheAdapter<>(configuracao.getTtl(), configuracao.getTamanhoMaximo());
    }

    private RestClient restClient(String nome) {
        RenegociacaoProperties.Integracao integracao = propriedades.integracao(nome);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(integracao.getTimeout());
        requestFactory.setReadTimeout(integracao.getTimeout());
        return restClientBuilder.clone()
                .baseUrl(integracao.getUrl())
                .requestFactory(requestFactory)
                .build();
    }
}