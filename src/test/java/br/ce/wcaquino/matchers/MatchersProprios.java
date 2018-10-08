package br.ce.wcaquino.matchers;

import static java.util.Calendar.DAY_OF_WEEK;

import java.util.Calendar;
import java.util.Date;

import br.ce.wcaquino.utils.DataUtils;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher hojeMaiUmDia(Integer numero) {
		Calendar calendar = Calendar.getInstance();
		Date date =  DataUtils.obterDataComDiferencaDias(numero);
		calendar.setTime(date);
		int diaSemana =  calendar.get(DAY_OF_WEEK);
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher hoje(Integer numero) {
		Calendar calendar = Calendar.getInstance();
		Date date =  DataUtils.obterDataComDiferencaDias(numero);
		calendar.setTime(date);
		int diaSemana =  calendar.get(DAY_OF_WEEK);
		return new DiaSemanaMatcher(diaSemana);
	}
}
