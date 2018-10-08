package br.ce.wcaquino.cliente;

import br.ce.wcaquino.dao.LocacaoDAOImpl;
import br.ce.wcaquino.servicos.EmailServiceImpl;
import br.ce.wcaquino.servicos.LocacaoService;

public class ConsumirLocacaoService {
	
	public static void main(String[] args) {
		
		LocacaoDAOImpl dao = new LocacaoDAOImpl();
		 EmailServiceImpl emailServiceImpl = new EmailServiceImpl();
		LocacaoService servico = new LocacaoService();
		
		
		servico.setLocacaoDao(dao);
		servico.setEmailService(emailServiceImpl);
		servico.notificarAtrasos();
	}

}
