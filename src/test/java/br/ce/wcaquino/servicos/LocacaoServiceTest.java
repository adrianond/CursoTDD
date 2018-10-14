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
	 * A diferença entre mock e Spy é que quando mock não sabe o que saber,ou seja, se você chamar o serviço desta maneira :
	 * Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
	 * e depois testa - lo com parametros diferentes:
	 * System.out.println("Mock:" + calcMock.somar(3, 2));
	 * o @Mock retornará a resposta padrão para aquela classe nesse caso que é um Integer será igual 0 será  ou uma String vazia  quando for classe String
	 * 
	 * Já o @Spy executar o método com os parametros chamados no teste:
	 * Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
	 * e depois:
	 * System.out.println("Spy:" + calcSpy.somar(2, 2));
	 * retornará 4, somará os parametros
	 *  IMPORTANTE - o @Spy nâo funciona com Interface, apenas classes pois ele chama a implementação do método
	 *  
	 *  IMPORTANTE - utilizo a anotação @Spy na instância do serviço para que seja possível utilizar os parametros passados 
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

		 //desejo que o teste seja realizado com todos parametros de envio mokados, lembre - se que o objeto do método when deve ser mockado
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

		 //como service utiliza a anotação @Spy, possível utilizar os valores passados por parametros no teste
		//moko a data que deve ser retornada pelo método obterData() da classe de serviço que é chamado quando chama o método alugarFilme()
		 Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).obterData();
		
		 //como service utiliza a anotação @Spy, possível utilizar os valores passados por parametros no teste
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
		 
		//mocko o objeto Usuario retornado pelo método retornaNomeUsuario()
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
			 //como preciso validar o lançamento da exceção do método alugarFilme(), preciso ter uma instancia da classe LocacaoService e não um mock da mesma
			service.alugarFilme(usuarioMockado, filmes, new Date(), 2);
			//Senão lançar a exceção, lança uma fail(), e quebra o teste			
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
			//como preciso validar o lançamento da exceção do método alugarFilme(), preciso ter uma instancia da classe LocacaoService e não um mock da mesma
			service.alugarFilme(null, filmesMockado, new Date(), 2);
//          Senão lançar a exceção, lança uma fail(), e quebra o teste			
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
		
		//como preciso validar o lançamento da exceção do método alugarFilme(), preciso ter uma instancia da classe LocacaoService e não um mock da mesma
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
			Assert.assertEquals(e.getMessage(), "Não é possível entregar filmes ao Domingos");
		}
	}
	
	@Test
	public void devoDevolverFilmeNoDiaSeguinte() throws FilmeSemEstoqueException, LocadoraException, SpcServiceException {
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setDataLocacao(new Date());
		 locacao.setDataRetorno(obterDataComDiferencaDias(1));
		 
		 when(serviceMockado.alugarFilme(usuarioMockado, filmesMockado, new Date(), 2)).thenReturn(locacao);
      
		//UTILIZO MEU PRÓPRIO MACHER PARA O TESTE - locacao.getDataRetorno() - fixo no código = d+1        
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
		
		//qualquer usuário
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes, new Date(), 2);
			Assert.fail();
		}catch (LocadoraException e) {
			Assert.assertEquals(e.getMessage(), "Usuário Negativado");
		}
		
		verify(spc).possuiNegativacao(usuario);
		//verify(spc).possuiNegativacao(usuario2);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){

		usuario.setNome("Usuario1");
		usuario.setNome("Usuario2");
		//gero esse segundo usuário para testar um erro - verificar o usuario notificado de atraso  		
		Usuario usuario2 = new UsuarioBuilder().getUsuario().build();
		usuario2.setNome("Usuario2");
		
		List<Locacao> locacoes = gerarLocacaoComAtraso(usuario);
		//List<Locacao> locacoes = gerarLocacaoSemAtraso(usuario2);
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
	
		/**
		 * Se a data de retorno não estiver atrasada não notifica o usuário, logo quando chamar verify(email).notificarAtraso(usuario2) 
		 *para verificar se usuário foi notificado quebrará o teste, a não ser que eu dirá usando o mockito que tal situação nunca debe acontecer - Mockito.never()
		 */
        //verificacao
		verify(email).notificarAtraso(usuario);
		//verify(email, Mockito.never()).notificarAtraso(usuario2);
		//verify(email2).notificarAtraso(usuario2);
		
		//usuário foi notificado 2 vezes
		//verify(email, Mockito.times(2)).notificarAtraso(usuario);
		
		//verifica se foi enviado mais de um e mail, para o caso de haver mais de uma locação com atraso para o usuário 
		//verify(emailImpl, Mockito.times(2)).notificarAtraso(usuario);
		
		// não verifica se foi notifiacdo nenhum usuário a não ser o usuário 'usuario'
		Mockito.verifyNoMoreInteractions(email);
		
		//para garantir que o serviço spc nunca será chamado na execução deste cenário, apesar que este teste nem chama tal serviço
		Mockito.verifyZeroInteractions(spc);
		
		//verifica se foi notificado algum usuario, qualquer usuário da instância de Usuario
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
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new SpcServiceException("Falha catastrófica"));
		
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
		
		//captura os argumentos enviados para o método salvar, no caso um objeto Locação
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

		// uso a classe reflect do java para mokar a chamada de um método private
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
		 //locação foi ontem
		 locacao1.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias após a locação
		 locacao1.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao1.setUsuario(usuario);
		 
		 Locacao locacao2 = new LocacaoBuilder().getLocacao().build();
		 locacao2.setValor(5.0);
		 //locação foi ontem
		 locacao2.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias após a locação
		 locacao2.setDataRetorno(obterDataComDiferencaDias(2));
		 locacao2.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao1, locacao2);
		 return locacoes;
	}
	
	private List<Locacao> gerarDuasLocacoesComAtraso(Usuario usuario){
		
		 Locacao locacao1 = new LocacaoBuilder().getLocacao().build();
		 locacao1.setValor(5.0);
		 //locação foi ontem
		 locacao1.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno está sendo realizado com atraso
		 locacao1.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao1.setUsuario(usuario);
		 
		 Locacao locacao2 = new LocacaoBuilder().getLocacao().build();
		 locacao2.setValor(5.0);
		 //locação foi ontem
		 locacao2.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno está sendo realizado com atraso
		 locacao2.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao2.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao1, locacao2);
		 return locacoes;
	}
	
	private List<Locacao> gerarLocacaoComAtraso(Usuario usuario){
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 //locação foi ontem
		 locacao.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno está sendo realizado com atraso
		 locacao.setDataRetorno(obterDataComDiferencaDias(3));
		 locacao.setUsuario(usuario);
		
		 List<Locacao> locacoes = Arrays.asList(locacao);
		 return locacoes;
	}
	
	private List<Locacao> gerarLocacaoSemAtraso(Usuario usuario){
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 //locação foi ontem
		 locacao.setDataLocacao(obterDataComDiferencaDias(-1));
		//retorno deve ser realizado 2 dias após a locação
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
		//terça	
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
		//sábado	
        case 7:
        	dia = 1;
			break;
		default:
			System.out.println("Data Inválida");
		}
		return dia;
	}
}
