/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Servlet para envio de informações sobre infração.

  Historico:

    $Log: InfoInfracao.java,v $
    Revision 1.9  2009/06/01 18:41:41  fos
    Consertado expressão regular de inteiros.

    Revision 1.8  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.6  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.5  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.4  2008/08/12 13:03:57  fos
    Agora retorna também a inconsistência.

    Revision 1.3  2007/04/11 12:10:14  fos
    Ajustado nó-pai do XML.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.InfracaoSimplificada;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento.EtapaProcesso;

 /**
 * Servlet para envio de informações sobre infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/06/01 18:41:41 $ $Author: fos $
 */
public class ContaInfracaoDisponivelUsuario extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ContaInfracaoDisponivelUsuario.class);
	/**
	 * Constrói o objeto
	 */
	public ContaInfracaoDisponivelUsuario() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Verifica segurança
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso(true))
			throw new ServletException("Usuário não atenticado!");		
		
		String sIdProcesso = request.getParameter("id_processo");

		String sDataInfracaoIni = request.getParameter("data_infracao_ini") != null ? request.getParameter("data_infracao_ini").trim() : null;
		sDataInfracaoIni = sDataInfracaoIni != null && sDataInfracaoIni.length() == 0 ? null : sDataInfracaoIni;

		String sHoraInfracaoIni = request.getParameter("hora_infracao_ini") != null ? request.getParameter("hora_infracao_ini").trim() : null;
		sHoraInfracaoIni = sHoraInfracaoIni != null && sHoraInfracaoIni.length() == 0 ? null : sHoraInfracaoIni;

		String sDataInfracaoFim = request.getParameter("data_infracao_fim") != null ? request.getParameter("data_infracao_fim").trim() : null;
		sDataInfracaoFim = sDataInfracaoFim != null && sDataInfracaoFim.length() == 0 ? null : sDataInfracaoFim;

		String sHoraInfracaoFim = request.getParameter("hora_infracao_fim") != null ? request.getParameter("hora_infracao_fim").trim() : null;
		sHoraInfracaoFim = sHoraInfracaoFim != null && sHoraInfracaoFim.length() == 0 ? null : sHoraInfracaoFim;

		if (sIdProcesso == null || !Pattern.matches("^(11|23|24|25|90|91|[0-9])$",sIdProcesso)) {
			new Mensagem(response).showErro("Identificador de etapa enviado invalido!");
			return;
		}

		String sIdEnquadramento = request.getParameter("id_enquadramento") != null ? request.getParameter("id_enquadramento").trim() : null;
		sIdEnquadramento = sIdEnquadramento != null && sIdEnquadramento.length() == 0 ? null : sIdEnquadramento;

		if (sIdEnquadramento != null && !Pattern.matches("[0-9]{0,9}",sIdEnquadramento)) {
			new Mensagem(response).showErro("Identificador de enquadramento enviado inválido!");
			return;
		}
		if (sDataInfracaoIni == null || !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoIni)) {
			new Mensagem(response).showErro("Data inicial do período enviada inválida!");
			return;
		}
		if (sDataInfracaoFim == null || !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoFim)) {
			new Mensagem(response).showErro("Data final do período enviada inválida!");
			return;
		}
		if (sHoraInfracaoIni == null || !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoIni)) {
			new Mensagem(response).showErro("Hora inicial do período enviada inválida!");
			return;
		}
		if (sHoraInfracaoFim == null || !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoFim)) {
			new Mensagem(response).showErro("Hora final do período enviada inválida!");
			return;
		}

		Integer idEnquadramento = sIdEnquadramento != null ? Integer.valueOf(sIdEnquadramento) : null;
		idEnquadramento = idEnquadramento != null && idEnquadramento > 0 ? idEnquadramento : null;

		String sConsistencia = request.getParameter("consistencia") != null ? request.getParameter("consistencia").trim() : null;
		sConsistencia = sConsistencia != null && sConsistencia.length() == 0 ? null : sConsistencia;

		if (sConsistencia != null && !Pattern.matches("[0-1]",sConsistencia)) {
			new Mensagem(response).showErro("Seleção de consistência inválida!");
			return;
		}

		Boolean consistencia = null;
		if (sConsistencia == null)	
			consistencia = null;
		else if (sConsistencia.equals("0"))	
			consistencia = false;
		else if (sConsistencia.equals("1"))	
			consistencia = true;

		Timestamp periodoIni = null; 
		Timestamp periodoFim = null; 

		if (sDataInfracaoIni != null) {
			try {
				periodoIni = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoIni+" "+sHoraInfracaoIni+":00").getTime());
				periodoFim = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoFim+" "+sHoraInfracaoFim+":59").getTime());
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
		}

		EtapaProcesso etapa = EtapaProcesso.valueOfId(Integer.valueOf(sIdProcesso));

		try {
			Integer totalInfracoes = InfracaoSimplificada.contaInfracoesPorEtapaProcessoUsuario(etapa.getId(), acesso.getUsuario().getId(), idEnquadramento, consistencia, periodoIni, periodoFim);

			AjaxXMLConstr xml = new AjaxXMLConstr("conta_infracao");
			xml.adicCampo("TOTAL", String.valueOf(totalInfracoes));
			xml.dump(response);
		}
		catch(Exception err) {
			logger.error("Erro ao montar o XML.",err);
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		
	}  	
}