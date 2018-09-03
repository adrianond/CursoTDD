package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

// classe utilizada para parametrizar os testes - estes testes são chamados de 'Testes Data Driven, ou Testes orintados a dados - são os testes nos quais parametrizamos os dados a serem usados em tais testes'
//Utilizo essa classe para testar dois métodos da LocacaoService.java : devePagar75PctNoFilme3() e naoDevePagarctNoFilme1,
//posso apagar esses testes da classe LocacaoServiceTest.java, mas não apagarei 
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	private LocacaoService service;
	
//  primeiro paramatro do array	que passamos os parametros para o teste - lista de filmes
	@Parameter
	public List<Filme> filmes;
	
//  segundo parametro do array que passamos os parametros para o teste - valor da locação
	@Parameter(value=1)
	public Double valorLocacao;
	
//  terceiro parametro do array que passamos os parametros para o teste - apenas para melhorar a legibilidade
	@Parameter(value=2)
	public String cenario;
	
	@Before
	public void setup(){
		service = new LocacaoService();
	}
	
//  usando o padrão builder	
	private static Filme filme1 = FilmeBuilder.umFilme().setEstoque().agora();
//	private static Filme filme1 = new Filme("Filme 1", 1, 4.0);
	private static Filme filme2 = new Filme("Filme 2", 1, 4.0);
	private static Filme filme3 = new Filme("Filme 3", 1, 4.0);
	private static Filme filme4 = new Filme("Filme 4", 1, 4.0);
	
	

	
//  retorna uma coleção de arrays de objetos	
// @Parameters(name="{2}") - após executar o teste, mostra o valor do terceiro parametro - olhe na vew de resultado de execução do teste
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
//          2 linhas de dados, ou seja, duas situações diferentes faz com que eu execute o teste 2 vezes, logo executa 2 vezes o teste:deveCalcularValorLocacaoConsiderandoDescontos
//          posso inserir mais linhas com mais situações diferentes, aumentando as situaçoes a serem testadas			
			{Arrays.asList(filme1, filme2, filme3), 7.0, "Este terceiro parametro é para melhorar a legibilidade do teste - 25% de desconto no filme 3"},
//			filme 3 após o desconto do primeiro teste não custa mais 4 e sim 3, logo preço diminui mais o segundo teste
			{Arrays.asList(filme1, filme3, filme4), 6.25, "Este terceiro parametro é para melhorar a legibilidade do teste - 100% de desconto no filme 4"},
//          dessa maneira o teste quebra, pois o valor esperado está errado			
//			{Arrays.asList(filme1, filme3, filme4), 8.0, "Este terceiro parametro é para melhorar a legibilidade do teste - 100% de desconto no filme 4"},
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//assertThat(resultado.getValor(), is(valorLocacao));
		System.out.println(cenario);
	}
	
	public static void main(String[] args) {
		Filme filme1 = FilmeBuilder.umFilme().setEstoque().agora();
		System.out.println(filme1.getEstoque());
	}
}
