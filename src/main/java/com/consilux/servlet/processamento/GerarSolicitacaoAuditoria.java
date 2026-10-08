/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 28/02/2007

  Descricao: Servlet para visualização de notificações geradas externamente.

  Historico:

    $Log: GerarRemessa.java,v $
    Revision 1.2  2009/03/18 17:22:01  fos
    Agora esta classe só se responsabiliza por gerar a remessa no banco de dados deixando a exportação por conta da nova classe 'ExportarRemessa'.

    Revision 1.1  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.4  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.3  2007/05/04 13:58:24  fos
    Agora redireciona o output de exe externo para a default output.

    Revision 1.2  2007/04/17 18:00:38  fos
    Ajustado o pacote da classe ConfiguracaoException.

    Revision 1.1  2007/04/11 11:57:12  fos
    Reposicionado o diretório

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.SolicitacaoAuditoria;
import com.consilux.model.Usuario;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

 /**
 * Servlet para geração de remessa.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/03/18 17:22:01 $ $Author: fos $
 */
public class GerarSolicitacaoAuditoria extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 8412512406909507677L;
	private static Logger logger = Logger.getLogger(GerarSolicitacaoAuditoria.class);
	
	/**
	 * Constrói o objeto 
	 */
	public GerarSolicitacaoAuditoria() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true); 
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sDataImagens = request.getParameter("data_imagens");
		
		if (sDataImagens == null || !ExpValida.DATA.validar(sDataImagens)) {
			new Mensagem(response).showErro("Data das imagens enviada inválida!", "javascript:window.close()");
			return;
		}

		Date dataImagens;
				
		try {
			dataImagens = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataImagens + " 00:00:00");
		}
		catch (Exception e) {
			logger.error("Erro realizar parser da data", e);
			new Mensagem(response).showErro("Erro realizar parser da data: "+e.getMessage(), "javascript:window.close()");
			return;
		}
		
		try {
			SolicitacaoAuditoria sa = geraSolicitacaoAuditoria(dataImagens,acesso.getUsuario());
			
			new Mensagem(response).showSucesso("Solicitação gerada com sucesso!<br>" +
			   "Clique <a href='#' onclick='window.open(\"/processo/SolicitacaoAuditoriaServlet?id_solicitacao_auditoria=" +
			   sa.getIdSolicitacaoAuditoria() + "\",\"Visualizar\",\"width=700, height=160\")'>&lt;aqui&gt;</a> " +
			   "para ver em PDF.", "javascript:window.close()");
			
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar a solição de auditoria.", e);
			new Mensagem(response).showErro("Erro ao gerar a solição de auditoria: "+e.getMessage(), "javascript:window.close()");
			return;
		}
		
	}  	  	
	
	private SolicitacaoAuditoria geraSolicitacaoAuditoria(Date dataImagens, Usuario usu) throws ServletException, SQLException, ConexaoException, ModelException {
		return SolicitacaoAuditoria.geraSolicitacaoAuditoria(dataImagens, EtapaProcesso.VALIDACAO.getId(), usu);
	}	
	
}