package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.SpcServiceException;

public interface SPCService {

	public boolean possuiNegativacao(Usuario usuario) throws SpcServiceException;
}
