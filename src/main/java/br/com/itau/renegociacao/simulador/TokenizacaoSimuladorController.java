package br.com.itau.renegociacao.simulador;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/simuladores/tokenizacao/v1/tokens")
@ConditionalOnProperty(name = "simuladores.habilitados", havingValue = "true")
class TokenizacaoSimuladorController {

    private final Map<String, UUID> cofre = new ConcurrentHashMap<>();

    @PostMapping
    TokenResponseDTO tokenizar(@RequestBody TokenRequestDTO request) {
        return new TokenResponseDTO(cofre.computeIfAbsent(request.getDocumento(), documento -> UUID.randomUUID()));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenRequestDTO {
        private String documento;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenResponseDTO {
        private UUID token;
    }
}