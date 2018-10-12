package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	@Mock
	private Calculadora calcMock;
	
	@Mock
	private Calculadora calcMock2;
	
	@Spy
	private Calculadora calcSpy;
	
	@Mock
	private EmailService email;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy(){
		
		/**
		 * A diferença entre mock e Spy é que quando mock não sabe o que saber,ou seja, se você chamar o serviço desta maneira :
		 * Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		 * e depois testa - lo com parametros diferentes:
		 * System.out.println("Mock:" + calcMock.somar(3, 2));
		 * o @Mock retornará a resposta padrão para aquela classe nesse caso que é um Integer será o ou uma "" quando for classe String
		 * 
		 * Já o @Spy executar o método com os parametros chamados no teste:
		 * Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		 * e depois:
		 * System.out.println("Spy:" + calcSpy.somar(2, 2));
		 * retornará 4, somará os parametros
		 *  IMPORTANTE - o @Spy nâo funciona com Interface, apenas classes pois ele chama a implementação do método
		 */
		
		//para o @Mock chamar a implementação do método real ao invés de .thenReturn(5), ou seja neste caso o @Mock soma os valores passados para o método somar()
		Mockito.when(calcMock2.somar(1, 2)).thenCallRealMethod();
		
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
		/**
		 * O método imprime da classe Calculadora não retorna nada é void, logo
		 * se não usar esta expectativa, o método imprime da classe Calculadora quando chamdo pela @Mock não será impresso já que este método não tem retorno, 
		 * e este é o padrão para esta anotação, quando o método é void não é feito nada, mas quando usar @Spy será impresso o valor do método imprime, pois
		 * o comportamento padrão é executar o método.
		 * 
		 * Agora se você quiser que a @Spy não execute o método imprime, descomente a expectativa:
		 * Mockito.doNothing().when(calcSpy).imprime()
		 * 
		 */
		Mockito.doNothing().when(calcSpy).imprime();
		
		System.out.println("Mock chama o método normamente .thenCallRealMethod(): " + calcMock2.somar(1, 2));
		System.out.println("Mock:" + calcMock.somar(1, 2));
		System.out.println("Spy soma os valores passados :" + calcSpy.somar(2, 2));
		System.out.println("Spy :" + calcSpy.somar(1, 2));
		
		System.out.println("Mock");
		calcMock.imprime();
		System.out.println("Spy");
		calcSpy.imprime();
	}
	

	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		// quando houver 2 parametros, se mockar um parametro com o matcher (
		// Mockito.anyInt()), terá que mockar os demais com matcher, se não
		// mockar os 2 dará erro
		// estou dizendo que no primeiro parametro passo 1 e no segundo qualquer
		// número inteiro e vai retornar 5
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

		// erro necessário mockar os dois parametros como demonstrado acima
		// Mockito.when(calc.somar(1, Mockito.anyInt())).thenReturn(5);

		// imprimo o método somar, neste caso mockado
		System.out.println(calc.somar(1, 100000));
	}

	@Test
	public void teste2() {
		Calculadora calc = Mockito.mock(Calculadora.class);

		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);

		Assert.assertEquals(5, calc.somar(134345, -234));
		System.out.println(argCapt.getAllValues());
	}
}
