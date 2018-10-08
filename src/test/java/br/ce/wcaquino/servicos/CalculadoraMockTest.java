package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

	@Test
	public void teste(){
		Calculadora calc = Mockito.mock(Calculadora.class);
		//quando houver 2 parametros, se mockar um parametro com o matcher ( Mockito.anyInt()), terá que mockar os demais com matcher, se não mockar os 2 dará erro
		//estou dizendo que no primeiro parametro passo 1 e no segundo qualquer número inteiro e vai retornar 5
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		//erro necessário mockar os dois parametros como demonstrado acima
//		Mockito.when(calc.somar(1, Mockito.anyInt())).thenReturn(5);
		
		//imprimo o método somar, neste caso mockado
		System.out.println(calc.somar(1, 100000));
	}
}
