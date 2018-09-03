package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private Usuario user = null;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		service = new LocacaoService();
		//user = new Usuario("Usuario 1");

//      usando padrão java builder			
		 user = UsuarioBuilder.umUsuario().agora();
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		List<Filme> filmes = Arrays.asList(new Filme("Filme 5", 1, 5.0));
		
		//acao
		Locacao locacao = service.alugarFilme(user, filmes);
		Double d = 5.0;	
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		Assert.assertEquals(locacao.getValor(), d);
		Assert.assertTrue((isMesmaData(locacao.getDataLocacao(), new Date())));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		Assert.assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception{
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));
		
		//acao
		service.alugarFilme(user, filmes);
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		//acao
		try {
			service.alugarFilme(user, filmes);
//          Senão lançar a exceção, lança uma fail(), e quebra o teste			
			//Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
			Assert.assertEquals(e.getMessage(), "Usuario vazio");
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException{
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(user, null);
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException{
		
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0), new Filme("Filme 2", 1, 4.0), new Filme("Filme 3", 1, 4.0));
		Double d = 7.0;	
		
		Locacao locacao = service.alugarFilme(user, filmes);
		Assert.assertEquals(locacao.getValor(), d);
	}
	
	@Test
	public void naoDevePagarctNoFilme1() throws FilmeSemEstoqueException, LocadoraException{
		
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0), new Filme("Filme 2", 1, 4.0), new Filme("Filme 3", 1, 4.0));
		Double d = 7.0;	
		
		Locacao locacao = service.alugarFilme(user, filmes);
		Assert.assertEquals(locacao.getValor(), d);
	}
	
	@Test
	public void deveTestarEntregarDeFilmesAosDomingos() throws FilmeSemEstoqueException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SUNDAY));
		
		List<Filme> filmes = Arrays.asList(new Filme("Filme 2", 1, 5.0));
		
		try {
			service.alugarFilme(user, filmes);
			Assert.fail();
		}catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "Não é possível entregar filmes ao Domingos");
		}
	}
	
	@Test
	public void devoDevolverFilmeNoDiaSeguinte() throws FilmeSemEstoqueException, LocadoraException {
		List<Filme> filmes = Arrays.asList(new Filme("Filme 2", 1, 5.0));
		
        Locacao retorno = service.alugarFilme(user, filmes);
//      UTILIZO MEU PRÓPRIO MACHER PARA O TESTE      
//      retorno.getDataRetorno() - fixo no código = d+1        
        //assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.THURSDAY));
       // assertThat(retorno.getDataRetorno(), MatchersProprios.caiDiaSeguinte());
//        assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.FRIDAY));
        
//       soma mais um dia        
        assertThat(retorno.getDataRetorno(), MatchersProprios.hojeMaiUmDia(1));
        
        assertThat(retorno.getDataLocacao(), MatchersProprios.hoje(0));
	}
	
	@Test
	public void forcarErroParaUsarMetodoDescribeTo() throws FilmeSemEstoqueException, LocadoraException {
		List<Filme> filmes = Arrays.asList(new Filme("Filme 2", 1, 5.0));
		
        Locacao retorno = service.alugarFilme(user, filmes);
//      UTILIZO MEU PRÓPRIO MACHER PARA O TESTE      
//      retorno.getDataRetorno() - fixo no código = d+1 /segundo parametro ja passo do dia seguinte    
//         assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.FRIDAY));
        
       
	}
}
