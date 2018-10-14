package br.ce.wcaquino.entidades;

public class ClasseIniciada {

	private String propiedad;

	public String getPropiedad() {
		System.out.println("Chama método get ===> " + propiedad);
		return propiedad;
	}

	public void setPropiedad(String propiedad) {
		this.propiedad = propiedad;
	}

	public ClasseIniciada() {
		System.out.println("Chama construtor ===> " + propiedad);
	}

}
