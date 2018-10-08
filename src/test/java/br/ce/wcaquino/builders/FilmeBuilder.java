package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import buildermaster.BuilderMaster;

public class FilmeBuilder {

	private Filme filme;
	private  String nome;
	private  Integer estoque;
	private  Double precoLocacao;
	
	public FilmeBuilder() {
		
	}
	
	public FilmeBuilder getFilme(){
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(estoque);
		builder.filme.setNome(nome);
		builder.filme.setPrecoLocacao(precoLocacao);
		return builder;
	}
	
	public FilmeBuilder setEstoque(Integer estoque){
		this.estoque = estoque;
		return this;
	}
	
	public FilmeBuilder precoLocacao(Double precoLocacao) {
		this.precoLocacao = precoLocacao;
		return this;
	}
	
	public FilmeBuilder precoLocacao(String nome) {
		this.nome = nome;
		return this;
	}
	
	public Filme build(){
		return filme ;
	}
	
	public static void main(String[] args) {
		
		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setPrecoLocacao(5.0);
		System.out.println(filme.getPrecoLocacao());
		
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
}
