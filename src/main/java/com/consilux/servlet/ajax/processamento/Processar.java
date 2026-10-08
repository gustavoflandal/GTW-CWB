/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descrição: Servlet para envio de informações sobre infração.

  Histórico:

    $Log: Processar.java,v $
    Revision 1.22  2009/05/22 19:40:51  fos
    Colocado mais DEBUGs.

    Revision 1.21  2009/05/19 11:34:28  fos
    Ajustada a máscara da regexp de placa veículares no Brasil.

    Revision 1.20  2009/05/08 19:00:10  fos
    Agora armazena a espécie para realimentação do cadastro.

    Revision 1.19  2009/03/19 23:07:53  fos
    Agora possui controle para infrações em espera.

    Revision 1.18  2009/01/28 19:29:31  fos
    Substituídos asserts por exceções quando a sessão for interrompida.

    Revision 1.17  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.15  2008/09/22 11:41:13  fos
    Correção de acentuação.

    Revision 1.14  2008/09/19 20:57:29  fos
    ASSIGNED - bug 53: Criar regras de passagem entre processos com aviso para o usuário.
    http://bugzilla.consilux.net/show_bug.cgi?id=53

    Revision 1.13  2008/09/03 13:06:36  fos
    ASSIGNED - bug 92: Infração sem imagem
    http://bugzilla.consilux.net/show_bug.cgi?id=92

    Revision 1.12  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.11  2008/08/12 13:04:52  fos
    Agora armazena informação sobre a obliteração.

    Revision 1.10  2008/07/23 14:32:26  fos
    Repassa o id da imagem selecionada para o processamento.

    Revision 1.9  2008/05/14 21:46:53  fos
    Consertada mascara do código da invalidação.

    Revision 1.8  2008/05/09 21:17:44  fos
    Consertada mascara de validação da placa...agora o range vai até NLU****

    Revision 1.7  2008/05/08 21:33:40  fos
    Agora faz o processo adequado com múltiplas iterações.
    Agora faz a processamento da digitação e validação.
    Agora finaliza o processo.

    Revision 1.6  2008/04/02 14:08:59  fos
    Detalhes de sintaxe.

    Revision 1.5  2008/02/28 18:45:12  fos
    Agora recebe inconsistências também.
    Retirado a inicialização do processo deste servlet.

    Revision 1.4  2008/02/21 21:07:45  fos
    Implementado a consistência, próximo, anterior e atual.

    Revision 1.3  2008/02/18 21:10:46  fos
    Agora a triagem envia a etapa para o processamento.

    Revision 1.2  2008/02/06 19:20:26  fos
    Carga da infração na nova tela funcional.

    Revision 1.1  2008/01/18 17:21:47  fos
    Primeira versão postada no CVS.

    Revision 1.3  2008/01/17 17:10:56  fos
    Feito melhorias para retirar warnings.

    Revision 1.2  2007/04/17 17:49:10  fos
    Agora a chamada 'próximo' trás como padrão o código '0' para a invalidação.

    Revision 1.1  2007/04/11 12:09:19  fos
    Primeira versão postada no CVS.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.servlet.ajax.processamento;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Inconsistencia;
import com.consilux.model.Processamento;
import com.consilux.model.Remessa;
import com.consilux.model.Processamento.Alvo;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.exception.ModelException;

 /**
 * Servlet para envio de informações sobre infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.22 $ $Date: 2009/05/22 19:40:51 $ $Author: fos $
 */
