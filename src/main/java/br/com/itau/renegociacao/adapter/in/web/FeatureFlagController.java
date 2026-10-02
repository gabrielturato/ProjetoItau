package br.com.itau.renegociacao.adapter.in.web;

import br.com.itau.renegociacao.adapter.in.web.mapper.FeatureFlagWebMapper;
import br.com.itau.renegociacao.adapter.out.featureflag.FeatureFlagEmMemoriaAdapter;
import br.com.itau.renegociacao.application.port.out.Feature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint operacional para ligar/desligar features em tempo de execução (ex.: migração da calculadora).
 */
@RestController
@RequestMapping("/v1/admin/feature-flags")
class FeatureFlagController {

    private final FeatureFlagEmMemoriaAdapter featureFlags;

    FeatureFlagController(FeatureFlagEmMemoriaAdapter featureFlags) {
        this.featureFlags = featureFlags;
    }

    @GetMapping
    Map<String, Boolean> listar() {
        return FeatureFlagWebMapper.paraResponse(featureFlags.todas());
    }

    @PutMapping("/{chave}")
    ResponseEntity<Void> alterar(@PathVariable String chave, @RequestBody AlteracaoFlagDTO alteracao) {
        return Feature.porChave(chave)
                .map(feature -> {
                    featureFlags.alterar(feature, alteracao.isHabilitada());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class AlteracaoFlagDTO {
        private boolean habilitada;
    }
}