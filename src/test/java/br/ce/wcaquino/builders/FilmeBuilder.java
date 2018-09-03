package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import buildermaster.BuilderMaster;

public class FilmeBuilder {

	private Filme filme;
	
	private FilmeBuilder(){}
	
	public static FilmeBuilder umFilme(){
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(2);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public static FilmeBuilder umFilmeSemEstoque(){
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme();
		builder.filme.setEstoque(0);
		builder.filme.setNome("Filme 1");
		builder.filme.setPrecoLocacao(4.0);
		return builder;
	}
	
	public FilmeBuilder semEstoque(){
		filme.setEstoque(0);
		return this;
	}
	
	public FilmeBuilder setEstoque(){
		filme.setEstoque(1);
		return this;
	}
	
	public FilmeBuilder comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		return this;
	}
	
	public Filme agora(){
		return filme;
	}
	
	public static void main(String[] args) {
		
		FilmeBuilder filme = FilmeBuilder.umFilmeSemEstoque();
		System.out.println(filme.agora().getPrecoLocacao());
		
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
}
