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
		 * A diferen�a entre mock e Spy � que quando mock n�o sabe o que saber,ou seja, se voc� chamar o servi�o desta maneira :
		 * Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		 * e depois testa - lo com parametros diferentes:
		 * System.out.println("Mock:" + calcMock.somar(3, 2));
		 * o @Mock retornar� a resposta padr�o para aquela classe nesse caso que � um Integer ser� o ou uma "" quando for classe String
		 * 
		 * J� o @Spy executar o m�todo com os parametros chamados no teste:
		 * Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		 * e depois:
		 * System.out.println("Spy:" + calcSpy.somar(2, 2));
		 * retornar� 4, somar� os parametros
		 *  IMPORTANTE - o @Spy n�o funciona com Interface, apenas classes pois ele chama a implementa��o do m�todo
		 */
		
		//para o @Mock chamar a implementa��o do m�todo real ao inv�s de .thenReturn(5), ou seja neste caso o @Mock soma os valores passados para o m�todo somar()
		Mockito.when(calcMock2.somar(1, 2)).thenCallRealMethod();
		
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
		/**
		 * O m�todo imprime da classe Calculadora n�o retorna nada � void, logo
		 * se n�o usar esta expectativa, o m�todo imprime da classe Calculadora quando chamdo pela @Mock n�o ser� impresso j� que este m�todo n�o tem retorno, 
		 * e este � o padr�o para esta anota��o, quando o m�todo � void n�o � feito nada, mas quando usar @Spy ser� impresso o valor do m�todo imprime, pois
		 * o comportamento padr�o � executar o m�todo.
		 * 
		 * Agora se voc� quiser que a @Spy n�o execute o m�todo imprime, descomente a expectativa:
		 * Mockito.doNothing().when(calcSpy).imprime()
		 * 
		 */
		Mockito.doNothing().when(calcSpy).imprime();
		
		System.out.println("Mock chama o m�todo normamente .thenCallRealMethod(): " + calcMock2.somar(1, 2));
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
		// Mockito.anyInt()), ter� que mockar os demais com matcher, se n�o
		// mockar os 2 dar� erro
		// estou dizendo que no primeiro parametro passo 1 e no segundo qualquer
		// n�mero inteiro e vai retornar 5
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);

		// erro necess�rio mockar os dois parametros como demonstrado acima
		// Mockito.when(calc.somar(1, Mockito.anyInt())).thenReturn(5);

		// imprimo o m�todo somar, neste caso mockado
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
