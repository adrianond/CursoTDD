package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.Mockito;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.dao.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private Usuario user = null;
	private SPCService spc;
	private LocacaoDAO dao;
	private EmailService email;
	private EmailServiceImpl emailImpl;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		service = new LocacaoService();
//		LocacaoDAO dao = new LocacaoDAOFake();
		
		dao = Mockito.mock(LocacaoDAOFake.class);
		spc = Mockito.mock(SPCService.class);
		//user = new Usuario("Usuario 1")
		email = Mockito.mock(EmailService.class);
		emailImpl  = Mockito.mock(EmailServiceImpl.class);

//      usando padrão java builder			
		 user = UsuarioBuilder.umUsuario().agora();
		 service.setLocacaoDao(dao);
		 service.setSPCService(spc);
		 service.setEmailService(email);
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
			service.alugarFilme(null, filmes);
//          Senão lançar a exceção, lança uma fail(), e quebra o teste			
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
			Assert.assertEquals(e.getMessage(), "Usuario vazio");
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException{
		
		exception.expect(LocadoraException.class);
		//exception.expectMessage("Filme vazio");
		
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
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 =UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
//		when(spc.possuiNegativacao(usuario)).thenReturn(true);
//		when(spc.possuiNegativacao(usuario2)).thenReturn(true);
		//qualquer usuário
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		try {
			//acao
//			service.alugarFilme(usuario, filmes);
			service.alugarFilme(usuario2, filmes);
			Assert.fail();
		}catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "Usuário Negativado");
		}
		
//		verify(spc).possuiNegativacao(usuario);
		verify(spc).possuiNegativacao(usuario2);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){
		//cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();

//      gero esse segundo usuário para testar um erro - verificar o usuario notificado de atraso  		
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuario 2").agora();
		
		List<Locacao> locacoes = Arrays.asList(LocacaoBuilder.umLocacao()
					.comUsuario(usuario)
					.comDataRetorno(obterDataComDiferencaDias(2))
					//não está com atraso
//					.comDataRetorno(obterDataComDiferencaDias(-2))
					.agora(),
					//segundo objeto da lista - está notificando este usuário duas vezes 
					 LocacaoBuilder.umLocacao()
					.comUsuario(usuario)
					.comDataRetorno(obterDataComDiferencaDias(2))
					//não está com atraso
//					.comDataRetorno(obterDataComDiferencaDias(-2))
					.agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
	
//      Se a data de retorna não estiver atrasada não notifica o usuário, logo quando chamar verify(email).notificarAtraso(usuario2) 
//		para verificar se usuário foi notificado quebrará o teste, a não ser que eu dirá usando o mockito que tal situação unca debe acontecer - Mockito.never()
		
		//verificacao
//		verify(email).notificarAtraso(usuario2);
		//verify(email, Mockito.never()).notificarAtraso(usuario2);
//		verify(email).notificarAtraso(usuario);
		//usuário foi notificado 2 vezes
		verify(email, Mockito.times(2)).notificarAtraso(usuario);
		//verify(emailImpl, Mockito.times(2)).notificarAtraso(usuario);
		// não verifica se foi notifiacdo nenhum usuário a não ser o usuário 'usuario'
		//Mockito.verifyNoMoreInteractions(email);
		//verifica se foi notificado algum usuario, qualquer usuário da instância de Usuario
		//verify(email, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
	}
}
