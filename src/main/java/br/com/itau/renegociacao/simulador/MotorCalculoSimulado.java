package br.com.itau.renegociacao.simulador;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Matemática usada pelas duas calculadoras simuladas. Ambas compartilham o motor para
 * representar a paridade que se espera entre o legado e a versão modernizada.
 */
final class MotorCalculoSimulado {

    private static final MathContext MC = MathContext.DECIMAL64;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal DOIS = BigDecimal.valueOf(2);
    private static final BigDecimal TAXA_BASE = new BigDecimal("1.49");
    private static final BigDecimal ACRESCIMO_POR_MES_DE_ATRASO = new BigDecimal("0.10");
    private static final BigDecimal TAXA_TETO = new BigDecimal("2.99");
    private static final BigDecimal IOF_DIARIO_PJ = new BigDecimal("0.000041");
    private static final BigDecimal IOF_ADICIONAL = new BigDecimal("0.0038");
    private static final int DIAS_TETO_IOF = 365;

    private MotorCalculoSimulado() {
    }

    @Getter
    @AllArgsConstructor
    static final class Plano {
        private final int quantidadeParcelas;
        private final BigDecimal taxaJurosMensal;
        private final BigDecimal valorIof;
        private final BigDecimal valorParcela;
        private final BigDecimal valorTotal;
    }

    static List<Plano> calcular(boolean sac, int quantidadeMaximaParcelas, BigDecimal saldo, int diasAtraso) {
        BigDecimal taxaPercentual = taxa(diasAtraso);
        BigDecimal taxa = taxaPercentual.divide(CEM, MC);
        return IntStream.rangeClosed(1, quantidadeMaximaParcelas)
                .mapToObj(parcelas -> plano(sac, parcelas, saldo, taxaPercentual, taxa))
                .toList();
    }

    private static BigDecimal taxa(int diasAtraso) {
        return TAXA_BASE.add(ACRESCIMO_POR_MES_DE_ATRASO.multiply(BigDecimal.valueOf(diasAtraso / 30)))
                .min(TAXA_TETO);
    }

    private static Plano plano(boolean sac, int n, BigDecimal saldo, BigDecimal taxaPercentual, BigDecimal i) {
        BigDecimal aliquotaIof = IOF_DIARIO_PJ.multiply(BigDecimal.valueOf(Math.min(n * 30, DIAS_TETO_IOF)))
                .add(IOF_ADICIONAL);
        BigDecimal iof = saldo.multiply(aliquotaIof, MC);
        BigDecimal financiado = saldo.add(iof);

        BigDecimal parcela;
        BigDecimal total;
        if (sac) {
            BigDecimal jurosPrimeiraParcela = financiado.multiply(i, MC);
            parcela = financiado.divide(BigDecimal.valueOf(n), MC).add(jurosPrimeiraParcela);
            total = financiado.add(jurosPrimeiraParcela.multiply(BigDecimal.valueOf(n + 1)).divide(DOIS, MC));
        } else {
            BigDecimal fator = BigDecimal.ONE.add(i).pow(n, MC);
            parcela = financiado.multiply(i).multiply(fator, MC).divide(fator.subtract(BigDecimal.ONE), MC);
            total = moeda(parcela).multiply(BigDecimal.valueOf(n));
        }
        return new Plano(n, taxaPercentual, moeda(iof), moeda(parcela), moeda(total));
    }

    private static BigDecimal moeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}