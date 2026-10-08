package com.consilux.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.IExpValida;

public class TrataEntrada {
	private HttpServletRequest request;
	private HttpServletResponse response;
	/**
	 * @param request
	 * @param response
	 */
	public TrataEntrada(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
	}
	
	public Boolean validaParametro(String nomeParametro, IExpValida exp) {
		return validaParametro(nomeParametro, exp, false);
	}
	
	public Boolean validaParametro(String nomeParametro, IExpValida exp, Boolean permiteNulo) {
		String sVal = request.getParameter(nomeParametro);
	
		if (permiteNulo)
			return (sVal == null);
		else 
			return exp.validar(sVal);
	}
}
