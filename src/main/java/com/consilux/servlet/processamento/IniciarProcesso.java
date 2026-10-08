/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Servlet para envio de informações sobre infração.

  Historico:

    $Log: IniciarProcesso.java,v $
    Revision 1.11  2009/06/01 18:42:51  fos
    Consertado expressão regular de inteiros.

    Revision 1.10  2009/03/19 23:08:48  fos
    Agora possui controle para infrações em espera e processamento direto.

    Revision 1.9  2009/03/03 21:40:08  fos
    Colocado filtro de consistência no processamento.

    Revision 1.8  2009/02/17 19:06:20  fos
    Colocado o identificador de liberação na validação da RegExp.

    Revision 1.7  2009/02/06 20:30:44  fos
    Feitos ajustes para filtrar por enquadramento.

    Revision 1.6  2009/01/12 12:49:52  fos
    Recuperação de repositório.

    Revision 1.4  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.3  2008/08/12 13:05:28  fos
    Busca a variável de usuário da forma correta.

    Revision 1.2  2008/05/08 21:34:34  fos
    Agora trabalha com a digitação e a validação também.

    Revision 1.1  2008/02/28 18:46:25  fos
    Primeira versão postada no CVS.

    Revision 1.4  2008/02/21 21:07:45  fos
    Implementado a consistencia, proximo, anterior e atual.

    Revision 1.3  2008/02/18 21:10:46  fos
    Agora a triagem envia a etapa para o processamento.

    Revision 1.2  2008/02/06 19:20:26  fos
    Carga da infração na nova tela funcional.

    Revision 1.1  2008/01/18 17:21:47  fos
    Primeira versão postada no CVS.

    Revision 1.3  2008/01/17 17:10:56  fos
    Feito melhorias para retirar warnings.

    Revision 1.2  2007/04/17 17:49:10  fos
    Agora a chamada 'proximo' traz como padrão o código '0' para a invalidação.

    Revision 1.1  2007/04/11 12:09:19  fos
    Primeira versão postada no CVS.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.servlet.processamento;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.exportalista.ExportaLista;
import com.consilux.exportalista.InfracaoCompletaBean;
import com.consilux.infra.SessaoFinalizaManager;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.Usuario;
import com.consilux.model.ValidarListaInfracoes;
import com.consilux.model.exception.ModelException;

/** 
 * Servlet para envio de informações sobre infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.11 $ $Date: 2009/06/01 18:42:51 $ $Author: fos $
 */
