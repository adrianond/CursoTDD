package br.ce.wcaquino.servicos;



import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.exceptions.SpcServiceException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	/**
	 * A diferen�a entre mock e Spy � que quando mock n�o sabe o que saber,ou seja, se voc� chamar o servi�o desta maneira :
	 * Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
	 * e depois testa - lo com parametros diferentes:
	 * System.out.println("Mock:" + calcMock.somar(3, 2));
	 * o @Mock retornar� a resposta padr�o para aquela classe nesse caso que � um Integer ser� igual 0 ser�  ou uma String vazia  quando for classe String
	 * 
	 * J� o @Spy executar o m�todo com os parametros chamados no teste:
	 * Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
	 * e depois:
	 * System.out.println("Spy:" + calcSpy.somar(2, 2));
	 * retornar� 4, somar� os parametros
	 *  IMPORTANTE - o @Spy n�o funciona com Interface, apenas classes pois ele chama a implementa��o do m�todo
	 *  
	 *  IMPORTANTE - utilizo a anota��o @Spy na inst�ncia do servi�o para que seja poss�vel utilizar os parametros passados 
	 *  no teste 
	 */
	@InjectMocks @Spy
	private LocacaoService service;
	
	@Mock
	private LocacaoService serviceMockado;

	@Mock
	private SPCService spc;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private EmailService email;
	
	@Mock
	private Locacao locacaoMock;
	
	@Mock
	private Usuario usuarioMockado;
	
	private Usuario usuario;
	
	@Mock
	private  List<Filme> filmesMockado;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	@Before
	public void setup(){
		 MockitoAnnotations.initMocks(this);
		 usuario = new UsuarioBuilder().getUsuario().build();
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {

		 Double d = 5.0;	
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 locacao.setDataLocacao(new Date());
		 locacao.setDataRetorno(obterDataComDiferencaDias(2));

		 //desejo que o teste seja realizado com todos parametros de envio mokados, lembre - se que o objeto do m�todo when deve ser mockado
		 when(serviceMockado.alugarFilme(usuarioMockado, filmesMockado, new Date(), 2)).thenReturn(locacao);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		Assert.assertEquals(locacao.getValor(), d);
		Assert.assertTrue((isMesmaData(locacao.getDataLocacao(), new Date())));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		Assert.assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(2)));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(2)), is(true));
	}
	
	@Test
	public void deveTestarDataRetornadaPeloMetodoObterData() throws Exception {
		
		usuario.setNome("Usuario1");
		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setEstoque(1);
		filme.setNome("Filme1");
		filme.setPrecoLocacao(5.0);
		List<Filme> filmes = Arrays.asList(filme);

		 //como service utiliza a anota��o @Spy, poss�vel utilizar os valores passados por parametros no teste
		//moko a data que deve ser retornada pelo m�todo obterData() da classe de servi�o que � chamado quando chama o m�todo alugarFilme()
		 Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();
		
		 //como service utiliza a anota��o @Spy, poss�vel utilizar os valores passados por parametros no teste
		 when(service.obterData()).thenReturn(DataUtils.obterData(28, 4, 2017));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes, new Date(), 2);
			
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
	}
	
	@Test
	public void deveTestarNomeUsuarioSemUsarMockito() throws Exception{
		
		 Usuario user = new Usuario("Usuario1");
		 
		//mocko o objeto Usuario retornado pelo m�todo retornaNomeUsuario()
		 Mockito.doReturn(user).when(service).retornaNomeUsuario();
		 
		 when(service.retornaNomeUsuario()).thenReturn(user);
		
		Usuario usuario = service.retornaNomeUsuario();
		assertEquals(usuario.getNome(), user.getNome());
	}
	
	@Test
	public void naoDeveAlugarFilmeSemEstoque() throws Exception{
		
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));

        //acao
		try {
			 //como preciso validar o lan�amento da exce��o do m�todo alugarFilme(), preciso ter uma instancia da classe LocacaoService e n�o um mock da mesma
			service.alugarFilme(usuarioMockado, filmes, new Date(), 2);
			//Sen�o lan�ar a exce��o, lan�a uma fail(), e quebra o teste			
			Assert.fail();
		} catch (FilmeSemEstoqueException e) {
			assertThat(e.getMessage(), is("Filme vazio"));
			Assert.assertEquals(e.getMessage(), "Filme vazio");
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws Exception{
		
		//acao
		try {
			//como preciso validar o lan�amento da exce��o do m�todo alugarFilme(), preciso ter uma instancia da classe LocacaoService e n�o um mock da mesma
			service.alugarFilme(null, filmesMockado, new Date(), 2);
//          Sen�o lan�ar a exce��o, lan�a uma fail(), e quebra o teste			
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
			Assert.assertEquals(e.getMessage(), "Usuario vazio");
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException, SpcServiceException{
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//como preciso validar o lan�amento da exce��o do m�todo alugarFilme(), preciso ter uma instancia da classe LocacaoService e n�o um mock da mesma
		service.alugarFilme(usuarioMockado, null, new Date(), 2);
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException, SpcServiceException{
		
		 List<Filme> filmes = Arrays.asList(new Filme("Filme 3", 1, 4.0));
		 Double d = 3.0;	
		
		Locacao locacao = service.alugarFilme(usuarioMockado, filmes, new Date(), 2);
		Assert.assertEquals(locacao.getValor(), d);
	}
	
	@Test
	public void naoDevePagarctNoFilme1() throws FilmeSemEstoqueException, LocadoraException, SpcServiceException{
		
		 List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));
		 Double d = 0.0;	
		
		Locacao locacao = service.alugarFilme(usuarioMockado, filmes, new Date(), 2);
		Assert.assertEquals(locacao.getValor(), d);
	}
	
	@Test
	public void deveTestarEntregarDeFilmesAosDomingos() throws FilmeSemEstoqueException, SpcServiceException {
		 //Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SUNDAY));
		 List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 4.0));
		 int dia =  retonarDomingo(new Date());
		 
		
		try {
			service.alugarFilme(usuarioMockado, filmes, new Date(),dia);
			Assert.fail();
		}catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "N�o � poss�vel entregar filmes ao Domingos");
		}
	}
	
	@Test
	public void devoDevolverFilmeNoDiaSeguinte() throws FilmeSemEstoqueException, LocadoraException, SpcServiceException {
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setDataLocacao(new Date());
		 locacao.setDataRetorno(obterDataComDiferencaDias(1));
		 
		 when(serviceMockado.alugarFilme(usuarioMockado, filmesMockado, new Date(), 2)).thenReturn(locacao);
      
		//UTILIZO MEU PR�PRIO MACHER PARA O TESTE - locacao.getDataRetorno() - fixo no c�digo = d+1        
        // assertThat(locacao.getDataRetorno(), MatchersProprios.caiEm(Calendar.SATURDAY));
        
        //soma mais um dia        
        assertThat(locacao.getDataRetorno(), MatchersProprios.addDias(1));
        assertThat(locacao.getDataLocacao(), MatchersProprios.hoje(0));
	}
	
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		
		usuario.setNome("Usuario1");
		Usuario usuario2 = new UsuarioBuilder().getUsuario().build();
		usuario2.setNome("Usuario2");
		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setEstoque(1);
		filme.setNome("Filme1");
		filme.setPrecoLocacao(4.0);
		
		List<Filme> filmes = Arrays.asList(filme);
		
		when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
		//when(spc.possuiNegativacao(usuario2)).thenReturn(true);
		
		//qualquer usu�rio
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes, new Date(), 2);
			Assert.fail();
		}catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "Usu�rio Negativado");
		}
		
		verify(spc).possuiNegativacao(usuario);
		//verify(spc).possuiNegativacao(usuario2);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){

		usuario.setNome("Usuario1");
		usuario.setNome("Usuario2");
		//gero esse segundo usu�rio para testar um erro - verificar o usuario notificado de atraso  		
		Usuario usuario2 = new UsuarioBuilder().getUsuario().build();
		usuario2.setNome("Usuario2");
		
		List<Locacao> locacoes = gerarLocacaoComAtraso(usuario);
		//List<Locacao> locacoes = gerarLocacaoSemAtraso(usuario2);
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
	
		/**
		 * Se a data de retorno n�o estiver atrasada n�o notifica o usu�rio, logo quando chamar verify(email).notificarAtraso(usuario2) 
		 *para verificar se usu�rio foi notificado quebrar� o teste, a n�o ser que eu dir� usando o mockito que tal situa��o nunca debe acontecer - Mockito.never()
		 */
        //verificacao
		verify(email).notificarAtraso(usuario);
		//verify(email, Mockito.never()).notificarAtraso(usuario2);
		//verify(email2).notificarAtraso(usuario2);
		
		//usu�rio foi notificado 2 vezes
		//verify(email, Mockito.times(2)).notificarAtraso(usuario);
		
		//verifica se foi enviado mais de um e mail, para o caso de haver mais de uma loca��o com atraso para o usu�rio 
		//verify(emailImpl, Mockito.times(2)).notificarAtraso(usuario);
		
		// n�o verifica se foi notifiacdo nenhum usu�rio a n�o ser o usu�rio 'usuario'
		Mockito.verifyNoMoreInteractions(email);
		
		//para garantir que o servi�o spc nunca ser� chamado na execu��o deste cen�rio, apesar que este teste nem chama tal servi�o
		Mockito.verifyZeroInteractions(spc);
		
		//verifica se foi notificado algum usuario, qualquer usu�rio da inst�ncia de Usuario
		//verify(email, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email, Mockito.times(1)).notificarAtraso(Mockito.any(Usuario.class));
	}
	
	@Test
	public void deveTratarErronoSPC() throws SpcServiceException, FilmeSemEstoqueException, LocadoraException {
		usuario.setNome("Usuario1");
		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setEstoque(1);
		filme.setNome("Filme1");
		filme.setPrecoLocacao(4.0);
		
		List<Filme> filmes = Arrays.asList(filme);
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new SpcServiceException("Falha catastr�fica"));
		
		//verificacao
		exception.expect(SpcServiceException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes, new Date(), 2);
		
	}
	
	@Test
	public void deveProrrogarUmaLocacao(){
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setDataLocacao(new Date());
		 locacao.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao.setValor(12.0);
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//captura os argumentos enviados para o m�todo salvar, no caso um objeto Loca��o
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(36.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), MatchersProprios.hoje(0));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(2)), is(true));
	}
	
	@Test
	public void deveTestarMetodoPrivadoSemPowerMock() throws Exception {

		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setEstoque(1);
		filme.setNome("Filme1");
		filme.setPrecoLocacao(4.0);
		Filme filme2 = new FilmeBuilder().getFilme().build();
		filme2.setEstoque(2);
		filme2.setNome("Filme2");
		filme2.setPrecoLocacao(4.0);
		

		List<Filme> filmes = Arrays.asList(filme, filme2);

		// uso a classe reflect do java para mokar a chamada de um m�todo private
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorTotal", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
        
		// verificacao
		Assert.assertThat(valor, is(8.0));
	}
	
	private List<Locacao> gerarDuasLocacoesSemAtraso(Usuario usuario){
		
		 Locacao locacao1 = new LocacaoBuilder().getLocacao().build();
		 locacao1.setValor(5.0);
		 //loca��o foi ontem
		 locacao1.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias ap�s a loca��o
		 locacao1.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao1.setUsuario(usuario);
		 
		 Locacao locacao2 = new LocacaoBuilder().getLocacao().build();
		 locacao2.setValor(5.0);
		 //loca��o foi ontem
		 locacao2.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias ap�s a loca��o
		 locacao2.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao2.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao1, locacao2);
		 return locacoes;
	}
	
	private List<Locacao> gerarDuasLocacoesComAtraso(Usuario usuario){
		
		 Locacao locacao1 = new LocacaoBuilder().getLocacao().build();
		 locacao1.setValor(5.0);
		 //loca��o foi ontem
		 locacao1.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno est� sendo realizado com atraso
		 locacao1.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao1.setUsuario(usuario);
		 
		 Locacao locacao2 = new LocacaoBuilder().getLocacao().build();
		 locacao2.setValor(5.0);
		 //loca��o foi ontem
		 locacao2.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno est� sendo realizado com atraso
		 locacao2.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao2.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao1, locacao2);
		 return locacoes;
	}
	
	private List<Locacao> gerarLocacaoComAtraso(Usuario usuario){
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 //loca��o foi ontem
		 locacao.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno est� sendo realizado com atraso
		 locacao.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao);
		 return locacoes;
	}
	
	private List<Locacao> gerarLocacaoSemAtraso(Usuario usuario){
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 //loca��o foi ontem
		 locacao.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias ap�s a loca��o
		 locacao.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao);
		 return locacoes;
	}
	
	private int retonarDomingo(Date data) {

		int dia_semana = DataUtils.retornarDiaSemana(data);
		int dia = 0;
		
		switch (dia_semana) {
		//domingo
		case 1:
			dia = 0;
			break;
		//segunda	
		case 2:
			dia = 6;
			break;
		//ter�a	
		case 3:
			dia = 5;
			break;
		//quarta	
		case 4:
			dia = 4;
			break;
		//quinta	
        case 5:
        	dia = 3;
			break;
		//sexta	
        case 6:
        	dia = 2;
			break;
		//s�bado	
        case 7:
        	dia = 1;
			break;
		default:
			System.out.println("Data Inv�lida");
		}
		return dia;
	}
}
