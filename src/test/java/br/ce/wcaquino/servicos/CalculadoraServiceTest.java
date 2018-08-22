package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class CalculadoraServiceTest {

	private Calculadora calc = null;

	@Before
	public void init() {
		calc = new Calculadora();
	}

	@Test
	public void deveSomarDoisValores() {

		int a = 5;
		int b = 3;
		int resultado = calc.somar(a, b);
		Assert.assertEquals(8, resultado);
	}

	@Test
	public void deveSubtrairDoisValores() {

		int a = 8;
		int b = 5;
		int resultado = calc.subtrair(a, b);

		Assert.assertEquals(3, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoDividirPorZero() throws NaoPodeDividirPorZeroException {

		int a = 8;
		int b = 0;
		int resultado = calc.dividir(a, b);
	}

	@Test
	public void deveLancarExcecaoDividirPorZero2() throws NaoPodeDividirPorZeroException {

		int a = 8;
		int b = 0;
		try {
			int resultado = calc.dividir(a, b);
			// Sen�o lan�ar a exce��o, lan�a uma fail(), e quebra o teste
			Assert.fail();
		} catch (NaoPodeDividirPorZeroException e) {
			assertThat(e.getMessage(), is("N�o � poss�vel dividir por zero"));
			Assert.assertEquals(e.getMessage(), "N�o � poss�vel dividir por zero");
		}
	}
}
