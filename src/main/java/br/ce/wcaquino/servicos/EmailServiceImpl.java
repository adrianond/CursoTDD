package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public class EmailServiceImpl implements EmailService {

	public void notificarAtraso(Usuario usuario) {
		System.out.println("Voce está com atraso usuário: " + usuario);
	}

}
