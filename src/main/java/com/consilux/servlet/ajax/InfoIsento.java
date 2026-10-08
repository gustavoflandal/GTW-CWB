/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 29/04/2007

  Descricao: Servlet para envio de informações sobre o cadastro de um veículo isento.

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Isento;

 /**
 * Servlet para envio de informações sobre o cadastro de um veículo isento.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.10 $ $Date: 2009/05/19 11:34:15 $ $Author: fos $
 */
public class InfoIsento extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoIsento.class); 
	/**
	 * Constrói o objeto
	 */
	public InfoIsento() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		if (!new Acesso(request, response, false).verificaAcesso(false))
//			throw new ServletException("Usuário não atenticado!");
		
		Date inicio = new Date();
		String sPlaca = request.getParameter("placa");
		String sIdEnquadramento = request.getParameter("id_enquadramento");
		String sData = request.getParameter("data");
		String sArea = request.getParameter("area");
		sArea = (sArea != null && sArea.length() > 0) ? sArea : null;
		
		try {
			if (sPlaca != null && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca))
				throw new ServletException("Placa enviada inválida!: " + sPlaca);
			
			else if (sIdEnquadramento == null || !ExpValida.NATURAL.validar(sIdEnquadramento)) {
		    	throw new ServletException("Enquadramento selecionado inválido!");
		    }
			else if (sData != null && !ExpValida.DATA.validar(sData)) {
				throw new ServletException("Data enviada inválida!");
			}
			else if (sArea != null && !ExpValida.NATURAL_COM_ZERO.validar(sArea)) {
				throw new ServletException("Área de isenção enviada inválida!");
			}
			
		    
			List<Isento> isentos;
			try {
				Date data = null;
			    if (sData != null)
			    	data = new SimpleDateFormat("dd/MM/yyyy").parse(sData);

			    Map<String,Object> mFiltro = new HashMap<String,Object>();
			    
			    if (data != null)
			        mFiltro.put("data_validade",new Timestamp(data.getTime()));
			    
			    if (sArea != null && !sArea.equals("0"))
			        mFiltro.put("area",Integer.valueOf(sArea));
			    
			    isentos = Isento.buscaRapidaIsentoPor(sPlaca, Integer.valueOf(sIdEnquadramento), data, mFiltro);
			}
			catch(Exception err) {
				err.printStackTrace();
				throw new ServletException("Erro ao buscar a infracao: " + err.getMessage());
			}

			try {
				AjaxXMLConstr xml = new AjaxXMLConstr("isento");
				if (isentos != null && isentos.size() > 0) {
					Isento isento = isentos.get(0);
					xml.adicCampo("PLACA", isento.getPlaca());
					xml.adicCampo("ENQUADRAMENTO", String.valueOf(isento.getIdEnquadramento()));
					xml.adicCampo("DATA_INICIO", isento.getDataInicio() != null ? new SimpleDateFormat("dd/MM/yyyy").format(isento.getDataInicio()) : "");
					xml.adicCampo("HORARIO_INICIO", isento.getHorarioInicio() != null ? new SimpleDateFormat("HH:mm:ss").format(isento.getHorarioInicio()) : "");
					xml.adicCampo("DATA_FIM", isento.getDataFim() != null ? new SimpleDateFormat("dd/MM/yyyy").format(isento.getDataFim()) : "");
					xml.adicCampo("HORARIO_FIM", isento.getHorarioFim() != null ? new SimpleDateFormat("HH:mm:ss").format(isento.getHorarioFim()) : "");
					xml.adicCampo("MOTIVO", isento.getMotivo() != null ? isento.getMotivo() : "");
					xml.adicCampo("ID_INCONSISTENCIA", String.valueOf(isento.getIdInconsistenciaIsencao()));
				}
				xml.dump(response);
			}
			catch(Exception err) {
				err.printStackTrace();
				throw new ServletException("Erro ao montar o XML: " + err.getMessage());
			}
		}  catch(ServletException se) {
			
			// Avisa o cliente com XML contendo o erro. 
			AjaxXMLConstr xml;
			
			try {
				xml = new AjaxXMLConstr("isento");
				xml.adicCampo("ERRO", se.getMessage());
				xml.dump(response);
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}		
		logger.info("[TEMPO] Isento ["+sPlaca+"]: "+(new Date().getTime() - inicio.getTime()));
	}  	
}