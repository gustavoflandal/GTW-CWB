package com.consilux.servlet.processamento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.Acesso;
import com.consilux.model.AgendarProcessamentoThread;
import com.consilux.model.JobBuscaRemessasPendentes;
import com.consilux.model.JobEnviaMovimentoValidado;
import com.consilux.model.LogRemessa;
import com.consilux.model.Mensagem;
import com.consilux.model.Processamento.EtapaProcesso;
import com.consilux.model.Remessa;
import com.consilux.model.RemessaIteracao;
import com.consilux.model.Usuario;
import com.consilux.servlet.remessa.ExportarRemessaTarefa;
import com.consilux.servlet.remessa.ExportarRemessaTarefa.TipoExportaRemessa;

/**
 * Servlet implementation class LiberarMovimentosLote
 */
public class LiberarMovimentosLote extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static ExecutorService service = Executors.newFixedThreadPool(20);
	private static ExecutorService serviceEnvioAPait = Executors.newFixedThreadPool(30);

	private static Logger logger = Logger
			.getLogger(LiberarMovimentosLote.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LiberarMovimentosLote() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true);
		LogRemessa logRemessa = new LogRemessa();

		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!");
			return; // O usuário não tem acesso...então cai fora!
		}
		
		Usuario usuario_atual = acesso.getUsuario();

		logger.info(acesso.getUsuario().getNome());
		StringBuffer requestURL = request.getRequestURL();
		if (request.getQueryString() != null) {
		    requestURL.append("?").append(request.getQueryString());
		}
		String completeURL = requestURL.toString();
		logger.info(completeURL);
		
		// /processo/LiberarMovimentosLote?sel_movimento=2024&sel_movimento=2026&sel_movimento=3003&mov_liberar=1
		// /processo/LiberarMovimentosLote?sel_movimento=2024&sel_movimento=2026&sel_movimento=3003&mov_enviar=1
		String mov_liberar = request.getParameter("mov_liberar");
		String mov_enviar = request.getParameter("mov_enviar");
		String mov_limpar = request.getParameter("mov_limpar");
		String mov_bloquear = request.getParameter("mov_bloquear");
		String mov_desbloquear = request.getParameter("mov_desbloquear");
		
		mov_liberar 	= 	mov_liberar == null 	|| mov_liberar.trim().equals("") 		? null : mov_liberar.trim();
		mov_enviar 		= 	mov_enviar == null 		|| mov_enviar.trim().equals("") 		? null : mov_enviar.trim();
		mov_limpar 		= 	mov_limpar == null 		|| mov_limpar.trim().equals("") 		? null : mov_limpar.trim();
		mov_bloquear 	= 	mov_bloquear == null 	|| mov_bloquear.trim().equals("") 		? null : mov_bloquear.trim();
		mov_desbloquear = 	mov_desbloquear == null || mov_desbloquear.trim().equals("") 	? null : mov_desbloquear.trim();

		String desbloquear_ultimo = request.getParameter("desbloquear_ultimo");
		String[] movimentos = request.getParameterValues("sel_movimento");

		Boolean desbloquearUltimoAuditor = desbloquear_ultimo == null ? false : Boolean.parseBoolean(desbloquear_ultimo);
		
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		response.getWriter().write("<link rel=\"stylesheet\" href=\"/css/gtw.css\" media=\"screen\" type=\"text/css\">");
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: center;\" >");
		
		logger.info("Resultado do Processamento de Movimentos de Lote");
		response.getWriter().write("<h3>Resultado do Processamento de Movimentos de Lote</h3><BR/>");
		
		response.getWriter().write("<body onunload=\"window.opener.location.reload(true)\">");
		
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: left; width: 600; margin: auto;\" >");
		
		if(movimentos == null || movimentos.length == 0) {
			logger.error("Nenhum Movimento de Lote foi Selecionado!");
			response.getWriter().write("Nenhum Movimento de Lote foi Selecionado!<BR/>");
		}else { 
			
			if (mov_limpar != null && mov_limpar.equals("1")) {
				
				try{
					Boolean existeAmostra = false;
					Boolean resultado = false;
					
					for (String movimento : movimentos) {
						Integer idRemessa = Integer.parseInt(movimento);
						Remessa remessa = Remessa.buscarRemessaPorId(idRemessa);
						
						response.getWriter().write("<BR/>");
						logger.info(remessa.getDescricaoApaitAlt() + " : Limpando amostra do Movimento de Lote<BR/>");
						response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Limpando amostra do Movimento de Lote<BR/>");
						
						existeAmostra = remessa.getTamanhoAmostraReal() > 0;
						
						if (existeAmostra) {
							resultado = Remessa.limparAmostraValidacao(idRemessa);
							JobBuscaRemessasPendentes.AtualizarRemessa(idRemessa);
						}else {
							response.getWriter().write("<BR/>");
							logger.info(remessa.getDescricaoApaitAlt() + " : Não existe amostra para o Movimento de Lote<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Não existe amostra para o Movimento de Lote<BR/>");
						}
						
						
						if (resultado) {
							response.getWriter().write("<BR/>");
							logger.info(remessa.getDescricaoApaitAlt() + " : Limpeza da amostra do Movimento de Lote finalizada com sucesso<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Limpeza da amostra do Movimento de Lote finalizada com sucesso<BR/>");
						}else if (!resultado && existeAmostra) {
							response.getWriter().write("<BR/>");
							logger.info(remessa.getDescricaoApaitAlt() + " : Não foi possível limpar a amostra do Movimento de Lote<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Não foi possível limpar a amostra do Movimento de Lote<BR/>");
						}
						
					}
					
				} catch (Exception e) {
					logger.error("ERRO ao limpar amostra do Movimento de Lote!", e);
				}
				
			} else if ((mov_bloquear != null && mov_bloquear.equals("1")) || (mov_desbloquear != null && mov_desbloquear.equals("1"))) {
				
				try {
					List<Integer> i_movimentos = new ArrayList<Integer>();
					for(String mov : movimentos) {
						i_movimentos.add(Integer.parseInt(mov));
					}
					
					for (String movimento : movimentos) {
						Integer idRemessa = Integer.parseInt(movimento);
						Remessa remessa = Remessa.buscarRemessaPorId(idRemessa);
						
						if (mov_bloquear != null && mov_bloquear.equals("1")) {
							response.getWriter().write("<BR/>");
							logger.info(remessa.getDescricaoApaitAlt() + " : Bloqueando Movimento de Lote...<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Bloqueando Movimento de Lote...<BR/>");
							
							RemessaIteracao.BloqueioIteracao(usuario_atual.getId(), remessa);
							
							logger.info(remessa.getDescricaoApaitAlt() + " : Movimento de Lote bloqueado!<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Movimento de Lote bloqueado!<BR/>");
						}
						if (mov_desbloquear != null && mov_desbloquear.equals("1")) {
							
							response.getWriter().write("<BR/>");
							logger.info(remessa.getDescricaoApaitAlt() + " : Desbloqueando Movimento de Lote...<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Desbloqueando Movimento de Lote...<BR/>");
							
							RemessaIteracao.DesbloqueioIteracao(remessa, desbloquearUltimoAuditor);
							
							logger.info(remessa.getDescricaoApaitAlt() + " : Movimento de Lote desbloqueado!<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Movimento de Lote desbloqueado!<BR/>");
						}
					}
				} catch (Exception e) {
					logger.error("ERRO ao Bloquear/Desbloquear Movimento de Lote!", e);
				}
			}
			else {
				
				try {
					Integer finalizado = JobEnviaMovimentoValidado.FinalizarRemessasValidadas();
					if(finalizado > 0) {
						logger.info("Finalizado " + finalizado + " Movimentos de Lote<br /><br />");
						response.getWriter().write("Finalizado " + finalizado + " Movimentos de Lote<br /><br />");
					}
					for (String movimento : movimentos) {
						Integer idRemessa = Integer.parseInt(movimento);
						Remessa remessa = Remessa.buscarRemessaPorId(idRemessa);
		
						response.getWriter().write("<BR/>");
						logger.info(remessa.getDescricaoApaitAlt() + " : Processando Movimento de Lote<BR/>");
						response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Processando Movimento de Lote<BR/>");
						
						if(!remessa.getIntegridade()) {
							logger.info(remessa.getDescricaoApaitAlt() + " : " + remessa.getIntegridadeDescricao() + "<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : " + remessa.getIntegridadeDescricao() + "<BR/>");
							response.getWriter().write(remessa.getDescricaoApaitAlt() + " : " + remessa.getIntegridadeDescricaoExt() + "<BR/>");
						}else {
						
							if (mov_liberar != null && mov_liberar.equals("1")) {
									
								if(remessa.getTotalInfracoesValidaveis() == 0) {
									logger.info(remessa.getDescricaoApaitAlt() + " : Movimento de Lote já foi 100% auditado!<BR/>");
									response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Movimento de Lote já foi 100% auditado!<BR/>");
								}
								else if(remessa.getTamanhoAmostraReal() > 0 && remessa.getTamanhoAmostraValidavel() == 0) {
									AgendarProcessamentoThread apt = new AgendarProcessamentoThread(
											acesso.getUsuario().getId(),
											EtapaProcesso.VALIDACAO.getId(),
											Integer.parseInt(movimento));
									
									service.execute(apt);
									
									logger.info(remessa.getDescricaoApaitAlt() + " : Processamento Agendado com Sucesso!<BR/>");
									response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Processamento Agendado com Sucesso!<BR/>");
								}
								else {
									logger.info(remessa.getDescricaoApaitAlt() + " : Amostra do Movimento de Lote não foi selecionada/validada<BR/>");
									response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Amostra do Movimento de Lote não foi selecionada/validada<BR/>");
								}
							}
			
							if (mov_enviar != null && mov_enviar.equals("1")) {
			
								if (remessa.getDataValidacao() == null) {
									logger.info(remessa.getDescricaoApaitAlt() + " : Movimento de Lote ainda não foi 100% Validado<BR/>");
									response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Movimento de Lote ainda não foi 100% Validado<BR/>");
								} else {
									String diretorio = ConfiguracaoProvider.getInstance()
											.getConfiguracaoChaveValor()
											.get("diretorio_apait");
									if (diretorio != null && diretorio.trim().length() > 0) {
										ExportarRemessaTarefa ert = new ExportarRemessaTarefa(
												remessa, TipoExportaRemessa.LoteValidado);
										serviceEnvioAPait.execute(ert);
										
										logger.info(remessa.getDescricaoApaitAlt() + " : Movimento enviado com Sucesso!<BR/>");
										response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Movimento enviado com Sucesso!<BR/>");
										
										if (serviceEnvioAPait instanceof ThreadPoolExecutor) {
										    logger.info("Quantidade de Threads ocupadas no ExcuterService: " + ((ThreadPoolExecutor) serviceEnvioAPait).getActiveCount());
										    logRemessa.insereLogRemessa(idRemessa, "Exportação Manual APAIT", "Movimento enviado(agendado) com Sucesso! Quantidade de Threads ocupadas no ExcuterService: " + ((ThreadPoolExecutor) serviceEnvioAPait).getActiveCount());
										}	
									} 
									else 
									{
										logger.info(remessa.getDescricaoApaitAlt() + " : Erro de configuração, diretório APAIT não informado!<BR/>");
										response.getWriter().write(remessa.getDescricaoApaitAlt() + " : Erro de configuração, diretório APAIT não informado!<BR/>");
									}
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("ERRO ao Liberar Movimento de Lote!", e);
				}
			}
		}
		response.getWriter().write("</div>");
		
		response.getWriter().write("<BR/>");
		response.getWriter().write("<button onclick=\"window.close()\">Retornar a tela de Seleção</button>");
		response.getWriter().write("</div>");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
