package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.ClasseIniciada;

public class ClasseMockeada {

	public void metodo() {

		for (int i = 0; i < 5; i++) {
			ClasseIniciada objeto = new ClasseIniciada();

			objeto.setPropiedad("Propriedade " + i);

			System.out.println(objeto.getPropiedad());

		}

	}
}
