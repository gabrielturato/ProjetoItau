package br.com.itau.renegociacao;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluxo completo, passando por HTTP real até os simuladores dos serviços externos.
 * <p>
 * A porta é escolhida livre antes do contexto subir, e não com RANDOM_PORT, porque as URLs dos
 * simuladores são montadas com {@code ${server.port}} na criação dos RestClients, antes do Tomcat iniciar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RenegociacaoIntegrationTest {

    private static final String CNPJ_A = "11.222.333/0001-81";
    private static final String CNPJ_B = "12.ABC.345/01DE-35";

    @Autowired
    private TestRestTemplate http;

    @DynamicPropertySource
    static void portaLivre(DynamicPropertyRegistry registry) throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            int porta = socket.getLocalPort();
            registry.add("server.port", () -> porta);
        }
    }

    @BeforeEach
    void desligarCalculadoraModernizada() {
        alterarCalculadoraModernizada(false);
    }

    @Test
    void simulaPeloMainframeESalvaNaJornada() {
        ResponseEntity<JsonNode> resposta = simular(CNPJ_A, "1001");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode corpo = resposta.getBody();
        assertThat(corpo.at("/status").asText()).isEqualTo("SIMULADA");
        assertThat(corpo.at("/simulacao/motorCalculo").asText()).isEqualTo("MAINFRAME");
        assertThat(corpo.at("/simulacao/custodia").asText()).isEqualTo("F5");
        assertThat(corpo.at("/simulacao/planos").size()).isEqualTo(corpo.at("/politica/quantidadeMaximaParcelas").asInt());
        assertThat(corpo.toString()).doesNotContain("11222333000181");

        JsonNode jornada = http.getForObject(resposta.getHeaders().getLocation(), JsonNode.class);
        assertThat(jornada.at("/idJornada")).isEqualTo(corpo.at("/idJornada"));
        assertThat(jornada.at("/simulacao")).isEqualTo(corpo.at("/simulacao"));
    }

    @Test
    void featureFlagDirecionaParaModernizadaComParidadeDeValores() {
        JsonNode mainframe = simular(CNPJ_A, "2001").getBody();

        alterarCalculadoraModernizada(true);
        JsonNode modernizada = simular(CNPJ_B, "2001").getBody();

        assertThat(modernizada.at("/simulacao/motorCalculo").asText()).isEqualTo("MODERNIZADA");
        assertThat(modernizada.at("/simulacao/custodia").asText()).isEqualTo("SF");
        assertThat(modernizada.at("/simulacao/planos")).isEqualTo(mainframe.at("/simulacao/planos"));
    }

    @Test
    void simulacaoRepetidaDoMesmoClienteVemDoCache() {
        JsonNode primeira = simular(CNPJ_A, "3001").getBody();
        JsonNode repetida = simular(CNPJ_A, "3001").getBody();

        assertThat(repetida.at("/idJornada")).isNotEqualTo(primeira.at("/idJornada"));
        assertThat(repetida.at("/simulacao")).isEqualTo(primeira.at("/simulacao"));
    }

    @Test
    void trocarDeCalculadoraNaoReaproveitaOCacheDaOutra() {
        simular(CNPJ_A, "3101");

        alterarCalculadoraModernizada(true);
        JsonNode modernizada = simular(CNPJ_A, "3101").getBody();

        alterarCalculadoraModernizada(false);
        JsonNode mainframe = simular(CNPJ_A, "3101").getBody();

        assertThat(modernizada.at("/simulacao/motorCalculo").asText()).isEqualTo("MODERNIZADA");
        assertThat(modernizada.at("/simulacao/custodia").asText()).isEqualTo("SF");
        assertThat(mainframe.at("/simulacao/motorCalculo").asText()).isEqualTo("MAINFRAME");
        assertThat(mainframe.at("/simulacao/custodia").asText()).isEqualTo("F5");
    }

    @Test
    void novaSimulacaoNaJornadaMantemOIdJornada() {
        JsonNode original = simular(CNPJ_A, "6001").getBody();
        String idJornada = original.at("/idJornada").asText();

        ResponseEntity<JsonNode> resposta = simularNovamente(idJornada, CNPJ_A, "6001", "6002");

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().at("/idJornada").asText()).isEqualTo(idJornada);
        assertThat(resposta.getBody().at("/contratos").size()).isEqualTo(2);

        JsonNode salva = http.getForObject("/v1/renegociacoes/jornadas/" + idJornada, JsonNode.class);
        assertThat(salva.at("/contratos")).isEqualTo(resposta.getBody().at("/contratos"));
        assertThat(salva.at("/simulacao")).isEqualTo(resposta.getBody().at("/simulacao"));
    }

    @Test
    void jornadaDeOutroClienteRetorna404() {
        String idJornada = simular(CNPJ_A, "7001").getBody().at("/idJornada").asText();

        assertThat(simularNovamente(idJornada, CNPJ_B, "7001").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void mesmoCnpjGeraSempreOMesmoClienteTokenizado() {
        JsonNode primeira = simular(CNPJ_A, "4001").getBody();
        JsonNode segunda = simular("11222333000181", "4002").getBody();

        assertThat(segunda.at("/idCliente")).isEqualTo(primeira.at("/idCliente"));
    }

    @Test
    void cnpjInvalidoRetorna422() {
        assertThat(simular("11.222.333/0001-82", "5001").getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void contratoInexistenteRetorna404() {
        assertThat(simular(CNPJ_A, "9990001").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void requisicaoSemContratosRetorna400() {
        ResponseEntity<JsonNode> resposta = http.postForEntity("/v1/renegociacoes/simulacoes",
                Map.of("cnpj", CNPJ_A, "contratos", List.of()), JsonNode.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<JsonNode> simular(String cnpj, String numeroContrato) {
        return http.postForEntity("/v1/renegociacoes/simulacoes", corpo(cnpj, numeroContrato), JsonNode.class);
    }

    private ResponseEntity<JsonNode> simularNovamente(String idJornada, String cnpj, String... numerosContrato) {
        return http.postForEntity("/v1/renegociacoes/jornadas/" + idJornada + "/simulacoes",
                corpo(cnpj, numerosContrato), JsonNode.class);
    }

    private static Map<String, Object> corpo(String cnpj, String... numerosContrato) {
        return Map.of(
                "cnpj", cnpj,
                "contratos", Arrays.stream(numerosContrato)
                        .map(numero -> Map.of("numeroContrato", numero, "codigoProduto", "PJ01"))
                        .toList());
    }

    private void alterarCalculadoraModernizada(boolean habilitada) {
        http.exchange("/v1/admin/feature-flags/calculadora-modernizada", HttpMethod.PUT,
                new HttpEntity<>(Map.of("habilitada", habilitada)), Void.class);
    }
}