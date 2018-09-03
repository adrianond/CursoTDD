package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraServiceTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//se tiver a anota��o abaixo @RunWith(Suite.class) - esta classe nao ser� mais interpretada como uma suite de testes
@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraServiceTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	//Remova se puder!
	
	@BeforeClass
	public static void init(){
		System.out.println("Fa�o algo antes de excutar os testes");
	}
	
	@AfterClass
	public static void aposExecucaoTestes(){
		System.out.println("Fa�o algo ap�s de excutar os testes");
	}
}
