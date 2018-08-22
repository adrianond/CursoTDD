package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {
    
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public int somar(int a, int b) {
		return  a + b;
		
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public int subtrair(int a, int b) {
		return a - b;
	}

    
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 * @throws NaoPodeDividirPorZeroException
	 */
	public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {

		if (b == 0) {
			throw new NaoPodeDividirPorZeroException("Não é possível dividir por zero");
		} else {
			return a / b;
		}
	}

}
