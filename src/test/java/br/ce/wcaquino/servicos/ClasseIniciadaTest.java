package br.ce.wcaquino.servicos;

import static org.mockito.Mockito.doReturn;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import br.ce.wcaquino.entidades.ClasseIniciada;


 
@RunWith(PowerMockRunner.class)
@PrepareForTest(ClasseMockeada.class)

public class ClasseIniciadaTest {
	
   ClasseMockeada mockeada = new ClasseMockeada();
	
	@Mock
	ClasseIniciada claseIniciadaMock;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
 
	@After
	public void tearDown() throws Exception {
	}
 
	@Test
	public void testUsandoMock() throws Exception {
		
		 String resultadoMock = "Soy un mock no me creas";
		 doReturn(resultadoMock).when(claseIniciadaMock).getPropiedad();
		 PowerMockito.whenNew(ClasseIniciada.class).withNoArguments().thenReturn(claseIniciadaMock);
		 
		// mockeada.metodo();
		 
	}
	@Test
	public void testUsandoElReal() throws Exception {
		mockeada.metodo();
	}

}
