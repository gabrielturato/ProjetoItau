package br.com.itau.renegociacao.adapter.in.web;

import br.com.itau.renegociacao.adapter.in.web.dto.JornadaResponseDTO;
import br.com.itau.renegociacao.adapter.in.web.dto.SimularRenegociacaoRequestDTO;
import br.com.itau.renegociacao.adapter.in.web.mapper.JornadaWebMapper;
import br.com.itau.renegociacao.adapter.in.web.mapper.SimularRenegociacaoWebMapper;
import br.com.itau.renegociacao.application.port.in.ConsultarJornadaUseCase;
import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoCommand;
import br.com.itau.renegociacao.application.port.in.SimularRenegociacaoUseCase;
import br.com.itau.renegociacao.domain.model.Jornada;
import br.com.itau.renegociacao.domain.model.JornadaId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/renegociacoes")
class RenegociacaoController {

    private final SimularRenegociacaoUseCase simularRenegociacao;
    private final ConsultarJornadaUseCase consultarJornada;

    RenegociacaoController(SimularRenegociacaoUseCase simularRenegociacao, ConsultarJornadaUseCase consultarJornada) {
        this.simularRenegociacao = simularRenegociacao;
        this.consultarJornada = consultarJornada;
    }

    @PostMapping("/simulacoes")
    ResponseEntity<JornadaResponseDTO> simular(@Valid @RequestBody SimularRenegociacaoRequestDTO request) {
        Jornada jornada = simularRenegociacao.executar(SimularRenegociacaoWebMapper.paraNovaJornada(request));
        URI location = URI.create("/v1/renegociacoes/jornadas/" + jornada.getId().getValor());
        return ResponseEntity.created(location).body(JornadaWebMapper.paraResponse(jornada));
    }

    @PostMapping("/jornadas/{idJornada}/simulacoes")
    JornadaResponseDTO simularNovamente(@PathVariable UUID idJornada,
                                        @Valid @RequestBody SimularRenegociacaoRequestDTO request) {
        SimularRenegociacaoCommand comando =
                SimularRenegociacaoWebMapper.paraJornadaExistente(new JornadaId(idJornada), request);
        return JornadaWebMapper.paraResponse(simularRenegociacao.executar(comando));
    }

    @GetMapping("/jornadas/{idJornada}")
    JornadaResponseDTO consultar(@PathVariable UUID idJornada) {
        return JornadaWebMapper.paraResponse(consultarJornada.consultar(new JornadaId(idJornada)));
    }
}