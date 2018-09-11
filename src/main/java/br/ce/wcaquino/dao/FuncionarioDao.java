package br.ce.wcaquino.dao;

import br.ce.wcaquino.dto.Funcionario;

public interface FuncionarioDao {

	public Funcionario buscarFuncionario(String matricula) throws Exception;

}
