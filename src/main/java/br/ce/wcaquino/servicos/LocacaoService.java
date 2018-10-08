package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.dao.LocacaoDAOImpl;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes, Date dataEntrega, int dia) throws FilmeSemEstoqueException, LocadoraException {
	
		Double valorTotal = 0d;
		Locacao locacao = new Locacao();
		Double precoDesconto = 0d;
		
		if(usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}
		
		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		for(Filme filme: filmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException("Filme vazio");
			}
		}
		
		if(spcService.possuiNegativacao(usuario)) {
			throw new LocadoraException("Usuário Negativado");
		}
		
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		
		for(Filme filme: filmes) {
			
			switch (filme.getNome()) {
			case "Filme 3":
				precoDesconto = filme.getPrecoLocacao() / 100 * 75;
				filme.setPrecoLocacao(precoDesconto);
				break;
			case "Filme 1":
				precoDesconto = filme.getPrecoLocacao() / 100 * 0;
				filme.setPrecoLocacao(precoDesconto);
				break;
			}
			/*if (filme.getNome().equals("Filme 3")){
				precoDesconto = filme.getPrecoLocacao() / 100 * 75;
				filme.setPrecoLocacao(precoDesconto);
			}
			if (filme.getNome().equals("Filme 1")){
				precoDesconto = filme.getPrecoLocacao() / 100 * 0;
				filme.setPrecoLocacao(precoDesconto);
			}*/
			valorTotal += filme.getPrecoLocacao();
		}
		locacao.setValor(valorTotal);
		
        //adiciona dias do mês a data de entrega	
		dataEntrega = adicionarDias(dataEntrega, dia);
		
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)){
			throw new LocadoraException("Não é possível entregar filmes ao Domingos");
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
        dao.salvar(locacao);
		return locacao;
	}
	
	public void notificarAtrasos(){
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao: locacoes) {
			if (locacao.getDataRetorno().after(DataUtils.adicionarDias(locacao.getDataLocacao(), 3))) {
				emailService.notificarAtraso(locacao.getUsuario());	
			}
		}
	}

	public void setLocacaoDao(LocacaoDAO dao) {
		this.dao =  (LocacaoDAOImpl) dao;
	}

	public void setSPCService(SPCService spc) {
		spcService = spc;
	}
	
	public void setEmailService(EmailService email) {
		emailService = email;
	}
	
}