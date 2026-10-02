package br.com.itau.renegociacao.application.port.out;

import br.com.itau.renegociacao.domain.model.ClienteId;
import br.com.itau.renegociacao.domain.model.Cnpj;

/**
 * Converte o CNPJ em um identificador opaco (LGPD). O mesmo CNPJ deve sempre gerar o mesmo ClienteId.
 */
public interface TokenizacaoClientePort {

    ClienteId tokenizar(Cnpj cnpj);
}