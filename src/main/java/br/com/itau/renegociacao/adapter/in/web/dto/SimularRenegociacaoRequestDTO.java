package br.com.itau.renegociacao.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimularRenegociacaoRequestDTO {

    @NotBlank
    private String cnpj;

    @NotEmpty
    @Size(max = 50)
    private List<@Valid ContratoSelecionadoDTO> contratos;

    /** Escrito à mão para mascarar o CNPJ (LGPD). */
    @Override
    public String toString() {
        return "SimularRenegociacaoRequestDTO[cnpj=***, contratos=" + contratos + "]";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ContratoSelecionadoDTO {

        @NotBlank
        private String numeroContrato;

        @NotBlank
        private String codigoProduto;
    }
}