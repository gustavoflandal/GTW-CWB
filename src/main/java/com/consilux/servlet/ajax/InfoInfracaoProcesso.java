/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 05/05/2008

  Descricao: Servlet para envio de informações infração no processo.

  Historico:

    $Log: InfoInfracaoProcesso.java,v $
    Revision 1.11  2009/06/01 18:42:19  fos
    Consertado expressão regular de inteiros.

    Revision 1.10  2009/05/08 18:58:49  fos
    Agora busca a espécie selecionada durante o processamento também.

    Revision 1.9  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.6  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.5  2008/08/12 13:04:37  fos
    Agora armazena informação sobre a obliteração.

    Revision 1.4  2008/07/23 14:31:48  fos
    Retirado imports não usados.

    Revision 1.3  2008/05/08 21:32:54  fos
    Alterações dos nomes das classes.
    Agora esta classe esta reponsável por buscar os dados da tabela infração processo.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Acesso;
import com.consilux.model.InfracaoProcesso;

 /**
 * Servlet para envio de informações infração no processo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.11 $ $Date: 2009/06/01 18:42:19 $ $Author: fos $
 */
public class InfoInfracaoProcesso extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constrói o objeto
	 */
	public InfoInfracaoProcesso() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, false).verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");
		
		String sIdInfracaoProcesso = request.getParameter("id_infracao_processo");
		
		if (sIdInfracaoProcesso == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdInfracaoProcesso))
			throw new ServletException("Identificador de Infração-Processo enviado invalido!");

		InfracaoProcesso infracaoProcesso;
		try {
			infracaoProcesso = InfracaoProcesso.buscaInfracaoProcessoPorId(Integer.parseInt(sIdInfracaoProcesso));
		}
		catch(Exception err) {
			throw new ServletException("Erro ao buscar a infração-processo: "+err.getMessage());
		}

		if (infracaoProcesso == null) {
			return;
		}

		try {
			AjaxXMLConstr xml = new AjaxXMLConstr("infracao_processo");
			xml.adicCampo("INFRACAO_PROCESSO", String.valueOf(infracaoProcesso.getId()));
			xml.adicCampo("INCONSISTENCIA", String.valueOf(infracaoProcesso.getIdInconsistencia()));
			xml.adicCampo("PLACA", infracaoProcesso.getPlaca());
			if (infracaoProcesso.getX() != null) {
				xml.adicCampo("X", String.valueOf(infracaoProcesso.getX()));
				xml.adicCampo("Y", String.valueOf(infracaoProcesso.getY()));
				xml.adicCampo("LARGURA", String.valueOf(infracaoProcesso.getLargura()));
				xml.adicCampo("ALTURA", String.valueOf(infracaoProcesso.getAltura()));
			}
			xml.adicCampo("ID_IMAGEM", String.valueOf(infracaoProcesso.getIdImagem()));
			if (infracaoProcesso.getIdMarcaCET() != null) {
				xml.adicCampo("ID_MARCA_PROCESSO", String.valueOf(infracaoProcesso.getIdMarcaCET()));
			}
			if (infracaoProcesso.getIdEspecie() != null) {
				xml.adicCampo("ID_ESPECIE_PROCESSO", String.valueOf(infracaoProcesso.getIdEspecie()));
			}
			if (infracaoProcesso.getUf() != null) {
				xml.adicCampo("UF_PROCESSO", String.valueOf(infracaoProcesso.getUf()));
			}
			xml.dump(response);
		}
		catch(Exception err) {
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
	}  	
}