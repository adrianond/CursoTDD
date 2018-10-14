package br.ce.wcaquino.servicos;




import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

/**
 * Para algumas operações que o mockito não consegue trabalhar como mockar o construtor de um objeto ou alterar o comportamento 
 * de algum método estatico ou métodos privados, posso utilizar o PowerMock
 * RunWith(PowerMockRunner.class) - testes serão gerenciados pelo PowerMock
 * 
 * @PrepareForTest({LocacaoServicePowerMockito.class, DataUtils.class}) - PowerMock prepara a classe LocacaoService para o teste, irá mockar a instância sem parametros da classe Date
 * @author nss_admin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoServicePowerMockito.class})
public class LocacaoServicePowerMockTest {

	@InjectMocks
	private LocacaoServicePowerMockito service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
	
	private Usuario usuario;
	
	@Mock
	private Usuario user;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		usuario = new UsuarioBuilder().getUsuario().build();
		//cria o spy do PowerMock
		service = PowerMockito.spy(service);
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception{
		
		usuario.setNome("Usuario1");
		Filme filme = new FilmeBuilder().getFilme().build();
		filme.setEstoque(1);
		filme.setNome("Filme1");
		filme.setPrecoLocacao(4.0);
		List<Filme> filmes = Arrays.asList(filme);
		
		//mocko o objeto Date na classe LocacaoServicePowerMockito
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(14, 10, 2018));

		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		assertThat(retorno.getDataRetorno(), MatchersProprios.addDias(1));
		//verifico se o construtor Date realmente foi chamado e como no método alugarFilme ele é cahamado 2x, logo informo isso no verify
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}
	
	@Test
	public void deveTestarNomeUsuario() throws Exception{
		
		 String nome = "Usuario1";
		 
		//mocko o objeto Usuario na classe LocacaoServicePowerMockito
		doReturn(nome).when(user).getNome();
		PowerMockito.whenNew(Usuario.class).withNoArguments().thenReturn(user);
		
		Usuario usuario = service.retornaNomeUsuario();
		assertEquals(usuario.getNome(), nome);
		//verifico se o construtor realmente foi chamado
		PowerMockito.verifyNew(Usuario.class).withNoArguments();
	}
	
	@Test
	public void testarProrrogarLocacao(){
		
		 Filme filme = new FilmeBuilder().getFilme().build();
		 filme.setEstoque(1);
		 filme.setNome("Filme1");
		 filme.setPrecoLocacao(4.0);
		
		 List<Filme> filmes = Arrays.asList(filme); 
		
		 Locacao locacao = new LocacaoBuilder().getLocacao().build();
		 locacao.setValor(5.0);
		 locacao.setUsuario(usuario);
		 locacao.setFilmes(filmes);
		 
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(new Date());
		 
		 int dia = calendar.get(DAY_OF_MONTH);
	     int mes = calendar.get(MONTH);
	     int ano = calendar.get(YEAR);
		
		Calendar calendario = Calendar.getInstance();
		calendario.set(Calendar.DAY_OF_MONTH, dia);
		calendario.set(Calendar.MONTH, mes);
		calendario.set(Calendar.YEAR, ano);
		
		//neste exemplo uso o PowerMock para mokar o um método estatico, Calendar.getInstance() que é responsável por instanciar um objeto Calendar
		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendario);
		
		Locacao loc = service.prorrogarLocacao(locacao, 2);
		System.out.println(loc.getDataLocacao());
		System.out.println(new Date());
		assertThat(loc.getDataLocacao(), MatchersProprios.addDias(0));
		
		PowerMockito.verifyStatic(Mockito.times(1));
		Calendar.getInstance();
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception{
		
		 Filme filme = new FilmeBuilder().getFilme().build();
		 filme.setEstoque(1);
		 filme.setNome("Filme1");
		 filme.setPrecoLocacao(4.0);
		
		 List<Filme> filmes = Arrays.asList(filme);

		 //moka a chamada do método privado calcularValorLocacao da classe de serviço, logo valor da loacação será 1.0 ao invés de 4.0
		 PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

		 Locacao locacao = service.alugarFilme(usuario, filmes);
		
		 Assert.assertThat(locacao.getValor(), is(1.0));
		 PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception{
		
		 Filme filme = new FilmeBuilder().getFilme().build();
		 filme.setEstoque(1);
		 filme.setNome("Filme1");
		 filme.setPrecoLocacao(4.0);
		
		 List<Filme> filmes = Arrays.asList(filme);
		
		//acao - testando um método private sem mokar o valor da locação, pois service é um spy, logo utiliza os valores passados por parametro e não mokei o valor da locação
		Double valor = org.powermock.reflect.Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificacao
		Assert.assertThat(valor, is(4.0));
	}
}
