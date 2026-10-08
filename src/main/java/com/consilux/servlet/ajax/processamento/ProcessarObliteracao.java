package com.consilux.servlet.ajax.processamento;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.InfracaoObliteracao;
import com.consilux.model.Processamento;
import com.consilux.model.ProcessarObliteracaoPermanente;

/**
 * Servlet implementation class ProcessarObliteracao
 */
public class ProcessarObliteracao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ProcessarObliteracao.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProcessarObliteracao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		String permanente = request.getParameter("permanente");
		String sIdInfracaoProcesso = request.getParameter("id_infracao_processo");
		String sIdImagem = request.getParameter("id_imagem");
		String sSequenciaObliteracao = request.getParameter("sequencia_obliteracao");
		String sAlturaImagem = request.getParameter("img_veiculo_altura");
		String sErroOblit = request.getParameter("erro_oblit");
		logger.info("sErroOblit => " + (sErroOblit == null ? "null" : sErroOblit));
		Boolean ErroOblit = false;
		
		String s_infracao_des = request.getParameter("infracao_des");
		Integer infracao_des = (s_infracao_des != null && ExpValida.NATURAL.validar(s_infracao_des)) ? Integer.parseInt(s_infracao_des) : 0;
		
		String sMultiplasObliteracoes = request.getParameter("sMultiplasObliteracoes");
		logger.info("sMultiplasObliteracoes => " + (sMultiplasObliteracoes == null ? "null" : sMultiplasObliteracoes));
		
		//Criar a lista de multi obliterações
		InfracaoObliteracao lstOBL = null;
		try {
			if(sMultiplasObliteracoes != null && sMultiplasObliteracoes.length() > 0) {
				lstOBL = montaMultiplasObliteracoes(sMultiplasObliteracoes, 
																    sIdImagem, 
																    sIdInfracaoProcesso);
				logger.info("Número de obliterações recebidas: " + lstOBL.getListObliteracoes().size());
			}
			else
				logger.warn("Nenhuma obliteração recebida");
		} catch(Exception e) {
			logger.error("Erro ao obter múltiplas obliterações", e);
		}
		
		if (lstOBL == null)
			return;
		
		try {
			ErroOblit = Boolean.parseBoolean(sErroOblit);
		} catch(Exception e) { }
		
		Integer tamanhoTarja = ConfiguracaoProvider.getInstance().getTamanhoTarja();
		
		try {
			
			if (sIdInfracaoProcesso == null || !ExpValida.NATURAL.validar(sIdInfracaoProcesso))
				throw new ServletException("Identificador da Infração no Processo enviado invalido!");
	
			if (sIdImagem == null || !ExpValida.NATURAL.validar(sIdImagem))
				throw new ServletException("Identificador da Imagem enviado invalido!");
	
			if (sSequenciaObliteracao == null || !ExpValida.NATURAL.validar(sSequenciaObliteracao))
				throw new ServletException("Identificador da Sequência de Obliteração enviado invalido!");
				
			if (sAlturaImagem == null || !ExpValida.NATURAL_COM_ZERO.validar(sAlturaImagem))
				throw new ServletException("Valor de 'img_veiculo_altura' enviado invalido!");
	
			Integer idInfracaoProcesso = Integer.valueOf(sIdInfracaoProcesso);
			Integer idImagem = Integer.valueOf(sIdImagem);
	
			Integer altura_imagem = Integer.valueOf(sAlturaImagem);
			
			List<Rectangle> obliteracoes = lstOBL.getListObliteracoes();
			for(int t=0;t<obliteracoes.size();t++) {
				if( (obliteracoes.get(t).y + obliteracoes.get(t).height) > (altura_imagem - tamanhoTarja) )
					obliteracoes.get(t).height = altura_imagem - tamanhoTarja - obliteracoes.get(t).y;
			}
			
			Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
	
			if (proc == null)
				throw new ServletException("Processamento não iniciado!");
			
			if(permanente != null && permanente.equals("1")) {
				ProcessarObliteracaoPermanente obPerm = new ProcessarObliteracaoPermanente(idInfracaoProcesso, obliteracoes, ErroOblit, infracao_des);
				obPerm.run();
			} else {
				for(int t=0;t<obliteracoes.size();t++) {
					proc.processaObliteracao(idInfracaoProcesso, idImagem, t + 1, obliteracoes.get(t));
				}
			}
			
		}
		catch (Exception ex) {
			logger.error("Erro ao processar a obliteração: "+ex.getMessage(), ex);
			mostraMensagemErroUsuario(response, "Erro ao processar a obliteração: "+ex.getMessage());
		}

	}
	private void mostraMensagemErroUsuario(HttpServletResponse response, String mensagem) {
		AjaxXMLConstr xml;
		try {
			xml = new AjaxXMLConstr("processar_obliteracao");
			xml.adicCampo("ERRO", mensagem);
			xml.dump(response);
		}
		catch (Exception e) {
			logger.error("Erro ao entregar XML de resposta.", e);
		}
	}
	
	//Autor: 		Luiz Fernando Amaral
	//Data:			30/08/2014
	//Descrição:	monta objeto a partir da string de multiplas obliteração vindo na JSP
	public InfracaoObliteracao montaMultiplasObliteracoes(String sMultiplasObliteracoes, 
		    											  String sIdImagem, 
		    											  String sIdInfracaoProcesso){
		
		InfracaoObliteracao obl;
		//Criação da classe que contém o IdImagem e o IdInfracao
		obl = new InfracaoObliteracao(
		          Integer.valueOf(sIdImagem),
				  Integer.valueOf(sIdInfracaoProcesso));
		
		String obliteracao[] = sMultiplasObliteracoes.split(";"); 
		
		for(int h=0; h<obliteracao.length; h++){
			String atributos[] = obliteracao[h].split(",");   
			
			obl.AdicionarObliteracao(Integer.valueOf(atributos[0]),
									 Integer.valueOf(atributos[1]),
									 Integer.valueOf(atributos[2]),
									 Integer.valueOf(atributos[3])
								 );
		}
		
		return obl;
	}
}
