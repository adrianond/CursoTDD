package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraServiceTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//se tiver a anotação abaixo @RunWith(Suite.class) - esta classe nao será mais interpretada como uma suite de testes
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
		System.out.println("Faço algo antes de excutar os testes");
	}
	
	@AfterClass
	public static void aposExecucaoTestes(){
		System.out.println("Faço algo após de excutar os testes");
	}
}
