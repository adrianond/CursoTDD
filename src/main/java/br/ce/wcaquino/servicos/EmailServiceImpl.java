package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public class EmailServiceImpl implements EmailService {

	public void notificarAtraso(Usuario usuario) {
		System.out.println("Voce est� com atraso usu�rio: " + usuario);
	}

}
