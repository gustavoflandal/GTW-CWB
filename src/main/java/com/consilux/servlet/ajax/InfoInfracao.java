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
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Infracao;
import com.consilux.model.InfracaoCompleta;
import com.consilux.model.Veiculo;

 /**
 * Servlet para envio de informações sobre infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/06/01 18:41:41 $ $Author: fos $
 */
public class InfoInfracao extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoInfracao.class);
	/**
	 * Constrói o objeto
	 */
	public InfoInfracao() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		if (!new Acesso(request, response, false).verificaAcesso(false))
//			throw new ServletException("Usuário não atenticado!");
		
		String sIdInfracao = request.getParameter("id_infracao");
		
		if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao))
			throw new ServletException("Identificador da Infração enviado invalido!");

		try {
			Infracao infracao = Infracao.buscaInfracaoPorId(Integer.parseInt(sIdInfracao));

			if (infracao == null) {
				return;
			}

			Veiculo veiculo = Veiculo.buscaVeiculoPorId(infracao.getIdVeiculo());

			logger.info("Obtendo id_imagem a partir do Veiculo");
			Integer id_imagem = veiculo.getIdImagemObj();
			if (id_imagem == null || id_imagem == 0) 
			{
				logger.info("Obtendo id_imagem a partir da Infracao");
				id_imagem = infracao.getIdImagemOBJ();
			}
			if (id_imagem == null || id_imagem == 0)
			{
				logger.info("Obtendo id_imagem a partir da Infracao Completa");
				id_imagem = InfracaoCompleta.buscaInfracaoPorId(infracao.getIdInfracao()).getIdImagemOBJ();
			}
			
			AjaxXMLConstr xml = new AjaxXMLConstr("infracao");
			xml.adicCampo("ID_INFRACAO", String.valueOf(infracao.getIdInfracao()));
			xml.adicCampo("PLACA", infracao.getPlaca());
			xml.adicCampo("PLACA_LIDA", veiculo.getPlaca() != null ? veiculo.getPlaca() : "");
			xml.adicCampo("VEICULO", String.valueOf(infracao.getIdVeiculo()));
			xml.adicCampo("DATA", new SimpleDateFormat("dd/MM/yyyy").format(infracao.getData()));
			xml.adicCampo("INCONSISTENCIA", String.valueOf(infracao.getIdInconsistencia()));
			xml.adicCampo("ID_IMAGEM_OBJ", String.valueOf(id_imagem));
			xml.adicCampo("COM_VIDEO", String.valueOf(veiculo.getComVideo()));
			xml.adicCampo("COM_PESAGEM", String.valueOf(veiculo.getComPesagem())); 
			
			xml.dump(response);
		}
		catch(Exception err) {
			logger.error("Erro ao montar o XML.",err);
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		
	}  	
}