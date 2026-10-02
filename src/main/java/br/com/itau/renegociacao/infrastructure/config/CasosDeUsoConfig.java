package br.com.itau.renegociacao.infrastructure.config;

import br.com.itau.renegociacao.application.port.in.ConsultarJornadaUseCase;
import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoUseCase;
import br.com.itau.renegociacao.application.port.out.CalculadoraPort;
import br.com.itau.renegociacao.application.port.out.ConsultaContratoPort;
import br.com.itau.renegociacao.application.port.out.JornadaRepositoryPort;
import br.com.itau.renegociacao.application.port.out.RegrasNegocioPort;
import br.com.itau.renegociacao.application.port.out.TokenizacaoClientePort;
import br.com.itau.renegociacao.application.usecase.ConsultarJornadaService;
import br.com.itau.renegociacao.application.usecase.SimularRenegociacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class CasosDeUsoConfig {

    @Bean
    SimularRenegociacaoUseCase simularRenegociacao(TokenizacaoClientePort tokenizacao,
                                                   ConsultaContratoPort consultaContrato,
                                                   RegrasNegocioPort regrasNegocio,
                                                   CalculadoraPort calculadora,
                                                   JornadaRepositoryPort jornadas) {
        return new SimularRenegociacaoService(tokenizacao, consultaContrato, regrasNegocio, calculadora, jornadas);
    }

    @Bean
    ConsultarJornadaUseCase consultarJornada(JornadaRepositoryPort jornadas) {
        return new ConsultarJornadaService(jornadas);
    }
}