/**********************************************************************************

	Projeto: Muralha Digital
	Nome do Modulo: GTW

	Empresa: Consilux Tecnologia

	Autor: Thiago Surgik
	Data: 06/10/2021

*********************************************************************************/

package muralha.digital.alerta;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import muralha.digital.consulta.StatusAlertaOcorrencia;
import muralha.digital.consulta.StatusAlertaOcorrencia.StatusAlerta;
import muralha.digital.monitorado.VeiculosMonitorados;
import muralha.digital.notificacao.ConfiguracaoSom;
import muralha.digital.notificacao.ConfiguracaoSons;
import muralha.digital.ocorrencia.Ocorrencias;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.veiculo.Veiculo;
import muralha.digital.veiculo.Veiculos;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebServlet("/MuralhaDigital/Alerta")
public class AlertaServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AlertaServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	private static UUID ID_MOTIVO_DESCARTE_ALERTA_VINCULADO = MotivoDescarte.Motivo.ALERTA_INVALIDO.GetID();
	private static String MSG_DESCARTE_ALERTA_VINCULADO = "Descartado no processo de tratamento de alertas vinculados.";
	private static UUID ID_STATUS_IRREGULARIDADE_EM_ABERTO = StatusAlertaOcorrencia.StatusOcorrencia.EM_ABERTO.GetID();
	
	private String emAtendimentoPor;
	
	public String getEmAtendimentoPor() {
		return emAtendimentoPor;
	}

	public void setEmAtendimentoPor(String emAtendimentoPor) {
		this.emAtendimentoPor = emAtendimentoPor;
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
    	//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		Integer idUsuario = null;
		String nomeUsuario = null;
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
			nomeUsuario = acessoUsuario.getUsuario().getNome();
		}
		
		try
		{
			String msg = null;
			String strAcao = request.getParameter("acao");
			String strIdOcorrencia = request.getParameter("idOcorrencia");
		
			if (strAcao == null || strAcao == "")
			{
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if(strAcao.equals("obterAlertaPorId"))
				ObterAlertaPorId(request, response);
			if(strAcao.equals("obterAlertasNaoTratadas"))
				ObterAlertasNaoTratados(request, response);
			if(strAcao.equals("obterCoordAlerta"))
				ObterCoordAlerta(request, response);
			if(strAcao.equals("obtemAlerta"))
				obtemAlerta(response, request);
			if (strAcao.equals("obterVeiculosMonitorados"))
				obterVeiculosMonitorados(request, response);
			if(strAcao.equals("obterAssinadoAlertaPorId"))
				ObterAssinadoAlertaPorId(request, response);
			if(strAcao.equals("obterAlertasPendentesAssinatura"))
				ObterAlertasPendentesAssinatura(request, response, idUsuario);

			if(strAcao.equals("obterAberturaAtendimento")) {
				if (strIdOcorrencia == null || strIdOcorrencia.isBlank()) {
					respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID Ocorrência não informado!");
					return;
				}
				ObterAberturaAtendimento(request, response, strIdOcorrencia);
			}
		}
	
		catch(Exception e)
		{
			logger.error("Erro no processo doPost() de requisição de Alerta: " + e.getMessage(), e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Integer idUsuario = null;
		String nomeUsuario = null;
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
			nomeUsuario = acessoUsuario.getUsuario().getNome();
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
			
			if(strAcao.equals("descartarAlerta"))
				DescartarAlerta(idUsuario, request, response);
			else if(strAcao.equals("processarAlertasVinculados"))
				ProcessarAlertasVinculados(idUsuario, request, response);
			else if (strAcao.equals("marcarComoEmAtendimento"))
				marcarComoEmAtendimento(nomeUsuario, response, request);
			else if (strAcao.equals("podeAtender"))
				podeAtender(nomeUsuario, response, request);
			else if (strAcao.equals("criarAlerta"))
				criarAlerta(response, request);
			else if (strAcao.equals("atualizarAlertaModalAcao"))
				atualizarAlertaModalAcao(idUsuario,request, response);
			}
		catch(Exception e)
		{
		logger.error("Erro no processo doPost() de requisição de alerta: " + e.getMessage(), e);
		}
	}
	
	private void DescartarAlerta(Integer idUsuario, HttpServletRequest request, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		String strIdAlerta = request.getParameter("idAlerta");
		String strIdMotivoDescarte = request.getParameter("idMotivoDescarte");
		String strObsDescarteAlerta = request.getParameter("obsDescarteAlerta");
		
		try
		{
			if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
			{
				msg = "Identificador do Alerta não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if (strIdMotivoDescarte == null || strIdMotivoDescarte.equals("") || strIdMotivoDescarte.equals("0")) 
			{
				msg = "Motivo do descarte não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if (strObsDescarteAlerta == null || strObsDescarteAlerta == "") 
			{
				msg = "Observação não informada!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
		
			UUID idAlerta = null, idMotivoDescarte = null, idStatusAlertaDescartado = null;

			try
			{
				idAlerta = UUID.fromString(strIdAlerta.trim());
				idMotivoDescarte = UUID.fromString(strIdMotivoDescarte.trim());
				idStatusAlertaDescartado = StatusAlerta.DESCARTADO.GetID();
			}
			catch (Exception e)
			{
				msg = "Erro ao preparar dados!";
				logger.error(msg + ": " + e.getMessage(), e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
		
			sucesso = Alertas.DescartarAlerta(idAlerta, idStatusAlertaDescartado, idMotivoDescarte, strObsDescarteAlerta, idUsuario);
			
			if (sucesso)
				msg = "Alerta descartado com sucesso!";
			else
				msg = "Falha ao descartar alerta!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao descartar alerta!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	private void ProcessarAlertasVinculados(Integer idUsuario, HttpServletRequest request, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		String strCadMonitorado = request.getParameter("idCadMonitorado");
		String strIdAlertaOrigem = request.getParameter("idAlertaOrigem");
		String strXmlAlertasVinculados = request.getParameter("xmlAlertasVinculados");
		
		//Listas e objetos de alertas para tratamento
		List<AlertaVinculadoAtualizar> listaDescartar = new ArrayList<AlertaVinculadoAtualizar>();
		List<AlertaVinculadoAtualizar> listaIrregularidade = new ArrayList<AlertaVinculadoAtualizar>();
		List<AlertaVinculadoAtualizar> listaVinculado = new ArrayList<AlertaVinculadoAtualizar>();
		AlertasVinculadosAtualizar alertasVinculadosDescartar = new AlertasVinculadosAtualizar();
		AlertasVinculadosAtualizar alertasVinculadosIrregularidade = new AlertasVinculadosAtualizar();
		AlertasVinculadosAtualizar alertasVinculados = new AlertasVinculadosAtualizar();
		
		try
		{
			if (strCadMonitorado == null || strCadMonitorado.equals("") || strCadMonitorado.equals("0")) 
			{
				msg = "Cadastro de monitorado não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if (strIdAlertaOrigem == null || strIdAlertaOrigem.equals("null") || strIdAlertaOrigem.equals("") || strIdAlertaOrigem.equals("0")) 
			{
				msg = "Identificador do Alerta não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if (strXmlAlertasVinculados == null || strXmlAlertasVinculados.equals("")) 
			{
				msg = "Dados dos alertas vinculados não informados!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			UUID idCadMonitorado = UUID.fromString(strCadMonitorado.trim());
			
			UUID idAlertaOrigem = UUID.fromString(strIdAlertaOrigem.trim());
			AlertasVinculadosAtualizar alertasVinculadosTratar = MontarObjetoComXML(strXmlAlertasVinculados);
			
			for (AlertaVinculadoAtualizar av : alertasVinculadosTratar.getAlertasVinculadosAtualizar())
			{
				if (av.getIdStatusAlerta().equals(StatusAlerta.DESCARTADO.GetID()))
					listaDescartar.add(av);
				else if (av.getIdStatusAlerta().equals(StatusAlerta.OCORRENCIA.GetID()))
					listaIrregularidade.add(av);
				else
					listaVinculado.add(av);
			}
			
			alertasVinculadosDescartar.setAlertasVinculadosAtualizar(listaDescartar);
			alertasVinculadosIrregularidade.setAlertasVinculadosAtualizar(listaIrregularidade);
			alertasVinculados.setAlertasVinculadosAtualizar(listaVinculado);
			
			if (!alertasVinculados.getAlertasVinculadosAtualizar().isEmpty())
				sucesso = Alertas.ProcessarAlertasVinculados(idAlertaOrigem, idUsuario, alertasVinculados);
			
			if (!alertasVinculadosDescartar.getAlertasVinculadosAtualizar().isEmpty())
				sucesso = Alertas.DescartarAlertasVinculados(idAlertaOrigem, ID_MOTIVO_DESCARTE_ALERTA_VINCULADO, MSG_DESCARTE_ALERTA_VINCULADO, idUsuario, alertasVinculadosDescartar);
			
			if (!alertasVinculadosIrregularidade.getAlertasVinculadosAtualizar().isEmpty())
			{
				Date dataOcorrencia = new Date();
				
				for (AlertaVinculadoAtualizar irregularidade : alertasVinculadosIrregularidade.getAlertasVinculadosAtualizar())
				{
					UUID idOcorrencia = UUID.randomUUID();
					sucesso = Ocorrencias.GerarOcorrencia(idOcorrencia, irregularidade.getIdAlerta(), irregularidade.getIdTipoAlerta(), irregularidade.getIdStatusAlerta(), ID_STATUS_IRREGULARIDADE_EM_ABERTO, idUsuario, dataOcorrencia, true, idAlertaOrigem);
				}
			}
			
			boolean cadastroAtivo = VeiculosMonitorados.CadastroAtivo(idCadMonitorado);
			
			if (sucesso)
				msg = "Alertas vinculados processados com sucesso!";
			else
				msg = "Falha ao processar alertas vinculados!";
			
			respostaXML.EnviarRespostaAlertaVinculadoXML(response, sucesso, msg, cadastroAtivo);
		}
		catch (IllegalArgumentException ie) {
			msg = "Ocorreu um erro ao preparar os dados para processamento!";
			logger.error(msg + ": "  + ie.getMessage(), ie);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		catch(Exception e)
		{
			msg = "Ocorreu um erro ao processar alertas vinculados!";
			logger.error(msg + ": "  + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}			
	}
	
	protected void ObterAlertaPorId(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		String strIdAlerta = request.getParameter("idAlerta");		
		String strIgnorePrivado = request.getParameter("ignorePrivado");

		if (strIdAlerta == null || strIdAlerta.isEmpty() || "0".equals(strIdAlerta)) {
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Identificador do Alerta não informado!");
			return;
		}

		UUID idAlerta;
		try {
			idAlerta = UUID.fromString(strIdAlerta.trim());
		} catch (Exception e) {
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao preparar dados para consulta!");
			return;
		}

		boolean ignorePrivado = verificarTokenIgnorePrivado(strIgnorePrivado);

		try {
			Acesso acesso = new Acesso(request, response, true); 
			Integer idUsuario = acesso.getUsuario().getId();

			if (!ignorePrivado) {
				boolean temAcesso = Alertas.VerificaAcessoPorAlertaUsuarioId(idAlerta, idUsuario);
				if (!temAcesso) {
						throw new SecurityException("Acesso negado: alerta privado e usuário não é o dono.");
				}
			}

	        // Consulta e retorno do alerta
			List<Alerta> listaAlerta = Alertas.obterAlertaPorId(idAlerta);
			Alertas alertas = new Alertas();
			alertas.setAlertas(listaAlerta);

			EnviarRespostaXML(response, alertas);

		} catch (SecurityException e) {
			respostaXML.EnviarRespostaRequisicaoXML(response, false, e.getMessage());
		} catch (Exception e) {
			logger.error("Erro ao obter dados do alerta: " + e.getMessage(), e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Ocorreu um erro ao consultar o alerta!");
		}
	}
	
	private boolean verificarTokenIgnorePrivado(String token) {
		if (token == null || token.isEmpty()) return false;

		try {
			byte[] decodedBytes = Base64.getDecoder().decode(token);
			String decoded = new String(decodedBytes, StandardCharsets.UTF_8);

	        // Esperado: "ignorarPrivado|ORIGEM|timestamp"
			String[] partes = decoded.split("\\|");
			return partes.length >= 1 && "ignorarPrivado".equals(partes[0]);

		} catch (Exception e) {
			logger.warn("Token de controle inválido ou malformado: " + e.getMessage());
			return false;
		}
	}
	
	protected void ObterCoordAlerta(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			String strIdAlerta = request.getParameter("idAlerta");
			
			if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
			{
				String msg = "Identificador do Alerta não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			UUID idAlerta = null;

			try
			{
				idAlerta = UUID.fromString(strIdAlerta.trim());
			}
			catch (Exception e)
			{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			//Cria objeto de retorno
			Veiculos veiculos = new Veiculos();
			veiculos.setListaVeiculos(new ArrayList<Veiculo>());
				
			//Faz a consulta já existente no banco de dados
			List<Veiculo> listaVeiculo = Alertas.ObterVeiculosAlertaMapaPorId(idAlerta);
			veiculos.setListaVeiculos(listaVeiculo);
			
			EnviarRespostaXML(response, veiculos);

			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter dados do alerta: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar o alerta!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	protected void ObterAlertasNaoTratados(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{	    	
			Alertas alertas = new Alertas();
			alertas.setAlertas(new ArrayList<Alerta>());
			
			List<Alerta> listaAlerta = Alertas.ObterAlertasNaoTratados();
			alertas.setAlertas(listaAlerta);
			
			EnviarRespostaXML(response, alertas);

			
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterAlertasNaoTratados(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao ObterAlertasNaoTratados()!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	public AlertasVinculadosAtualizar MontarObjetoComXML(String xml)
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance(AlertasVinculadosAtualizar.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setAdapter(new NormalizedStringAdapter());

			Object o = unmarshaller.unmarshal(new StringReader(xml));
			AlertasVinculadosAtualizar alertasVinculados = AlertasVinculadosAtualizar.class.cast(o);
            
//       AlertasVinculadosAtualizar alertasVinculados = JAXB.unmarshal(new StringReader(xml), AlertasVinculadosAtualizar.class);
            
         return alertasVinculados;
		}
		catch(Exception e)
		{
			throw new IllegalStateException("Error while deserializing a XML text to Object of type " + AlertasVinculadosAtualizar.class, e);
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, Alertas alertas) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Alertas.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(alertas, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			alertas = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de alerta!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, Veiculos veiculos) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(Veiculos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(veiculos, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(veiculos.getListaVeiculos().size()) );
			veiculos = null;
			
		}
		catch(Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de veiculos!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	public static void marcarComoEmAtendimento(String nomeUsuario, HttpServletResponse response, HttpServletRequest request) throws ConexaoException {
		String IdAlerta = request.getParameter("idAlerta");
		
		Alertas.AtualizaAtendente(IdAlerta, nomeUsuario);
	}

	public static boolean validaPermissao(HttpServletRequest request, HttpServletResponse response, String sessaoId) {
		Acesso acesso = new Acesso(request, response, true); 
		String loginAtual = acesso.getUsuario().getNome();
		try {
			return loginAtual != null;
		} catch (Exception e) {
			logger.error("Erro ao validar permissão do usuário pela sessão: " + e.getMessage(), e);
			return false;
		}
	}

	public static void podeAtender(String nomeUsuario, HttpServletResponse response, HttpServletRequest request) throws ConexaoException {
		String idAlerta = request.getParameter("idAlerta");

		String emAtendimentoPor = Alertas.PodeAtender(idAlerta, nomeUsuario);

		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"emAtendimentoPor\": \"" + (emAtendimentoPor != null ? emAtendimentoPor : "") + "\"}");
		} catch (IOException e) {
			logger.error("Erro ao escrever resposta JSON: " + e.getMessage(), e);
			throw new ConexaoException("Erro ao responder se o alerta pode ser atendido.");
		}
	}
	
	public static void obtemAlerta(HttpServletResponse response, HttpServletRequest request) throws ConexaoException {
		String idAlerta = request.getParameter("idAlerta");

		try {
	        Alerta alerta = Alertas.ObtemAlerta(idAlerta); // ou Alerta.ObtemAlerta dependendo da sua estrutura

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			if (alerta != null) {
				String json = String.format(
						"{" +
							"\"id\": \"%s\"," +
							"\"placa\": \"%s\"," +
							"\"emAtendimentoPor\": \"%s\"," +
							"\"atendido\": %s" +
						"}",
						alerta.getId(),
						alerta.getPlacaVeiculo() != null ? alerta.getPlacaVeiculo() : "",
						alerta.getEmAtendimentoPor() != null ? alerta.getEmAtendimentoPor() : "",
						alerta.getAtendido() == 1 ? "true" : "false"
				);

				response.getWriter().write(json);
			} else {
				response.getWriter().write("{\"erro\": \"Alerta não encontrado.\"}");
			}

		} catch (Exception e) {
			logger.error("Erro ao obter alerta: " + e.getMessage(), e);
			throw new ConexaoException("Erro ao responder dados do alerta.");
		}
	}

	private void criarAlerta(HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.setContentType("application/json;charset=UTF-8");

		String veiculoIdStr = request.getParameter("veiculoId");    // ID UUID do veículo monitorado
		String veiculoPlaca = request.getParameter("veiculoPlaca"); // Placa do veículo
		String tipoAlertaStr = request.getParameter("tipoAlerta");
//	   String descricao = request.getParameter("descricao");

		try {
			UUID idTipoAlerta = UUID.fromString(tipoAlertaStr);
			UUID idVeiculoMonitorado = UUID.fromString(veiculoIdStr);

			// Busca veículo tempo real pelo parâmetro placa
			UUID idVeiculoTempoReal = Alertas.getIdVeiculoTempoRealPorPlaca(veiculoPlaca);

			if (idVeiculoTempoReal == null) {
				//out.print("{\"status\":\"erro\",\"mensagem\":\"Veículo em tempo real não encontrado.\"}");
				return;
			}

	        // Cria alerta (3 argumentos, sem descrição por enquanto)
			boolean sucesso = Alertas.criarAlerta(idTipoAlerta, idVeiculoMonitorado, idVeiculoTempoReal);

			if (sucesso) {
				//out.print("{\"status\":\"sucesso\",\"mensagem\":\"Alerta criado com sucesso.\"}");
			} else {
				//out.print("{\"status\":\"erro\",\"mensagem\":\"Falha ao criar alerta.\"}");
			}

		} catch (IllegalArgumentException e) {
			//out.print("{\"status\":\"erro\",\"mensagem\":\"Parâmetro UUID inválido.\"}");
		} catch (Exception e) {
			e.printStackTrace();
			//out.print("{\"status\":\"erro\",\"mensagem\":\"Erro interno: " + e.getMessage() + "\"}");
		}
	}


	public static void obterVeiculosMonitorados(HttpServletRequest request, HttpServletResponse response) throws ConexaoException {
		response.setContentType("application/json;charset=UTF-8");
		String placa = request.getParameter("placa");

		try {
			List<JsonObject> veiculos = Veiculos.ObterVeiculo(placa);

			Gson gson = new GsonBuilder()
								.serializeNulls()
								.create();

			String json = veiculos != null ? gson.toJson(veiculos) : "[]";
			response.getWriter().write(json);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write("[]");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void atualizarAlertaModalAcao(Integer idUsuario, HttpServletRequest request, HttpServletResponse response) {
		String msg;
		boolean sucesso = true;

		try {
			String strIdAlerta = request.getParameter("idAlerta");
			String strAssinado = request.getParameter("assinado");

			if (strIdAlerta == null || strIdAlerta.isEmpty()) {
				msg = "ID do alerta não informado!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAssinado == null) {
				msg = "Valor do campo 'assinado' não informado!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			UUID idAlerta = UUID.fromString(strIdAlerta.trim());
			boolean assinado = Boolean.parseBoolean(strAssinado.trim());

			boolean resultado = Alertas.AtualizarAlertaModalAcao(idAlerta, assinado, idUsuario);

			if (resultado) {
				msg = "Campo 'assinado' atualizado com sucesso!";
			} else {
				msg = "Falha ao atualizar campo 'assinado'.";
				sucesso = false;
			}

			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);

		} catch (Exception e) {
			msg = "Erro ao atualizar campo 'assinado': " + e.getMessage();
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	protected void ObterAssinadoAlertaPorId(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		try 
		{
			String strIdAlerta = request.getParameter("idAlerta");
			
			if (strIdAlerta == null || strIdAlerta.equals("") || strIdAlerta.equals("0")) 
			{
				String msg = "Identificador do Alerta não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			UUID idAlerta = null;

			try
			{
				idAlerta = UUID.fromString(strIdAlerta.trim());
			}
			catch (Exception e)
			{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			//Cria objeto de retorno
			Alertas alertas = new Alertas();
			alertas.setAlertas(new ArrayList<Alerta>());
			
			List<Alerta> listaAlerta = Alertas.obterAssinadoAlertaPorId(idAlerta);
			alertas.setAlertas(listaAlerta);
			
			EnviarRespostaXML(response, alertas);
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter dados do alerta: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar o alerta!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	/*
		Nessa função abaixo é verificado se o atendimento pode ser iniciado ou não,
		começando pela criação da variável "podeAbrir" que inicia com o valor do "permitirAberturaAtendimento"
		então é feito os responses para o front jogando o valor recebido do banco (sendo true ou false).
	*/
	protected void ObterAberturaAtendimento(HttpServletRequest request, HttpServletResponse response, String strIdOcorrencia) throws ServletException, IOException
	{
		try 
		{
			boolean podeAbrir = Alertas.permitirAberturaAtendimento(strIdOcorrencia);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/plain;charset=UTF-8");

			response.getWriter().write(Boolean.toString(podeAbrir));
			response.getWriter().flush();
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter obterAberturaAtendimento(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao obterAberturaAtendimento()!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	public void ObterAlertasPendentesAssinatura(HttpServletRequest request, HttpServletResponse response, int idUsuario) throws ConexaoException {
		try 
		{		
			Alerta alerta = Alertas.ObterAlertasPendentesAssinatura(idUsuario);
			int naoAssinado = alerta.getNao_assinados();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/plain;charset=UTF-8");

			response.getWriter().write(Integer.toString(naoAssinado));
			response.getWriter().flush();
		}
		catch(Exception e)
		{
			logger.error("Erro ao obter ObterConfigTempos(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao obter os alertas não assinados!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}	
	}
}