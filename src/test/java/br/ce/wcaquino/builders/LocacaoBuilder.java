package br.ce.wcaquino.builders;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;


public class LocacaoBuilder {
	private Locacao locacao;
	
	private Usuario usuario;
	private List<Filme> filmes;
	private Date dataLocacao;
	private Date dataRetorno;
	private Double valor;
	
	public LocacaoBuilder(){
		
	}

	public LocacaoBuilder getLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		builder.locacao = new Locacao();
		builder.locacao.setUsuario(usuario);
		builder.locacao.setFilmes(filmes);
		builder.locacao.setDataLocacao(dataLocacao);
		builder.locacao.setDataRetorno(dataRetorno);
		builder.locacao.setValor(valor);
		return builder;
	}

	public LocacaoBuilder setUsuario(Usuario usuario) {
		this.usuario = usuario;
		return this;
	}

	public LocacaoBuilder setFilmes(List<Filme> filmes) {
		this.filmes = filmes;
		return this;
	}

	public LocacaoBuilder setDataLocacao(Date dataLocacao) {
		this.dataLocacao = dataLocacao;
		return this;
	}

	public LocacaoBuilder setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
		return this;
	}

	public LocacaoBuilder setValor(Double valor) {
		this.valor = valor;
		return this;
	}

	public Locacao build() {
		return locacao;
	}
}
