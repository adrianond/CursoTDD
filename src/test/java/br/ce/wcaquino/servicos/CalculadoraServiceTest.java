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
			// Senão lançar a exceção, lança uma fail(), e quebra o teste
			Assert.fail();
		} catch (NaoPodeDividirPorZeroException e) {
			assertThat(e.getMessage(), is("Não é possível dividir por zero"));
			Assert.assertEquals(e.getMessage(), "Não é possível dividir por zero");
		}
	}
}
