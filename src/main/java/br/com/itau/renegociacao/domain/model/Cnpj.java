package br.com.itau.renegociacao.domain.model;

import br.com.itau.renegociacao.domain.exception.CnpjInvalidoException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Locale;

/**
 * CNPJ do cliente final.
 * <p>
 * Existe apenas na borda de entrada: é tokenizado assim que chega e nunca trafega pelo
 * restante do sistema (LGPD). O {@code toString} é mascarado para não vazar em logs,
 * por isso não usa {@code @ToString} do Lombok.
 * <p>
 * Aceita o formato numérico e o alfanumérico (IN RFB 2.229/2024, vigente desde jul/2026):
 * 12 primeiros caracteres em [0-9A-Z] e 2 dígitos verificadores numéricos.
 */
@Getter
@EqualsAndHashCode
public final class Cnpj {

    private static final String FORMATO_ENTRADA = "[0-9A-Za-z./-]+";
    private static final String FORMATO_NORMALIZADO = "[0-9A-Z]{12}[0-9]{2}";

    private final String valor;

    public Cnpj(String valor) {
        if (valor == null || !valor.matches(FORMATO_ENTRADA)) {
            throw new CnpjInvalidoException();
        }
        String normalizado = valor.replaceAll("[./-]", "").toUpperCase(Locale.ROOT);
        if (!valido(normalizado)) {
            throw new CnpjInvalidoException();
        }
        this.valor = normalizado;
    }

    @Override
    public String toString() {
        return "Cnpj[***]";
    }

    private static boolean valido(String cnpj) {
        if (!cnpj.matches(FORMATO_NORMALIZADO) || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(cnpj, 12) == valorDoCaractere(cnpj.charAt(12))
                && digitoVerificador(cnpj, 13) == valorDoCaractere(cnpj.charAt(13));
    }

    private static int digitoVerificador(String cnpj, int tamanho) {
        int soma = 0;
        int peso = tamanho - 7;
        for (int i = 0; i < tamanho; i++) {
            soma += valorDoCaractere(cnpj.charAt(i)) * peso--;
            if (peso < 2) {
                peso = 9;
            }
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /** Regra da RFB: valor ASCII do caractere menos 48 (dígitos mantêm seu valor; 'A' = 17). */
    private static int valorDoCaractere(char caractere) {
        return caractere - '0';
    }
}