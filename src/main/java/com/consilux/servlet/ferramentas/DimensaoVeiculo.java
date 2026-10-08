/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 24/08/2017

  Descrição: Servlet para envio de dados da dimensão do veículo (altura, comprimento, largura, grupos de eixos).

  Histórico:

 *********************************************************************************/
package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.ferramenta.Dimensao;

/**
 * Servlet para envio de dados da dimensão do veículo (altura, comprimento, largura, grupos de eixos).
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 24/08/2017
 */
public class DimensaoVeiculo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = 8931202539704328853L;
	private static Logger logger = Logger.getLogger(DimensaoVeiculo.class); 
	
	/**
	 * Constrói o objeto
	 */
	public DimensaoVeiculo() {
		super();
	}   	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false)) {
			throw new ServletException("Usuário não atenticado!");
		}

		Date inicio = new Date();
		String sIdVeiculo = request.getParameter("id_veiculo");
		
		if (sIdVeiculo == null || !ExpValida.LONGO.validar(sIdVeiculo)) {
			throw new ServletException("Identificador do veículo enviado inválido!");
		}

		Integer idVeiculo = Integer.valueOf(sIdVeiculo);
		
		List<Dimensao> imagens = new ArrayList<Dimensao>();
		int qtde = 0;
		String url = "/WEB-INF/templates/veiculo/dimensao_veiculo.jsp";
		
		try {
			
			Dimensao pesoDimensao = Dimensao.buscaPesoDimensaoPorIdVeiculo(idVeiculo);
			
			List<Dimensao> dimensoes = Dimensao.buscaDimensaoPorIdVeiculo(idVeiculo);
			List<Dimensao> eixos = Dimensao.buscaPesoEixoPorIdVeiculo(idVeiculo);
			List<Dimensao> grupos = Dimensao.buscaPesoGrupoPorIdVeiculo(idVeiculo);
			
			
			List<String> listaImagens = new ArrayList<String>();
			
			if (pesoDimensao == null) {

				url = "/WEB-INF/templates/veiculo/dimensao_veiculo_vazio.jsp";
				
			} else {
				
				url = "/WEB-INF/templates/veiculo/dimensao_veiculo.jsp";
				
				if (pesoDimensao.getTodasClassificacaoQfv() != null) {
					listaImagens = Arrays.asList(pesoDimensao.getTodasClassificacaoQfv().split("-"));
				}
				
				if(listaImagens.size() > 3) {
		 			qtde = 3;
				} else {
		 			qtde = listaImagens.size();
				}
				
				if (listaImagens != null) {
					for (int i = 0; i < qtde; i++) {
						Dimensao imagem = new Dimensao();
						imagem.setClassificacaoQfv(listaImagens.get(i).trim());
						imagens.add(imagem);
					}
				}
				
				request.setAttribute("imagens", imagens);
				
				request.setAttribute("pesoDimensao", pesoDimensao);
				
				request.setAttribute("dimensoes", dimensoes);
				request.setAttribute("eixos", eixos);
				request.setAttribute("grupos", grupos);
				
			}
			
		    response.setContentType("text/html; charset=UTF-8");
		    response.setCharacterEncoding("UTF-8");
			
			RequestDispatcher rd = request.getRequestDispatcher(url);
			rd.include(request, response);

			logger.info("[TEMPO] Dimensão: " + (new Date().getTime() - inicio.getTime()));
		}
		catch(Exception err) {
			logger.error("Erro ao mostar dimensões.", err);
			throw new ServletException("Erro ao mostrar dimensões: " + err.getMessage(), err);
		}
	}
}