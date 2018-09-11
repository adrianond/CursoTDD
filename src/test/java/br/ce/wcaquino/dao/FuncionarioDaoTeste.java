package br.ce.wcaquino.dao;

import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import br.ce.wcaquino.dto.Funcionario;
import br.ce.wcaquino.servicos.Transacao;

public class FuncionarioDaoTeste {

	@Mock
	Transacao transacao;
	Transacao t;
	@Mock
	FuncionarioDao funcionario;
	FuncionarioDaoImpl f;
	@Mock
	Funcionario func;

	@Before
	public void init() {
//      inicializo os mocks - exemplo: Transacao transacao	
		MockitoAnnotations.initMocks(this);
		f = new FuncionarioDaoImpl();
		t = new Transacao();
	}

	@Test
	public void deveTestarBuscarFuncionario() throws Exception {

		when(transacao.executar("1234")).thenReturn("");
		
		when(funcionario.buscarFuncionario("1234")).thenReturn(func);
		
//		f.RetornaTransacao(t);
//		Funcionario f2 = f.buscarFuncionario("1234");

	}

}
