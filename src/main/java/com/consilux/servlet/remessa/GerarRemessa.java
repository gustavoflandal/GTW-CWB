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
package com.consilux.servlet.remessa;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.DateUtil;
import com.consilux.model.Acesso;
import com.consilux.model.Enquadramento;
import com.consilux.model.ExportaRemessa;
import com.consilux.model.Inconsistencia;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.exception.ModelException;
import com.consilux.model.remessa.RemessaFactory;

 /**
 * Servlet para geração de remessa.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/03/18 17:22:01 $ $Author: fos $
 */
public class GerarRemessa extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 8412512406909507677L;
	private static Logger logger = Logger.getLogger(GerarRemessa.class);
	
	/**
	 * Constrói o objeto 
	 */
	public GerarRemessa() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, true);
		if (!acesso.verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");
//		String sIdProcesso = request.getParameter("id_processo");
		String sIdEnquadramento = request.getParameter("id_enquadramento");
		String sDataRemessa = request.getParameter("dataremessa");
		String sResidual = request.getParameter("residual");
		String sNumeroInfracoes = request.getParameter("infracoes");
		String sIdInconsistencia = request.getParameter("id_inconsistencia");
		String sTipoGeracao = request.getParameter("tipo_geracao");
		String sIdRemessaAutomatico = request.getParameter("id_remessa_automatico");
		
		int infracoes = 0, id_usuario = 0, idEnquadramento = 999999, idInconsistencia = 999999, tipoGeracao = 0, idRemessaAutomatico = 0;
		
		try {
			logger.info("sIdInconsistencia = " + sIdInconsistencia);
			
			if(sNumeroInfracoes == null || !ExpValida.NATURAL_COM_ZERO.validar(sNumeroInfracoes))
				infracoes = 9999;
			else
				infracoes = Integer.parseInt(request.getParameter("infracoes"));
			
			id_usuario = acesso.getUsuario().getId();
			
			if(infracoes > 9999)
				infracoes = 9999;
			
			tipoGeracao = Integer.parseInt(sTipoGeracao);
			
			if (sIdRemessaAutomatico != null && !sIdRemessaAutomatico.equals("")) {
				idRemessaAutomatico = Integer.parseInt(sIdRemessaAutomatico);
			}
			
			idEnquadramento = Integer.parseInt(sIdEnquadramento);
			idInconsistencia = Integer.parseInt(sIdInconsistencia);
						
		} catch(Exception e) {}
		
//		if (sIdProcesso != null && !ExpValida.NATURAL.validar(sIdProcesso)) {
//			new Mensagem(response).showErro("Identificador do processo enviado inválido!");
//			return;
//		}
		
		if (sDataIni == null || !ExpValida.DATA.validar(sDataIni)) {
			new Mensagem(response).showErro("Data inicial enviada inválida!");
			return;
		}
		if (sDataFim == null || !ExpValida.DATA.validar(sDataFim)) {
			new Mensagem(response).showErro("Data final enviada inválida!");
			return;
		}
		
		if (sNumeroInfracoes == null || sNumeroInfracoes.equals("") || infracoes == 0) {
			new Mensagem(response).showErro("Número de infrações enviado inválido!");
			return;			
		}
		if (sDataRemessa == null || !ExpValida.DATA.validar(sDataRemessa)) {
			new Mensagem(response).showErro("Data do movimento enviada inválida!");
			return;
		}
		if (sResidual != null && !sResidual.equals("true")) {
			new Mensagem(response).showErro("Indicador resídual inválido!");
			return;
		}

		Enquadramento enquadramento = null;
		Inconsistencia inconsistencia = null;
		EtapaProcesso etapa;
		Date dtIni = null;
		Date dtFim = null;
		Date dtRemessa;
		Boolean residual;
				
		try {
			SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			dtRemessa = formatoData.parse(sDataRemessa + " 23:59:59");
			residual = Boolean.valueOf(sResidual);
			etapa = EtapaProcesso.REMESSA_GERAL;
			dtIni =	formatoData.parse(sDataIni + " 00:00:00");
			dtFim = formatoData.parse(sDataFim + " 23:59:59");
			
			if (tipoGeracao == 1) {
				enquadramento = Enquadramento.buscaEnquadramentosPorId(idEnquadramento);
				if (idInconsistencia == 99)
					inconsistencia = new Inconsistencia(99, "Vel. 100% Acima da Vel. Regul.");
				else 
					inconsistencia = Inconsistencia.buscaInconsistenciaPorId(idInconsistencia);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		if (dtRemessa.before(new Date())) {
			new Mensagem(response).showErro("Data do movimento não pode ser anterior a hoje!");
			return;
		}

		if (dtRemessa.after(DateUtil.addDays(new Date(), 4))) {
			new Mensagem(response).showErro("Data do movimento não pode ser agendada para mais de 3 dias!");
			return;
		}
		
		try {
			
			if (tipoGeracao == 1) {
				preparaRemessa(enquadramento, etapa, dtIni, dtFim, residual, inconsistencia);
				Remessa remessa = geraRemessa(enquadramento, etapa, dtIni, dtFim, dtRemessa, residual, infracoes, id_usuario, inconsistencia, idRemessaAutomatico);
				remessa.reposicionarRemessa(Processamento.EtapaProcesso.VALIDACAO.getId());
				
				new Mensagem(response).showSucesso("Movimento de Lote gerado com sucesso!<br>" +
				   "Clique <a href='#' onclick='window.open(\"/remessa/remessa.jsp?id_remessa=" +
				   remessa.getIdRemessa() + "\",\"Detalhes\",\"width=700, height=160\")'>&lt;aqui&gt;</a> " +
				   "para ver detalhes.");
			
			} else if (tipoGeracao == 2) {
				//preparaRemessaAutomatico(etapa, dtIni, dtFim);
				Boolean remessaAutomatico = geraRemessaAutomatico(etapa, dtIni, dtFim, dtRemessa, residual, infracoes, id_usuario);
				
				if (remessaAutomatico) {
					new Mensagem(response).showSucesso("Agendamento para geração automática de lotes gravado com sucesso!");
				} else {
					new Mensagem(response).showErro("Erro ao gravar agendamento para geração automática de lotes!");
				}
				
			}
			
		} catch (Exception e) {
			logger.error("Erro ao gerar a remessa.", e);
			new Mensagem(response).showErro("Erro ao gerar a remessa: "+e.getMessage());
			return;
		}
		
	}  	  	
	
	private void preparaRemessa(Enquadramento enquadramento, EtapaProcesso etapa, Date dtIni, Date dtFim, Boolean residual, Inconsistencia inconsistencia) throws Exception {

		Class<? extends ExportaRemessa<?>> tipoRemessa;
		try {
			
			// Recupera qual é a implementação (da configuração).
			tipoRemessa = ConfiguracaoProvider.getInstance().getImplementacaoExportaRemesssa();

			// Recupera o método utilizando reflection.
			Method preparaRemessa = tipoRemessa.getMethod("preparaRemessa", Enquadramento.class, EtapaProcesso.class, Date.class, Date.class, Boolean.TYPE, Inconsistencia.class);
			
			// Invoca o método
			preparaRemessa.invoke(null, enquadramento, etapa, dtIni, dtFim, residual.booleanValue(), inconsistencia);
			
		} catch (ConfiguracaoException e) {
			raiseAndLogError("Erro de configuração ao preparar remessa", e);
		} catch (SecurityException e) {
			raiseAndLogError("Erro de segurança ao preparar remessa", e);
		} catch (NoSuchMethodException e) {
			raiseAndLogError("Erro de reflexão ao preparar remessa", e);
		} catch (IllegalArgumentException e) {
			raiseAndLogError("Erro de argumentos ao preparar remessa", e);			
		} catch (IllegalAccessException e) {
			raiseAndLogError("Erro de acesso ao preparar remessa", e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof Exception) //Se é uma Exceção interna, então repassa...
				throw (Exception)e.getCause();
			else {
				raiseAndLogError("Erro de invocação ao preparar remessa", e);
			}
		}
	}
	
	private Remessa geraRemessa(Enquadramento enquadramento, EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, Date dataRemessa, boolean residual, int infracoes, int id_usuario, Inconsistencia inconsistencia, Integer idRemessaAutomatico) throws ServletException, SQLException {

		Class<? extends RemessaFactory> factoryType;
		Remessa ret = null;
		try {
			
			// Recupera qual é a implementação (da configuração).
			factoryType = ConfiguracaoProvider.getInstance().getImplementacaoGeraRemesssa();

			// Cria uma instância
			RemessaFactory factoryInstance = factoryType.newInstance();
			
			// Invoca a criação
			ret = factoryInstance.gerarRemessa(enquadramento, etapaProcesso, dataInicial, dataFinal, dataRemessa, residual, infracoes, id_usuario, inconsistencia, idRemessaAutomatico);
			
		} catch (ConfiguracaoException e) {
			raiseAndLogError("Erro de configuração ao gerar remessa.", e);
		} catch (SecurityException e) {
			raiseAndLogError("Erro de segurança ao gerar remessa.", e);
		} catch (ConexaoException e) {
			raiseAndLogError("Erro de conexão ao banco de dados ao gerar remessa.", e);			
		} catch (ModelException e) {
			raiseAndLogError("Erro de argumentos ao gerar remessa ou não há infrações", e);
		} catch (IllegalAccessException e) {
			raiseAndLogError("Erro de acesso ao gerar remessa.", e);
		} catch (InstantiationException e) {
			raiseAndLogError("Erro ao instanciar a fábrica de remessa.", e);
		}
		
		return ret;
	}	


	@SuppressWarnings("unused")
	private void preparaRemessaAutomatico(EtapaProcesso etapa, Date dtIni, Date dtFim) throws Exception {

		Class<? extends ExportaRemessa<?>> tipoRemessa;
		try {
			
			// Recupera qual é a implementação (da configuração).
			tipoRemessa = ConfiguracaoProvider.getInstance().getImplementacaoExportaRemesssa();

			// Recupera o método utilizando reflection.
			Method preparaRemessaAutomatico = tipoRemessa.getMethod("preparaRemessaAutomatico", EtapaProcesso.class, Date.class, Date.class);
			
			// Invoca o método
			preparaRemessaAutomatico.invoke(null, etapa, dtIni, dtFim);
			
		} catch (ConfiguracaoException e) {
			raiseAndLogError(e.getMessage() != null ? e.getMessage() : "Erro de configuração ao preparar remessa", e);
		} catch (SecurityException e) {
			raiseAndLogError("Erro de segurança ao preparar remessa", e);
		} catch (NoSuchMethodException e) {
			raiseAndLogError("Erro de reflexão ao preparar remessa", e);
		} catch (IllegalArgumentException e) {
			raiseAndLogError("Erro de argumentos ao preparar remessa", e);			
		} catch (IllegalAccessException e) {
			raiseAndLogError("Erro de acesso ao preparar remessa", e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof Exception) //Se é uma Exceção interna, então repassa...
				throw (Exception)e.getCause();
			else {
				raiseAndLogError("Erro de invocação ao preparar remessa", e);
			}
		}
	}
	
	private Boolean geraRemessaAutomatico(EtapaProcesso etapaProcesso, Date dataInicial, Date dataFinal, Date dataRemessa, boolean residual, int infracoes, int id_usuario) throws ServletException, SQLException {

		Class<? extends RemessaFactory> factoryType;
		Boolean bRet = null;
		try {
			
			// Recupera qual é a implementação (da configuração).
			factoryType = ConfiguracaoProvider.getInstance().getImplementacaoGeraRemesssa();

			// Cria uma instância
			RemessaFactory factoryInstance = factoryType.newInstance();
			
			// Invoca a criação
			bRet = factoryInstance.gerarRemessaAutomatico(etapaProcesso, dataInicial, dataFinal, dataRemessa, infracoes, id_usuario);
			
		} catch (ConfiguracaoException e) {
			raiseAndLogError(e.getMessage() != null ? e.getMessage() : "Erro de configuração ao gerar remessa.", e);
		} catch (SecurityException e) {
			raiseAndLogError("Erro de segurança ao gerar remessa.", e);
		} catch (ConexaoException e) {
			raiseAndLogError("Erro de conexão ao banco de dados ao gerar remessa.", e);			
		} catch (ModelException e) {
			raiseAndLogError(e.getMessage() != null ? e.getMessage() : "Erro de argumentos ao gerar remessa ou não há infrações", e);			
		} catch (IllegalAccessException e) {
			raiseAndLogError("Erro de acesso ao gerar remessa.", e);
		} catch (InstantiationException e) {
			raiseAndLogError("Erro ao instanciar a fábrica de remessa.", e);
		}
		
		return bRet;
	}
	
	
	private void raiseAndLogError(String message, Throwable rootCause) throws ServletException {
		logger.error(message, rootCause);
		throw new ServletException(message, rootCause);
	}
	
}