public class Processar extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Processar.class); 
	private String avisoProc = null;
	private String mensagemProc = null;
	private Boolean bloqueio = null;
	private Integer idInfracaoProcesso = null;
	/**
	 * Constrói o objeto
	 */
	public Processar() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Date inicio = new Date();
		Acesso acesso = new Acesso(request, response, false); 
		
		this.avisoProc = null;
		this.mensagemProc = null;
		this.bloqueio = false;
		this.idInfracaoProcesso = null;
		
		try {

			if (!acesso.verificaAcesso(false)) {
				mostraMensagemErroUsuario(response, "Usuário não autenticado.");
				return;
			}
			else if (request.getSession().getAttribute("[processamento]") == null) {
				mostraMensagemErroUsuario(response, "Sessão encerrada; re-inicie o processamento.");
				return;
			}
			
			Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
			
			String sAcao = request.getParameter("acao");
			String sIdProcesso = request.getParameter("id_processo");
			String sIdInfracao = request.getParameter("infracao");
			
			String sIdRemessa = request.getParameter("id_remessa");
			String s_imagens_amostragem = request.getParameter("imagens_amostragem");
			
			if (sAcao == null || !Pattern.matches("[0-6]",sAcao))
				throw new ServletException("Identificador da ação enviado invalido!");
			logger.debug("sIdProcesso = " + sIdProcesso);
			if (sIdProcesso == null || !Pattern.matches("^(11|23|24|25|90|91|[0-9])$",sIdProcesso))
				throw new ServletException("Identificador de etapa enviado invalido!");
			if (sIdInfracao == null || !Pattern.matches("[0-9]{1,8}",sIdInfracao))
				throw new ServletException("Identificador da infração enviado invalido!");
	
			
			Processamento.Acao acao = Processamento.Acao.values()[Integer.parseInt(sAcao)];
			EtapaProcesso etapa = EtapaProcesso.valueOfId(Integer.valueOf(sIdProcesso));
			Integer idInfracao = Integer.valueOf(sIdInfracao);
			Integer iRet = null;
			
			long dt_in1 = Calendar.getInstance().getTimeInMillis();
			switch(acao) {
				case CONSISTE:
					iRet = consiste(request, etapa, idInfracao);
					break;
				case INCONSISTE:
					iRet = inconsiste(request, etapa, idInfracao);
					break;
				case ANTERIOR:
					iRet = anterior(request, etapa, idInfracao);
					break;
				case PROXIMO:
					iRet = proximo(request, etapa, idInfracao);
					break;
				case ATUAL:
					iRet = atual(request, etapa);
					break;
				case ESPERA:
					iRet = espera(request, etapa, idInfracao);
					break;
				case PENDENTE:
					iRet = proc.getIdInfracaoProcessada(); //??? Felipe
					proc.setInfracaoAtual(iRet);
					break;
			}
			logger.info("TempoProcessar " + acao + " " + (Calendar.getInstance().getTimeInMillis() - dt_in1));
			
			if (proc.isBotaoVoltarPressionado() && 
				proc.getIdInfracaoProcessada() != null && 
				proc.getIdInfracaoProcessada() == iRet) {
				proc.setBotaoVoltarPressionado(false);
			}
			logger.debug("anterior : iRet = " + iRet + "; Infracao Atual = " + proc.getIdInfracaoProcessada());
			
			//Verificar se o lote será reprovado
			Remessa remessa = null;
			Integer id_remessa = 0, errosProcessamento = 0, ac = 0, imagens_amostragem = -1;
			Boolean reprovar = false;
				
			if (sIdRemessa != null && s_imagens_amostragem != null) {
				dt_in1 = Calendar.getInstance().getTimeInMillis();
				
				try { 
					imagens_amostragem = Integer.parseInt(s_imagens_amostragem);
					if (imagens_amostragem >= 0)
					{
						id_remessa = Integer.parseInt(sIdRemessa);
						
						remessa = Remessa.buscarRemessaPorId(id_remessa);
						errosProcessamento = remessa.getErrosProcessamento();
						ac = remessa.getAc();
						
						if (errosProcessamento > ac) {
							reprovar = true;
						}else{
							reprovar = false;
						}
					}
				} catch (NumberFormatException err) {
					logger.error("Erro ao processar.", err);
					mostraMensagemErroUsuario(response, err.getMessage());
				} catch (ConexaoException err) {
					logger.error("Erro ao processar.", err);
					mostraMensagemErroUsuario(response, err.getMessage());
				} catch (SQLException err) {
					logger.error("Erro ao processar.", err);
					mostraMensagemErroUsuario(response, err.getMessage());
				}
				
				logger.info("TempoProcessar LoteReprovado " + (Calendar.getInstance().getTimeInMillis() - dt_in1));
			}
				
			AjaxXMLConstr xml = null;
			try {
				xml = new AjaxXMLConstr("infracao");
			}
			catch (ParserConfigurationException e) {
				throw new ServletException("Erro ao montar o XML!");
			}
			
			xml.adicCampo("ID_INFRACAO", iRet != null ? String.valueOf(iRet) : "");
			if (iRet != null) {
				xml.adicCampo("ID_INFRACAO_PROCESSO", String.valueOf(this.idInfracaoProcesso));
			}
			if (this.avisoProc != null) {
				logger.warn("AVISO: "+this.avisoProc);
				xml.adicCampo("AVISO", this.avisoProc);
			}
			if (this.mensagemProc != null) {
				logger.warn("MENSAGEM [BLOQ: "+this.bloqueio+"]: "+this.mensagemProc);
				xml.adicCampo("BLOQUEIO", String.valueOf(this.bloqueio));
				xml.adicCampo("MENSAGEM", this.mensagemProc);
			}
			
			xml.adicCampo("REPROVAR",reprovar ? "1" : "0");
			
			xml.dump(response);
		}
		catch(ServletException err) {
//			throw new ServletException("Erro ao processar requisição: "+err.getMessage());
			logger.error("Erro ao processar.", err);
			mostraMensagemErroUsuario(response, err.getMessage());
		}
		if (acesso.getUsuario() != null)
			logger.info("[TEMPO] Processar: "+(new Date().getTime() - inicio.getTime())+"...[" + acesso.getUsuario().getUsuario() + "]");
	}  	

	private Integer consiste(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		String sPlaca = request.getParameter("placa");
		String sIdMarcaProcesso = request.getParameter("id_marca_processo");
		String sIdEspecieProcesso = request.getParameter("id_especie_processo");
		String sUfProcesso = request.getParameter("uf_processo");
		String sIdImagem = request.getParameter("id_imagem");
		String sX = request.getParameter("obliteracao_x");
		String sTempo = request.getParameter("tempo");
		String sClassificacaoTarja = request.getParameter("classificacao_veiculo");
		String sIdEnquadramento = request.getParameter("id_enquadramento");
		String sPossuiCadastro = request.getParameter("possuiCadastro");
		Boolean possuiCadastro = Boolean.valueOf(sPossuiCadastro);
		
		if (sClassificacaoTarja != null) {
			sClassificacaoTarja = sClassificacaoTarja.trim() == "" ? null : sClassificacaoTarja.trim();
		}
		
		String observacao = request.getParameter("observacao");
		
		if(observacao != null && observacao.trim().length() > 0) {
			logger.debug("Recebeu Observacao [" + observacao + "]");
			proc.setObservacao(observacao);
		}
		else {
			proc.setObservacao(null);
		}
		
		if (sPlaca != null && proc.getEtapa().getId() == EtapaProcesso.TRIAGEM.getId())
			sPlaca = null;
		
		if (sPlaca != null && sPlaca.length() > 0 && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca))
			throw new ServletException("Placa enviada inválida!");

		if (sIdMarcaProcesso != null && !Pattern.matches("[0-9]{1,8}",sIdMarcaProcesso))
			throw new ServletException("Identificador da marca CET enviado invalido: "+sIdMarcaProcesso);

		if (sIdEspecieProcesso != null && !Pattern.matches("[0-9]{1,8}",sIdEspecieProcesso))
			throw new ServletException("Identificador da espécie enviado invalido: "+sIdEspecieProcesso);

		if (sUfProcesso != null && sUfProcesso.length() > 0 && !ExpValida.UF.validar(sUfProcesso))
			throw new ServletException("Identificador da uf enviado invalido: "+sUfProcesso);
		
		if (sIdImagem == null || !Pattern.matches("[0-9]{1,8}",sIdImagem))
			throw new ServletException("Identificador da imagem enviado invalido: "+sIdImagem);
				
		if (sTempo == null || !ExpValida.INTEIRO.validar(sTempo))
			throw new ServletException("Tempo enviado invalido: "+sTempo);

		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");

		try {
			
			if (!proc.verifInfracaoAtual(idInfracao)) {//Se não é a infração atual...tenta buscar novamente...
				if (proc.buscaInfracao(idInfracao, Alvo.ATUAL, null) != idInfracao.intValue())
					throw new ServletException("Erro de fluxo no sistema! Não foi possível garantir acesso exclusivo!");
			}
			if (sX != null) {
				throw new ModelException("Os valores de obliteração não acompanham mais o processamento!");
			}
			
			proc.setPlaca(sPlaca);
			
			if (Integer.valueOf(sIdImagem) > 0)
				proc.setIdImagem(Integer.valueOf(sIdImagem));
			
			if (sIdMarcaProcesso != null)
				proc.setIdMarcaProcesso(Integer.valueOf(sIdMarcaProcesso));

			if (sIdEspecieProcesso != null)
				proc.setIdEspecieProcesso(Integer.valueOf(sIdEspecieProcesso));
			
			if (sUfProcesso != null)
				proc.setUfProcesso(sUfProcesso);
			
			if (sClassificacaoTarja != null) {
				proc.setClassificacaoTarja(sClassificacaoTarja);
			} else {
				proc.setClassificacaoTarja(null);
			}
			
			proc.setIdInconsistencia(0);
			
			if (sTempo != null && Integer.parseInt(sTempo) > 0)
				proc.setTempoCliente(Integer.parseInt(sTempo));
			
			if (proc.processa()) {
				iRet = proc.buscaInfracao(idInfracao, null);
				proc.setIdInfracaoProcessada(iRet);
			}
			else
				iRet = 0;

			avisoProc = proc.popAviso();
			mensagemProc = proc.popMensagem();
			bloqueio = proc.getBloqueio();
			idInfracaoProcesso = proc.getIdInfracaoProcesso();
			
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao consistir a infração no banco de dados!", err);
		}
		
		return iRet;
	}
	
	private Integer inconsiste(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");

		String sIdInconsistencia = request.getParameter("id_inconsistencia");
		String sTempo = request.getParameter("tempo");
		String sPlaca = request.getParameter("placa");
		String sIdMarcaProcesso = request.getParameter("id_marca_processo");
		String sIdEspecieProcesso = request.getParameter("id_especie_processo");
		String sUfProcesso = request.getParameter("uf_processo");
		String sClassificacaoTarja = request.getParameter("classificacao_veiculo");
		String sIdEnquadramento = request.getParameter("id_enquadramento");
		String sPossuiCadastro = request.getParameter("possuiCadastro");
		Boolean possuiCadastro = Boolean.valueOf(sPossuiCadastro);
		
		if (sClassificacaoTarja != null) {
			sClassificacaoTarja = sClassificacaoTarja.trim() == "" ? null : sClassificacaoTarja.trim();
		}
		
		/// XXX: TODO: CONTESTACAO
		
		if (sPlaca != null && proc.getEtapa().getId() == EtapaProcesso.TRIAGEM.getId())
			sPlaca = null;
		
		if (sPlaca != null && sPlaca.length() > 0 && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca))
			throw new ServletException("Placa enviada inválida!");
		
		String observacao = request.getParameter("observacao");
		
		if(observacao != null && observacao.trim().length() > 0) {
			logger.debug("Recebeu Observacao [" + observacao + "]");
			proc.setObservacao(observacao);
		} else {
			proc.setObservacao(null);
		} 
		
		if (sIdInconsistencia == null || (etapa.getId() != 90 && etapa.getId() != 91 && !Pattern.matches("[1-9]|[1-9]([0-9]{1,8})",sIdInconsistencia)))
			throw new ServletException("Identificador da inconsistência enviado invalido!");

		if (sTempo == null || !ExpValida.INTEIRO.validar(sTempo))
			throw new ServletException("Tempo enviado invalido: "+sTempo);
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		try {
			Integer idInconsistencia = Integer.valueOf(sIdInconsistencia);
			
			String modo_cav = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_funcionalidade_cav");
			if(modo_cav != null && modo_cav.equals("0")) {
			
				if (idInconsistencia > 0 && !(sPlaca != null && sPlaca.trim().length() > 0)) {
					Inconsistencia incIsento = null;
					incIsento = Inconsistencia.buscaInconsistenciasIsento(idInconsistencia);
					
					if (incIsento != null) {
						throw new ServletException("Para uma inconsistencia de isenção, a placa deve ser informada!");
					}
				}
			}

			if (!proc.verifInfracaoAtual(idInfracao)) {//Se não é a infração atual...tenta buscar novamente...
				if (proc.buscaInfracao(idInfracao, Alvo.ATUAL, null) != idInfracao.intValue())
					throw new ServletException("Erro de fluxo no sistema! Não foi possível garantir acesso exclusivo!");
			}

			proc.setIdInconsistencia(Integer.valueOf(idInconsistencia));
			proc.setPlaca(sPlaca);
			
			if (sIdMarcaProcesso != null)
				proc.setIdMarcaProcesso(Integer.valueOf(sIdMarcaProcesso));

			if (sIdEspecieProcesso != null)
				proc.setIdEspecieProcesso(Integer.valueOf(sIdEspecieProcesso));
			
			if (sUfProcesso != null)
				proc.setUfProcesso(sUfProcesso);
			
			if (sClassificacaoTarja != null) {
				proc.setClassificacaoTarja(sClassificacaoTarja);
			} else {
				proc.setClassificacaoTarja(null);
			}
			
			if (sTempo != null && Integer.parseInt(sTempo) > 0)
				proc.setTempoCliente(Integer.parseInt(sTempo));

			if (proc.processa()) {
				iRet = proc.buscaInfracao(idInfracao, null);
				proc.setIdInfracaoProcessada(iRet);
			}
			else
				iRet = 0;
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao inconsistir a infração no banco de dados: "+err.getMessage(), err);
		}
		avisoProc = proc.popAviso();
		mensagemProc = proc.popMensagem();
		bloqueio = proc.getBloqueio();
		idInfracaoProcesso = proc.getIdInfracaoProcesso();
		
		return iRet;
	}
	
	private Integer anterior(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		proc.setBotaoVoltarPressionado(true);
		
		try {
			iRet = proc.buscaInfracao(idInfracao, Alvo.ANTERIOR, null);
			avisoProc = proc.popAviso();
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao buscar a infração anterior no banco de dados: "+err.getMessage(), err);
		}
		
		return iRet;
	}
	
	private Integer proximo(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		try {
			iRet = proc.buscaInfracao(idInfracao, Alvo.PROXIMO, null);
			avisoProc = proc.popAviso();
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao buscar a próxima infração no banco de dados: "+err.getMessage(), err);
		}
		
		return iRet;
	}
	
	@SuppressWarnings("unused")
	private Integer proximo_novo(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		try {
			iRet = proc.buscaInfracao(idInfracao, Alvo.PROXIMO_NOVO, null);
			avisoProc = proc.popAviso();
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao buscar a próxima infração no banco de dados: "+err.getMessage(), err);
		}
		
		return iRet;
	}
	
	private Integer atual(HttpServletRequest request, EtapaProcesso etapa) throws ServletException {
		Integer iRet = null;
		
		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		try {
			iRet = proc.buscaInfracao(null);
			avisoProc = proc.popAviso();
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao buscar a infração atual do banco de dados: "+err.getMessage(), err);
		}
		
		return iRet;
	}
	
	
	private Integer espera(HttpServletRequest request, EtapaProcesso etapa, Integer idInfracao) throws ServletException {
		Integer iRet = null;
		
		String sTempo = request.getParameter("tempo");

		Processamento proc = (Processamento) request.getSession().getAttribute("[processamento]");

		if (sTempo == null || !ExpValida.INTEIRO.validar(sTempo))
			throw new ServletException("Tempo enviado invalido: "+sTempo);
		
		if (proc == null)
			throw new ServletException("Processamento não iniciado!");
		
		try {
			if (sTempo != null && Integer.parseInt(sTempo) > 0)
				proc.setTempoCliente(Integer.parseInt(sTempo));

			proc.espera(idInfracao);
			avisoProc = proc.popAviso();
			iRet = proc.buscaInfracao(idInfracao, null);
		} 
		catch (Exception err) {
			throw new ServletException("Erro ao colocar a infração na espera: "+err.getMessage(), err);
		}
		
		return iRet;
	}
	
	private void mostraMensagemErroUsuario(HttpServletResponse response, String mensagem) {
		AjaxXMLConstr xml;
		try {
			xml = new AjaxXMLConstr("infracao");
			xml.adicCampo("ERRO", mensagem);
			xml.dump(response);
		}
		catch (Exception e) {
			logger.error("Erro ao entregar XML de resposta.", e);
		}
	}

	/**
	 * Obter dados da remessa
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @param codigo_externo, idEnquadramento
	 * Data: 16/06/2016
	 */
	public Integer obterRemessa(Long codigoExterno, Long idEnquadramento) throws ConexaoException, SQLException, ModelException {
		
		Integer id_remessa = 0;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" select   ");
		sbSQL.append(" 	r.id_remessa  ");
		sbSQL.append(" from remessa  r (NOLOCK) ");
		sbSQL.append(" where   ");
		sbSQL.append(" 	r.codigo_externo = ?  ");
		sbSQL.append(" 	and r.id_enquadramento = ?  ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, codigoExterno);
			ps.setLong(2, idEnquadramento);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				id_remessa = rs.getInt("id_remessa");
			}
				
		}catch (SQLException e) {
			logger.error("ERRO de SQL ao obterRemessa - Obter erros do lote.", e);
			throw new ModelException("ERRO de SQL ao obterRemessa - Obter erros do lote", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
		
		return id_remessa;
	}

}
