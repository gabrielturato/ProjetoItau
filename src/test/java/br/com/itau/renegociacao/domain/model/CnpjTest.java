package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.CnpjInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CnpjTest {

    @Test
    void normalizaCnpjNumericoFormatado() {
        assertThat(new Cnpj("11.222.333/0001-81").getValor()).isEqualTo("11222333000181");
    }

    @Test
    void aceitaCnpjAlfanumerico() {
        assertThat(new Cnpj("12.abc.345/01de-35").getValor()).isEqualTo("12ABC34501DE35");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "11222333000182", "1122233300018", "00000000000000", "11.222.333/0001-8X", "11 222 333 0001 81"})
    void rejeitaCnpjInvalido(String valor) {
        assertThatThrownBy(() -> new Cnpj(valor)).isInstanceOf(CnpjInvalidoException.class);
    }

    @Test
    void naoExpoeODocumentoNoToString() {
        assertThat(new Cnpj("11222333000181").toString()).doesNotContain("11222333000181");
    }
}