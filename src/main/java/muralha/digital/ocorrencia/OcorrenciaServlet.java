/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 06/10/2021

*********************************************************************************/

package muralha.digital.ocorrencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.consulta.StatusAlertaOcorrencia;
import muralha.digital.monitorado.VeiculosMonitorados;
import muralha.digital.notificacao.StatusNotificacao;
import muralha.digital.notificacao.TipoNotificacao;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.util.Resultado;

@WebServlet("/MuralhaDigital/Ocorrencia")
public class OcorrenciaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(OcorrenciaServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
		Integer idUsuario = null;
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		else
		{
			idUsuario = acessoUsuario.getUsuario().getId();
		}
		
		String msg = null;
		String strAcao = request.getParameter("acao");
		
		try
		{
			if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
	    	if(strAcao.equals("gerarOcorrencia"))	    	
	    		GerarOcorrencia(idUsuario, request, response);
	    	else if(strAcao.equals("salvarConfigAcaoProcedimento"))	    	
	    		GravarConfigAcaoProcedimento(idUsuario, request, response);
	    	else if(strAcao.equals("finalizarOcorrencia"))	    	
	    		FinalizarOcorrencia(idUsuario, request, response);
		    		
	    }
    	catch(Exception e)
    	{
    		logger.error("Erro no processo doPost() de requisição de irregularidade: " + e.getMessage(), e);
	    }
    }
    
	private void GerarOcorrencia(Integer idUsuario, HttpServletRequest request, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		String strIdAlerta = request.getParameter("idAlerta");
		String strIdTipoAlerta = request.getParameter("idTipoAlerta");
		String strIdCadMonitorado = request.getParameter("idCadMonitorado");
		
		try
    	{
			if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
	    	{
	    		msg = "Identificador do Alerta não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			if (strIdTipoAlerta == null || strIdTipoAlerta.equals("") || strIdTipoAlerta.equals("0")) 
	    	{
	    		msg = "Identificador do Tipo de Alerta não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			if (strIdCadMonitorado == null || strIdCadMonitorado.equals("") || strIdCadMonitorado.equals("0")) 
	    	{
	    		msg = "Identificador do Cadastro de Monitoramento não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idOcorrencia = UUID.randomUUID();
			UUID idAlerta = null, idTipoAlerta = null, idCadMonitorado = null;
			UUID idStatusAlertaOcorrenciaGerada = StatusAlertaOcorrencia.StatusAlerta.OCORRENCIA.GetID();
			UUID idStatusOcorrenciaPendente = StatusAlertaOcorrencia.StatusOcorrencia.EM_ABERTO.GetID();
			
			try
	    	{
				idAlerta = UUID.fromString(strIdAlerta.trim());
				idTipoAlerta = UUID.fromString(strIdTipoAlerta.trim());
				idCadMonitorado = UUID.fromString(strIdCadMonitorado.trim());				
			}
	    	catch (Exception e)
	    	{
				msg = "Erro ao preparar dados!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
		
			sucesso = Ocorrencias.GerarOcorrencia(idOcorrencia, idAlerta, idTipoAlerta, idStatusAlertaOcorrenciaGerada, idStatusOcorrenciaPendente, idUsuario);
			
			boolean possuiAlertaPendente = VeiculosMonitorados.PossuiAlertaPendente(idCadMonitorado);
			
			if (sucesso)
				msg = "Irregularidade confirmada com sucesso!";
			else
				msg = "Falha ao gerar irregularidade!";
			
			respostaXML.EnviarRespostaAlertaPendenteXML(response, sucesso, msg, idOcorrencia, possuiAlertaPendente);
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao gerar irregularidade!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void GravarConfigAcaoProcedimento(Integer idUsuario, HttpServletRequest request, HttpServletResponse response)
	{
		Resultado resultado = null;
		String msg = null;
		boolean possuiGrupoCad = false;
		
		String strIdOcorrencia = request.getParameter("idOcorrencia");
		String strGruposEmail = request.getParameter("gruposEmail");
		String strGruposSMS = request.getParameter("gruposSMS");
		String strPermiteAtendimento = request.getParameter("permiteAtendimento");
		String strPermiteAlterarAtendimento = request.getParameter("permiteAlterarAtendimento");
		
		try
    	{
			if (strIdOcorrencia == null || strIdOcorrencia.equals("") || strIdOcorrencia.equals("0")) 
	    	{
	    		msg = "Identificador da Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			possuiGrupoCad = ( !(strGruposEmail == null || strGruposEmail.equals("") || strGruposEmail.equals("0")) || !(strGruposSMS == null || strGruposSMS.equals("") || strGruposSMS.equals("0")) );
			
//			if (!possuiGrupoCad) 
//	    	{
//	    		msg = "Escolher pelo menos um grupo para notificação!";
//	    		logger.error(msg);	
//	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
//	    		return;
//	    	}
			
			Boolean permiteAtendimento = null;
			boolean permiteAlterarAtendimento = false;
			
			if (strPermiteAtendimento != null && !strPermiteAtendimento.trim().equals(""))
				permiteAtendimento = Boolean.parseBoolean(strPermiteAtendimento);
			
			if (strPermiteAlterarAtendimento != null && !strPermiteAlterarAtendimento.trim().equals(""))
				permiteAlterarAtendimento = Boolean.parseBoolean(strPermiteAlterarAtendimento);
			
			UUID idOcorrencia = null;
			UUID idTipoNotificacaoEmail = TipoNotificacao.Tipo.EMAIL.GetID(), idTipoNotificacaoSMS = TipoNotificacao.Tipo.SMS.GetID();
			UUID idStatusNotificacaoPendente = StatusNotificacao.Status.PENDENTE.GetID();
			List<Integer> gruposEmail = new ArrayList<Integer>();
			List<Integer> gruposSMS = new ArrayList<Integer>();
			
			try
	    	{
				idOcorrencia = UUID.fromString(strIdOcorrencia.trim());
				
				String[] gEmail = strGruposEmail.split(",");
				String[] gSMS = strGruposSMS.split(",");
				
				if (gEmail.length > 0)
				{
					for (String grupo : gEmail) {
						if (grupo != null && !grupo.trim().equals(""))
						{
							int idGrupo = Integer.parseInt(grupo);
							gruposEmail.add(idGrupo);
						}
					}
				}
				
				if (gSMS.length > 0)
				{
					for (String grupo : gSMS) {
						if (grupo != null && !grupo.trim().equals(""))
						{
							int idGrupo = Integer.parseInt(grupo);
							gruposSMS.add(idGrupo);
						}
					}
				}
				
				
			}
	    	catch (Exception e)
	    	{
				msg = "Erro ao preparar dados!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
		
			if (possuiGrupoCad)
				resultado = Ocorrencias.GravarConfigNotificacao(idOcorrencia, idTipoNotificacaoEmail, idTipoNotificacaoSMS, idStatusNotificacaoPendente, idUsuario, gruposEmail, gruposSMS);
			
			if (permiteAlterarAtendimento && (resultado == null || resultado.isSucesso()))
				resultado = Ocorrencias.AtualizarStatusAtendimento(idOcorrencia, permiteAtendimento, idUsuario);
			
			// Se resultado é nulo, significa que não teve nenhuma altereação para salvar, então retorna mensagem de sucesso para o usuário.
			resultado = resultado == null ? new Resultado(true, "") : resultado;
			
			if (resultado.isSucesso())
				msg = "Configuração de Ações e Procedimentos da Irregularidade gravada com sucesso!";
			else
				msg = "Falha ao gravar Configuração de Ações e Procedimentos da Irregularidade! " + resultado.getMensagem();
			
			respostaXML.EnviarRespostaRequisicaoXML(response, resultado.isSucesso(), msg);
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao gravar Configuração de Notificação da Irregularidade!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void FinalizarOcorrencia(Integer idUsuario, HttpServletRequest request, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		String strIdOcorrencia = request.getParameter("idOcorrencia");
		String strIdTipoOcorrencia = request.getParameter("idTipoOcorrencia");
		String strIdStatusOcorrencia = request.getParameter("idStatusOcorrencia");
		String strObsFinalizarOcorrencia = request.getParameter("obsFinalizarOcorrencia");
		
		try
    	{
			if (strIdOcorrencia == null || strIdOcorrencia.equals("") || strIdOcorrencia.equals("0")) 
	    	{
	    		msg = "Identificador da Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			if (strIdTipoOcorrencia == null || strIdTipoOcorrencia.equals("") || strIdTipoOcorrencia.equals("0")) 
	    	{
	    		msg = "Identificador do Tipo de Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			if (strObsFinalizarOcorrencia == null || strObsFinalizarOcorrencia == "") 
	    	{
	    		msg = "Observação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
			UUID idOcorrencia = null, idTipoOcorrencia = null, idStatusOcorrencia = null;
			
			try
	    	{
				idOcorrencia = UUID.fromString(strIdOcorrencia.trim());
				idTipoOcorrencia = UUID.fromString(strIdTipoOcorrencia.trim());
				idStatusOcorrencia = UUID.fromString(strIdStatusOcorrencia.trim());
				
				if (!StatusOcorrencias.isStatusOcorrenciaFinalizacao(idTipoOcorrencia, idStatusOcorrencia))
				{
					msg = "O status escolhido é imcompatível com este tipo de irregularidade. Contate o administrador do sistema!";
					logger.error(msg);	
					respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		    		return;
				}
			}
	    	catch (Exception e)
	    	{
				msg = "Erro ao preparar dados!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
		
			sucesso = Ocorrencias.FinalizarOcorrencia(idOcorrencia, idStatusOcorrencia, strObsFinalizarOcorrencia, idUsuario);
			
			if (sucesso)
				msg = "Irregularidade finalizada com sucesso!";
			else
				msg = "Falha ao finalizar irregularidade!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg, idOcorrencia);
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao finalizar irregularidade!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
}
