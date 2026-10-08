/**********************************************************************************

  Projeto: Muralha Digital
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 01/10/2021

*********************************************************************************/

package muralha.digital.consulta;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.consulta.StatusAlertaOcorrencia.StatusAlerta;
import muralha.digital.dispositivo.DispositivosEquipamentos;
import muralha.digital.util.Paginacao;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/AlertaOcorrencia")
public class AlertaOcorrenciaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AlertaOcorrenciaServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
    	//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
       	try
    	{
       		String msg = null;
			String strAcao = request.getParameter("acao");
	    	
	    	if (strAcao == null || strAcao == "") 
	    	{
	    		msg = "Ação não informada!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	else if(strAcao.equals("consultaPorFiltrosTela"))
	    		ConsultaPorFiltrosTela(request, response);
	    	
	    	
	    	else if(strAcao.equals("consultaPredefinida"))
	    		ConsultaPredefinida(request, response);

			
	    	else if( strAcao.equals("QuantitativosAlertasOcorrencias"))
			{
				String tipoRegistro 		= request.getParameter("tipoRegistro");
				UUID tipoAlertaOcorrencia 	= UUID.fromString(request.getParameter("tipoAlertaOcorrencia"));
				int tempoHoras 				= Integer.parseInt(request.getParameter("tempo"));
				
				DispositivosEquipamentos qtos = AlertasOcorrencias.ObterQuantitativosPorEquipamento ( tipoRegistro,
																									  tipoAlertaOcorrencia, 
																									  tempoHoras);
 				

				JAXBContext context = JAXBContext.newInstance(DispositivosEquipamentos.class);
				Marshaller marsHall = context.createMarshaller();
				marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				
				StringWriter sw = new StringWriter();
				marsHall.marshal(qtos, sw);
				String xml = sw.toString();
				sw.close();
				
				response.setHeader("Content-Type", "text/xml");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write(xml);
				response.getWriter().flush();
			}
	    	
	    	else if(strAcao.equals("consultaAlertasPendCadMonitorado"))
	    		ConsultaAlertasPorCadMonitorado(request, response, StatusAlerta.PENDENTE.GetID());
	    	
	    	else if(strAcao.equals("consultaAlertasPorCadMonitorado"))
	    		ConsultaAlertasPorCadMonitorado(request, response, null);
	    	
	    	else if(strAcao.equals("consultaAlertasPorVeiculo"))
	    		ConsultaAlertasPorVeiculo(request, response);
	    	
    	}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	private void ConsultaPorFiltrosTela(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strTipoRegistro = request.getParameter("tipoRegistro");
			String strTipoAlertaOcorrencia = request.getParameter("tipoAlertaOcorrencia");
			String strStatus = request.getParameter("status");
			String strPlaca = request.getParameter("placa");
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strEquipamento = request.getParameter("equipamento");
			String strPrivado = request.getParameter("privado");
			String strSupervisionado = request.getParameter("supervisionado");
			String strAssinadosPendentes = request.getParameter("assinadosPendentes");
			String strIdCadVeicMonitorado = request.getParameter("cadVeiculoMonitorado");
			String strIdRegistroFato = request.getParameter("idFato");
			String strIdUsuarioAlerta = request.getParameter("usuario");
			String strAssinado = request.getParameter("assinado");

			Paginacao paginacao;
			
	    	if (strTipoRegistro == null || strTipoRegistro.equals("") || strTipoRegistro.equals("0")) 
	    	{
	    		String msg = "Tipo não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
			
	    	strPlaca = strPlaca != null && strPlaca.trim().equals("") ? null : strPlaca.toUpperCase();
	    	
	    	if (strPlaca != null) {
	    	    String[] arrPlacas = strPlaca.split(",");
	    	    for (String p : arrPlacas) {
	    	        if (p != null && !p.trim().isEmpty()) {
	    	            String placaLimpa = p.trim().toUpperCase();

	    	            boolean valida = ExpValida.PLACA.validar(placaLimpa)
	    	                           || ExpValida.PLACA_MERCOSUL.validar(placaLimpa)
	    	                           || ExpValida.PLACA_PARCIAL.validar(placaLimpa);

	    	            if (!valida) {
	    	                String msg = "Placa informada inválida! (" + placaLimpa + ")";
	    	                logger.error(msg);	
	    	                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    	                return;
	    	            }
	    	        }
	    	    }
	    	}
	    	
	    	if ( ((strDataIni == null || strDataIni.trim() ==  "") && (strDataFim != null && strDataFim.trim() !=  "")) || 
	    			((strDataFim == null || strDataFim.trim() ==  "") && (strDataIni != null && strDataIni.trim() !=  "")) )
	    	{
	    		String msg = "Favor informar datas de início e fim da pesquisa!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}	    	
	    	
	    	paginacao = new Paginacao(request);
	    	if (!paginacao.OperacaoValida())
	    	{
	    		String msg = "Dados de paginação não informados corretamente!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	
	    	Integer idLocal = null;
	    	Integer idRegistroFato = null;
	    	Integer idUsuarioAlerta = null;
	    	Integer assinado = null;
			UUID idTipoRegistro = null, idTipoAlertaOcorrencia = null, idStatus = null;
			UUID idCadVeiculoMonitorado = null;  
			Date dataIni = null, dataFim = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			TipoRegistro.Tipo tipoRegistro = null;
			String funcaoSQL;
			boolean privado = "true".equalsIgnoreCase(strPrivado);
			boolean supervisionado = "true".equalsIgnoreCase(strSupervisionado);
			boolean assinadosPendentes = "true".equalsIgnoreCase(strAssinadosPendentes);
	    	try
	    	{
	    		if (strTipoRegistro != null && !strTipoRegistro.trim().equals(""))
	    		{
	    			idTipoRegistro = UUID.fromString(strTipoRegistro.trim());
					tipoRegistro = TipoRegistro.Tipo.GetValue(idTipoRegistro);
	    		}
	    		else if (tipoRegistro == null)
	    		{
	    			tipoRegistro = TipoRegistro.Tipo.ALERTA;
	    		}
	    		
	    		//Obter função SQL a ser consultada
	    		funcaoSQL = tipoRegistro.GetFuncaoSQL();
	    		
	    		
	    		if (strTipoAlertaOcorrencia != null && !strTipoAlertaOcorrencia.trim().equals("") && !strTipoAlertaOcorrencia.trim().equals("0"))
	    			idTipoAlertaOcorrencia = UUID.fromString(strTipoAlertaOcorrencia);
				
	    		
	    		if (strStatus != null && !strStatus.trim().equals("") && !strStatus.trim().equals("0"))
	    			idStatus = UUID.fromString(strStatus);
	    		
	    		if (strEquipamento != null && !strEquipamento.trim().equals("") && !strEquipamento.trim().equals("0"))
	    			idLocal = Integer.parseInt(strEquipamento);
	    		
	    		if (strIdCadVeicMonitorado != null && !strIdCadVeicMonitorado.trim().equals("") && !strIdCadVeicMonitorado.trim().equals("0"))
	    			idCadVeiculoMonitorado = UUID.fromString(strIdCadVeicMonitorado);
		    	
	    		if (strDataIni != null && !strDataIni.trim().equals("")) {
		    		strDataIni = strDataIni + ":00";
		    		dataIni = sdf.parse(strDataIni);
		    	}
		    	
	    		if (strDataFim != null && !strDataFim.trim().equals("")) {
	    			strDataFim = strDataFim + ":59";
	    			dataFim = sdf.parse(strDataFim);
		    	}
	    		if (strIdRegistroFato != null && !strIdRegistroFato.trim().equals("") && !strIdRegistroFato.trim().equals("0"))
	    			idRegistroFato = Integer.parseInt(strIdRegistroFato);
	    		
	    		if (strIdUsuarioAlerta != null && !strIdUsuarioAlerta.trim().equals("") && !strIdUsuarioAlerta.trim().equals("0"))
	    			idUsuarioAlerta = Integer.parseInt(strIdUsuarioAlerta);
	    		
	    		if (strAssinado != null && !strAssinado.trim().equals(""))
	    			assinado = Integer.parseInt(strAssinado);
	    		
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
	    	if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
	    	{
	    		String msg = "A data de início deve ser menor que a data fim!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
			Integer idUsuario = null;
			//Validando acesso do usuário		
			final Acesso acessoUsuario = new Acesso(request, response, true);
			idUsuario = acessoUsuario.getUsuario().getId();	
			ObterAlertasOcorrencias(funcaoSQL, idTipoAlertaOcorrencia, idStatus, strPlaca, dataIni, dataFim, idLocal,idUsuario,privado,supervisionado,assinadosPendentes,idCadVeiculoMonitorado, idRegistroFato, idUsuarioAlerta, assinado, paginacao, response);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	private void ObterAlertasOcorrencias(String funcaoSQL, UUID idTipoAlertaOcorrencia, UUID idStatus, String placa, Date dataIni, Date dataFim, Integer idLocal, Integer idUsuario,boolean privado, boolean supervisionado,boolean assinadosPendentes,UUID idCadVeiculoMonitorado, Integer idRegistroFato, Integer idUsuarioAlerta, Integer assinado, Paginacao paginacao, HttpServletResponse response)
	{
		try
		{
			AlertasOcorrencias alertasOcorrencias = AlertasOcorrencias.ObterListaAlertasOcorrencias(funcaoSQL, idTipoAlertaOcorrencia, idStatus, placa, dataIni, dataFim, idLocal, idUsuario,privado , supervisionado, assinadosPendentes, idCadVeiculoMonitorado, idRegistroFato, idUsuarioAlerta, assinado, paginacao);
			EnviarRespostaXML(response, alertasOcorrencias);
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void ConsultaPredefinida(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdLocal = request.getParameter("idLocal");
			String strTempo = request.getParameter("tempo");
			String strTipoRegistro = request.getParameter("tipoRegistro");
			String strTipoAlertaOcorrencia = request.getParameter("tipoAlertaOcorrencia");
			
			Paginacao paginacao;
			
	    	if (strIdLocal == null || strIdLocal.equals("") || strIdLocal.equals("0")) 
	    	{
	    		String msg = "Local não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if (strTempo == null || strTempo.equals("") || strTempo.equals("0")) 
	    	{
	    		String msg = "Período de consulta não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if (strTipoRegistro == null || strTipoRegistro.equals("") || strTipoRegistro.equals("0")) 
	    	{
	    		String msg = "Tipo não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	if (strTipoAlertaOcorrencia == null || strTipoAlertaOcorrencia.equals("") || strTipoAlertaOcorrencia.equals("0")) 
	    	{
	    		String msg = "Tipo de Alerta/Irregularidade não informado!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
	    	
	    	paginacao = new Paginacao(request);
	    	if (!paginacao.OperacaoValida())
	    	{
	    		String msg = "Dados de paginação não informados corretamente!";
	    		logger.error(msg);	
	    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
	    	
			
	    	Integer idLocal = null, tempo = null;
	    	UUID idTipoAlertaOcorrencia = null;
			Date dataIni = null, dataFim = null;
			TipoRegistro.Tipo tipoRegistro = null;
			String funcaoSqlAlt;
	    	
	    	try
	    	{
    			idLocal = Integer.parseInt(strIdLocal);
    			tempo = Integer.parseInt(strTempo);
    			
    			dataFim = new Date();
    			dataIni = new Date(dataFim.getTime() - TimeUnit.HOURS.toMillis(tempo));
				
	    		if (strTipoRegistro != null && !strTipoRegistro.trim().equals(""))
	    		{
	    			tipoRegistro = TipoRegistro.Tipo.GetValueByDesc(strTipoRegistro);
	    		}
	    		else if (tipoRegistro == null)
	    		{
	    			tipoRegistro = TipoRegistro.Tipo.ALERTA;
	    		}
	    		
	    		//Obter função SQL a ser consultada
	    		funcaoSqlAlt = tipoRegistro.GetFuncaoSqlAlt();
	    		
	    		idTipoAlertaOcorrencia = UUID.fromString(strTipoAlertaOcorrencia);
			}
	    	catch (Exception e)
	    	{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
			}
	    	
	    	if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
	    	{
	    		String msg = "A data de início deve ser menor que a data fim!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    		return;
	    	}
						
	    	ObterAlertasOcorrenciasPredefinida(funcaoSqlAlt, idLocal, dataIni, dataFim, idTipoAlertaOcorrencia, paginacao, response);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas/irregularidades!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ObterAlertasOcorrenciasPredefinida(String funcaoSqlAlt, Integer idLocal, Date dataIni, Date dataFim, UUID idTipoAlertaOcorrencia, Paginacao paginacao, HttpServletResponse response)
	{
		try
		{
			AlertasOcorrencias alertasOcorrencias = AlertasOcorrencias.ObterListaAlertasOcorrenciasAlt(funcaoSqlAlt, idLocal, dataIni, dataFim, idTipoAlertaOcorrencia, paginacao);
			EnviarRespostaXML(response, alertasOcorrencias);
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas/irregularidades!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	private void ConsultaAlertasPorCadMonitorado(HttpServletRequest request, HttpServletResponse response, UUID idStatusAlerta) throws ServletException, IOException 
	{
		String strCadMonitorado = request.getParameter("idCadMonitorado");
		
		Paginacao paginacao = new Paginacao(request);
		
    	if (strCadMonitorado == null || strCadMonitorado.equals("") || strCadMonitorado.equals("0")) 
    	{
    		String msg = "Cadastro de monitorado não informado!";
    		logger.error(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
    	}
		
		UUID idCadMonitorado = null;
    	
    	try
    	{
			idCadMonitorado = UUID.fromString(strCadMonitorado);
		}
    	catch (Exception e)
    	{
			String msg = "Erro ao preparar dados para consulta!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
		Integer idUsuario = null;
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		idUsuario = acessoUsuario.getUsuario().getId();
    	ObterAlertasPorCadMonitorado(idCadMonitorado, idStatusAlerta, idUsuario, paginacao, response);
	}
	
	private void ObterAlertasPorCadMonitorado(UUID idCadMonitorado, UUID idStatusAlerta, Integer idUsuario, Paginacao paginacao, HttpServletResponse response)
	{
		try
		{		
			boolean verificaAcesso = AlertasOcorrencias.VerificaAcessoPorCadVeiMoniUsuarioId(idCadMonitorado, idUsuario);
			if (!verificaAcesso) {
			    throw new SecurityException("Acesso negado: alerta privado e usuário não é o dono.");
			}
			
			AlertasOcorrencias alertasOcorrencias = AlertasOcorrencias.ObterAlertasPorCadMonitorado(idCadMonitorado, idStatusAlerta, paginacao);
			EnviarRespostaXML(response, alertasOcorrencias);
			
		}
		catch (SecurityException e) {
		    String msg = "Acesso negado: alerta privado e usuário não é o dono.";
		    respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		    return;
		} catch(Exception e)
		{
			String msg = String.format("Ocorreu um erro ao consultar alertas %s do cadastro de monitorado!", (idStatusAlerta != null ? "pendentes" : ""));
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	private void ConsultaAlertasPorVeiculo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String strIdVeiculo = request.getParameter("idVeiculo");
		
//		Paginacao paginacao = new Paginacao(request);
		
    	if (strIdVeiculo == null || strIdVeiculo.equals("") || strIdVeiculo.equals("0")) 
    	{
    		String msg = "Veículo não informado!";
    		logger.error(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
    	}
		
		UUID idVeiculo = null;
    	
    	try
    	{
			idVeiculo = UUID.fromString(strIdVeiculo);
		}
    	catch (Exception e)
    	{
			String msg = "Erro ao preparar dados para consulta!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
    	
    	ObterAlertasPorVeiculo(idVeiculo, response);
	}
	
	private void ObterAlertasPorVeiculo(UUID idVeiculo, HttpServletResponse response)
	{
		try
		{
			AlertasOcorrencias alertasOcorrencias = AlertasOcorrencias.ObterAlertasPorVeiculo(idVeiculo);
			EnviarRespostaXML(response, alertasOcorrencias);
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar alertas do veículo!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
		
	}
	
	
	private void EnviarRespostaXML(HttpServletResponse response, AlertasOcorrencias alertasOcorrencias) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(AlertasOcorrencias.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(alertasOcorrencias, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			alertasOcorrencias = null;
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de alertas/irregularidades!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}	
}
