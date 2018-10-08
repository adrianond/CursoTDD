package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;
	private String nome;
	
	public UsuarioBuilder() {
		
	}
	
	public UsuarioBuilder getUsuario(){
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario();
		builder.usuario.setNome(nome);
		return builder;
	}
	
	public UsuarioBuilder setNome(String nome) {
		this.nome = nome;
		return this;
	}
	
	public Usuario build(){
		return usuario;
	}
}
