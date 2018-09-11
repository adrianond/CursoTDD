package br.ce.wcaquino.dao;

import br.ce.wcaquino.dto.Funcionario;
import br.ce.wcaquino.servicos.Transacao;

public class FuncionarioDaoImpl implements FuncionarioDao {

	Transacao transacao = null;

	void RetornaTransacao(Transacao tx) {
		this.transacao = tx;
	}

	public Funcionario buscarFuncionario(String matricula) throws Exception {
		try {
			String resposta = transacao.executar(matricula);
			return montarFuncionario(resposta);
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private Funcionario montarFuncionario(String resposta) {
		String nome = resposta.substring(0, 10);
		String matricula = resposta.substring(10, 16);
		String setor = resposta.substring(16, 21);
		return new Funcionario(nome.trim(), matricula.trim(), Integer.parseInt(setor.trim()));
	}

}