public class IniciarProcesso extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(IniciarProcesso.class); 
	/**
	 * Constrói o objeto
	 */
	public IniciarProcesso() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("incomplete-switch")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true); 

		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!");
			return; //O usuário não tem acesso...então cai fora!
		}

		String sProcessaDireto = request.getParameter("processa_direto");
		String sIdProcesso = request.getParameter("id_processo");
		String[] sIdInfracoes = request.getParameterValues("sel_infracao");

		String sDataInfracaoIni = request.getParameter("data_infracao_ini") != null ? request.getParameter("data_infracao_ini").trim() : null;
		sDataInfracaoIni = sDataInfracaoIni != null && sDataInfracaoIni.length() == 0 ? null : sDataInfracaoIni;

		String sHoraInfracaoIni = request.getParameter("hora_infracao_ini") != null ? request.getParameter("hora_infracao_ini").trim() : null;
		sHoraInfracaoIni = sHoraInfracaoIni != null && sHoraInfracaoIni.length() == 0 ? null : sHoraInfracaoIni;

		String sDataInfracaoFim = request.getParameter("data_infracao_fim") != null ? request.getParameter("data_infracao_fim").trim() : null;
		sDataInfracaoFim = sDataInfracaoFim != null && sDataInfracaoFim.length() == 0 ? null : sDataInfracaoFim;

		String sHoraInfracaoFim = request.getParameter("hora_infracao_fim") != null ? request.getParameter("hora_infracao_fim").trim() : null;
		sHoraInfracaoFim = sHoraInfracaoFim != null && sHoraInfracaoFim.length() == 0 ? null : sHoraInfracaoFim;

		String sInfracoesPreSelecionadas = request.getParameter("infracoes_pre_selecionadas") != null ? request.getParameter("infracoes_pre_selecionadas").trim() : null;
		sInfracoesPreSelecionadas = sInfracoesPreSelecionadas != null && sInfracoesPreSelecionadas.length() == 0 ? null : sInfracoesPreSelecionadas;

		logger.debug("sIdProcesso = " + sIdProcesso);
		if (sIdProcesso == null || !Pattern.matches("^(11|23|24|25|90|91|[0-9])$",sIdProcesso)) {
			new Mensagem(response).showErro("Identificador de etapa enviado invalido!");
			return;
		}
		
		String sIdRemessa = request.getParameter("id_remessa") != null ? request.getParameter("id_remessa").trim() : null;
		sIdRemessa = sIdRemessa != null && sIdRemessa.length() == 0 ? null : sIdRemessa;

		String sAmostra = request.getParameter("amostra") != null ? request.getParameter("amostra").trim() : null;
		Boolean amostra = sAmostra != null && sAmostra.equals("true");
		logger.debug("sAmostra = " + sAmostra + "; amostra = " + amostra);
		
		String sIdEnquadramento = request.getParameter("id_enquadramento") != null ? request.getParameter("id_enquadramento").trim() : null;
		sIdEnquadramento = sIdEnquadramento != null && sIdEnquadramento.length() == 0 ? null : sIdEnquadramento;

		if (sDataInfracaoIni != null && sDataInfracaoFim != null && sHoraInfracaoIni == null && sHoraInfracaoFim == null) {
			sHoraInfracaoIni = "00:00";
			sHoraInfracaoFim = "23:59";
		}
		if (sIdEnquadramento != null && !Pattern.matches("[0-9]{0,9}",sIdEnquadramento)) {
			new Mensagem(response).showErro("Identificador de enquadramento enviado inválido!");
			return;
		}
		if (sDataInfracaoIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoIni)) {
			new Mensagem(response).showErro("Data inicial do período enviada inválida!");
			return;
		}
		if (sDataInfracaoFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoFim)) {
			new Mensagem(response).showErro("Data inicial do período enviada inválida!");
			return;
		}
		if (sHoraInfracaoIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoIni)) {
			new Mensagem(response).showErro("Hora inicial do período enviada inválida!");
			return;
		}
		if (sHoraInfracaoFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoFim)) {
			new Mensagem(response).showErro("Hora final do período enviada inválida!");
			return;
		}
		if ((sDataInfracaoIni != null || sHoraInfracaoIni != null || sDataInfracaoFim != null || sHoraInfracaoFim != null) &&
				(sDataInfracaoIni == null || sHoraInfracaoIni == null || sDataInfracaoFim == null || sHoraInfracaoFim == null)) {
			new Mensagem(response).showErro("Período incompleto!");
			return;
		}
		if (sInfracoesPreSelecionadas != null && !Pattern.matches("[0-9]{0,8}",sInfracoesPreSelecionadas)) {
			new Mensagem(response).showErro("Campo infrações pré selecionadas enviado inválido!");
			return;
		}

		String sConsistencia = request.getParameter("consistencia") != null ? request.getParameter("consistencia").trim() : null;
		sConsistencia = sConsistencia != null && sConsistencia.length() == 0 ? null : sConsistencia;

		if (sConsistencia != null && !Pattern.matches("[0-2]",sConsistencia)) {
			new Mensagem(response).showErro("Seleção de consistência inválida!");
			return;
		}

		String sEspera = request.getParameter("espera") != null ? request.getParameter("espera").trim() : null;
		sEspera = sEspera != null && sEspera.length() == 0 ? null : sEspera;

		String sNumImagens = request.getParameter("num_imagens") != null ? request.getParameter("num_imagens").trim() : null;
		sNumImagens = sNumImagens != null && sNumImagens.length() == 0 ? null : sNumImagens;

		Boolean espera = sEspera != null ? (sEspera.equals("true") ? true : sEspera.equals("false") ? false : null) : null;

		Integer idRemessa = sIdRemessa != null ? Integer.valueOf(sIdRemessa) : null;
		//idRemessa = idRemessa != null && idRemessa > 0 ? idRemessa : null;
		
		Boolean existeJanela = false;
		Remessa remessa = null;
		
		try {
			if (idRemessa != null && idRemessa > 0) {
				existeJanela = Remessa.verificarJanelaValidacao(idRemessa);
			}
			
			if (existeJanela) {
				remessa = Remessa.buscarJanelaValidacao(idRemessa);
				
				if (acesso.getUsuario().getId() != remessa.getIdUsuarioJanela()) {
					new Mensagem(response).showErro("O auditor " + remessa.getNomeUsuarioJanela() + " está validando este lote no momento. Por favor, selecione outro lote para validar!");
					return;
				}
			}
			
		} catch (ConexaoException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ModelException e) {
			e.printStackTrace();
		}
		
		Integer idEnquadramento = sIdEnquadramento != null ? Integer.valueOf(sIdEnquadramento) : null;
		idEnquadramento = idEnquadramento != null && idEnquadramento > 0 ? idEnquadramento : null;

		Boolean consistencia = null;
		if (sConsistencia == null || sConsistencia.equals("0"))	
			consistencia = null;
		else if (sConsistencia.equals("1"))	
			consistencia = true;
		else if (sConsistencia.equals("2"))	
			consistencia = false;

		if (sIdInfracoes == null) { //Se o parâmetro não foi passado, consideramos que nenhuma infração foi selecionada.
			sIdInfracoes = new String[0];
		}

		Boolean processaDireto = (sProcessaDireto != null && sProcessaDireto.equals("1"));

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

		Integer numImagens = sNumImagens != null ? Integer.valueOf(sNumImagens) : null;
		numImagens = numImagens != null && numImagens > 0 ? numImagens : null;

		List<Integer> lInfracoes = new ArrayList<Integer>();
		for (String sIdInfracao: sIdInfracoes) {
			if (!Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao)) {
				new Mensagem(response).showErro("Infração enviada invalida!");
				return;
			}
			else
				lInfracoes.add(Integer.valueOf(sIdInfracao));
		}

		//Verificando se não teve pré selecionadas...
		if (lInfracoes.size() == 0) {
			if (sInfracoesPreSelecionadas != null && Integer.valueOf(sInfracoesPreSelecionadas) > 0) {
				List<? extends ExportaLista> infracoesSel = InfracaoCompletaBean.getListaParaRelatorio();
				for (ExportaLista e: infracoesSel) {
					lInfracoes.add(((InfracaoCompletaBean) e).getId());
				}
			}
		}

		if (numImagens != null && lInfracoes.size() < 1) {
			new Mensagem(response).showErro("Selecione manualmente as infrações!");
			return;
		}

		EtapaProcesso etapa = EtapaProcesso.valueOfId(Integer.valueOf(sIdProcesso));
		
		if (amostra) {
			try {
				Remessa.ObterAmostraRemessa(idRemessa);
			} catch (ConexaoException e) {
				logger.error("Erro de conexão ao obter amostra.", e);
				throw new ServletException("Erro de conexão ao obter amostra.", e);
//				e.printStackTrace();
			} catch (SQLException e) {
				logger.error("Erro de SQL ao obter amostra.", e);
				throw new ServletException("Erro de SQL ao obter amostra.", e);
//				e.printStackTrace();
			}			
		}

		try {
			inicia(request, etapa, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim, numImagens, lInfracoes);

			if (processaDireto) {

					Configuracao conf = ConfiguracaoProvider.getInstance();
					Integer idGrupoGerenteAuditores = conf.getIdGrupoGerenteAuditores();
					Integer idGrupoDesenvolvedores = conf.getIdGrupoDesenvolvedores();
					Integer idGrupoSupervisores = Integer.parseInt(conf.getConfiguracaoChaveValor().get("grupo_supervisor"));
					Boolean pertenceGrupoGerenteAuditores = Usuario.usuarioPertenceAoGrupo(acesso.getUsuario().getId(), idGrupoGerenteAuditores);
					Boolean pertenceGrupoDesenvolvedores = Usuario.usuarioPertenceAoGrupo(acesso.getUsuario().getId(), idGrupoDesenvolvedores);
					Boolean pertenceGrupoSupervisores = Usuario.usuarioPertenceAoGrupo(acesso.getUsuario().getId(), idGrupoSupervisores);
					Boolean acessoOk = (pertenceGrupoDesenvolvedores || pertenceGrupoGerenteAuditores || pertenceGrupoSupervisores);
					Boolean permiteValidacaoConsistentes = acessoOk && conf.isComValidacaoConsistentesAgendamento();
					Boolean permiteValidacaoInconsistentes = acessoOk;

					// Se o contrato permite validação nas consistentes e inconsistentes, então passa direto
					if (permiteValidacaoConsistentes && permiteValidacaoInconsistentes) {
						response.sendRedirect("/processo/processar_infracao_direto.jsp");
					}
					else
					{
						// Caso contrário, verifica se as infrações foram selecionadas individualmente...
						ValidarListaInfracoes vli = new ValidarListaInfracoes();				
						List<Integer> listaConsistentes = new ArrayList<Integer>();
						if(lInfracoes.size() > 0){
							listaConsistentes = vli.buscaConsistentes(lInfracoes);
						}							

						//Se as infrações foram selecionadas individualmente, e não têm nenhuma consistente passa...
						//OU Se foi selecionado o filtro de inconsistente...também passa...
						if (permiteValidacaoInconsistentes && (
								(lInfracoes.size() > 0 && listaConsistentes.size() == 0) ||
								(consistencia != null && !consistencia))
							) {
							response.sendRedirect("/processo/processar_infracao_direto.jsp");
						} //Não vai passar, verificando se o motivo foi uma consistente na seleção manual...
						else if (!permiteValidacaoConsistentes && listaConsistentes.size() > 0){
							new Mensagem(response).showErro("Não é possivel \"Processar Direto\" com infrações consistentes!<br>Foram encontradas "+listaConsistentes.size()+" infrações consistentes.");
							logger.warn("Tentando processar direto infrações consistentes! ["+acesso.getUsuario().getUsuario()+"]");
							return;
						} //Não vai passar porque não foi selecionado o filtro inconsistente!
						else {
							new Mensagem(response).showErro("Não é possivel \"Processar Direto\" com infrações consistentes!<br>Foram encontradas "+listaConsistentes.size()+" infrações consistentes.");
							logger.warn("Tentando processar direto infrações consistentes! ["+acesso.getUsuario().getUsuario()+"]");
							return;
						}							
//					}
//					break;
//
//					// Se não está na validação então passa...
//				default:
//					response.sendRedirect("/processo/processar_infracao_direto.jsp");
//				break;
				}
			}
			else {
				switch (etapa) {
				case TRIAGEM:
					response.sendRedirect("/processo/processar_infracao_triagem.jsp");
					break;
				case DIGITACAO:
					response.sendRedirect("/processo/processar_infracao_digitacao.jsp");
					break;
				case VALIDACAO:
					response.sendRedirect("/processo/processar_infracao_validacao.jsp");
					break;
				case REMESSA_VALIDADA:
					response.sendRedirect("/processo/processar_infracao_validacao.jsp");
					break;
				case LIBERACAO:
					response.sendRedirect("/processo/processar_infracao_liberacao.jsp");
					break;
				case IMAGENS_TESTE:
					response.sendRedirect("/processo/processar_imgteste_digitacao.jsp");
					break;
				case DIGITACAO_SUPERVISOR:
					response.sendRedirect("/processo/processar_infracao_digitacao_supervisor.jsp");
					break;
				case CONTESTACAO:
					response.sendRedirect("/processo/processar_infracao_contestacao.jsp");
					break;
				case CONTESTACAO_CAV:
					response.sendRedirect("/processo/processar_infracao_contestacao.jsp");
					break;
				}
			}
		}
		catch(Exception err) {
			//			throw new ServletException("Erro ao processar requisição: "+err.getMessage());
			err.printStackTrace();
			throw new ServletException(err);
		}

	}  	

	private void inicia(HttpServletRequest request, EtapaProcesso etapa, Integer idRemessa, Boolean amostra, Integer idEnquadramento, Boolean consistencia, Boolean espera, Timestamp periodoIni, Timestamp periodoFim, Integer numImagens, List<Integer> infracoes) throws ServletException {
		Integer idUsuario = ((Usuario)request.getSession().getAttribute("[usuario]")).getId();
		Processamento proc = null;

		//Caso não exista cria um novo processamento na sessão.
		if (infracoes.size() > 0)
			proc = Processamento.iniciaProcessamento(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim, infracoes);
		else if (numImagens != null)
			throw new ServletException("Não é possível processar imagens com filtro 'numero de img local/pista' sem seleção prévia.");
		else
			try {
				proc = Processamento.iniciaProcessamento(etapa, idUsuario, idRemessa, amostra, idEnquadramento, consistencia, espera, periodoIni, periodoFim);
			} catch (Exception e) {
				logger.error("Erro ao iniciar processamento.", e);
				throw new ServletException("Erro ao iniciar o processamento. ", e);
			}

		request.getSession().setAttribute("[processamento]", proc);
		SessaoFinalizaManager.adicSessaoFinaliza(request.getSession(), proc);
	}
	
	@SuppressWarnings("unused")
	private Boolean verificaDataProcessaDireto(EtapaProcesso etapa, Integer idEnquadramento, Boolean consistencia, Timestamp periodoIni) throws ServletException {
		Boolean bRet = false;
		
		try {
			bRet = Processamento.isPrimeiraDataDoProcesso(etapa, idEnquadramento, consistencia, periodoIni);
		} 
		catch (Exception e) {
			logger.error("Erro ao verificar a data do processamento.", e);
			throw new ServletException("Erro ao verificar a data do processamento.", e);
		}
		
		return bRet;
	}
}
