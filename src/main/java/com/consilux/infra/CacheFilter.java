package com.consilux.infra;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Implementação de um filtro que adeiciona cabeçalhos para forçar o cache de 
 * arquivos.
 * @author raoni
 *
 */
public class CacheFilter implements javax.servlet.Filter {

	FilterConfig filterConfig = null;

	public void init(FilterConfig filterConfig){
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpServletResponse httpResponse = (HttpServletResponse) res;
		
		// Evita que seja feito cache dos arquivos que NÃO DEVEM ser cacheados 
		if(!httpRequest.getRequestURI().contains(".nocache.") ){

			// 2592000 segundos = 30 dias
			httpResponse.setHeader("Cache-Control", "public, max-age=2592000");
 			httpResponse.setDateHeader("Expires", DateUtils.addDays(new Date(), 30).getTime());
		}

		chain.doFilter(req, res);
	}

	public void destroy(){
		this.filterConfig = null;
	}
}