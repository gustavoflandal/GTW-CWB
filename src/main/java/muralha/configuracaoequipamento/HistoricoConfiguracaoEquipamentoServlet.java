package muralha.configuracaoequipamento;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.consilux.model.Acesso;
import com.consilux.model.Usuario;

import muralha.digital.notificacao.GrupoNotificacao;
import muralha.digital.notificacao.GruposNotificacao;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/HistoricoConfiguracaoEquipamento")
public class HistoricoConfiguracaoEquipamentoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(HistoricoConfiguracaoEquipamentoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    ResultadoOperacao resultado = new ResultadoOperacao(false, "Erro interno inesperado.");

	    try {
	        if (!new Acesso(request, response, true).verificaAcesso(false)) {
	            resultado.setMensagem("Usuário não autenticado.");
	            EnviarResultadoOperacaoXML(response, resultado);
	            return;
	        }

	        Integer idUsuario = ((Usuario) request.getSession().getAttribute("[usuario]")).getId();

	        String acao = request.getParameter("acao");	      

	        if ("atualizarConfigsEquipamentos".equals(acao)) {
	        	
		        String dadosJsonString = request.getParameter("dadosJson");
		        String idTipoAlertaOcorrencia = request.getParameter("idTipoAlertaOcorrencia");
		        String[] valoresSelecionadosStr = request.getParameterValues("idsGrupos");	
		        

		        if (dadosJsonString == null || dadosJsonString.isEmpty()) {
		            resultado.setMensagem("Ops! Ocorreu um erro ao capturar as informações dos grupos!");
		            EnviarResultadoOperacaoXML(response, resultado);
		            return;
		        }	        	        

		        int[] idsGrupos = Arrays.stream(valoresSelecionadosStr)
		            .mapToInt(Integer::parseInt)
		            .toArray();
		        
		        if (idsGrupos.length < 1) {
		            resultado.setMensagem("Ops! Ocorreu um erro ao capturar os grupos!");
		            EnviarResultadoOperacaoXML(response, resultado);
		            return;
		        }
		        
		        List<GrupoNotificacao> gruposParaInserir = MontarListaNovasConfigs(idsGrupos, UUID.fromString(idTipoAlertaOcorrencia), idUsuario);
	        	
	            boolean result = HistoricoConfiguracaoEquipamentos.CadastrarHistorico(idUsuario, dadosJsonString);

	            if (!result) {
	                resultado.setMensagem("Erro ao cadastrar histórico.");
	                EnviarResultadoOperacaoXML(response, resultado);
	                return;
	            }
	            
	            List<UUID> ids = new ArrayList<>();
	            JSONObject jsonObj = new JSONObject(dadosJsonString);
	            JSONArray idsGruposRemover = jsonObj.getJSONObject("ListaGruposNotificacao").getJSONArray("GrupoNotificacao");

	            for (int i = 0; i < idsGruposRemover.length(); i++) {
	                JSONObject grupo = idsGruposRemover.getJSONObject(i);
	                ids.add(UUID.fromString(grupo.getString("id")));
	            }

	            // Convertendo List<UUID> para UUID[]
	            UUID[] arrayIdsRemover = ids.toArray(new UUID[0]);

	            result = GruposNotificacao.DeletarConfigsPorIds(arrayIdsRemover);
	            
	            if (!result) {
	                resultado.setMensagem("Erro ao remover as configurações antigas!");
	                EnviarResultadoOperacaoXML(response, resultado);
	                return;
	            }
	            
	            result = GruposNotificacao.InserirConfigsPorLista(gruposParaInserir);
	            
	            if (!result) {
	                resultado.setMensagem("Erro ao inserir as novas configurações!");
	                EnviarResultadoOperacaoXML(response, resultado);
	                return;
	            }
	        }

	        resultado.setSucesso(true);
	        resultado.setMensagem("Operação realizada com sucesso.");
	        EnviarResultadoOperacaoXML(response, resultado);

	    } catch (Exception ex) {
	        logger.error("Erro no processamento do POST", ex);
	        resultado.setMensagem("Erro ao processar a requisição: " + ex.getMessage());
	        EnviarResultadoOperacaoXML(response, resultado);
	    }
	}

	@SuppressWarnings("unused")
	private void EnviarRespostaXML(HttpServletResponse response, ConfiguracaoEquipamento configEquip)
			throws JAXBException, IOException
	{
		logger.info("Iniciando resposta XML");
		
		JAXBContext context;
		try
		{
			context = JAXBContext.newInstance(ConfiguracaoEquipamento.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(configEquip, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			//logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(configEquip.obterConfiguracoes().size()) );
			configEquip = null;
			logger.info("Resposta XML - Sucesso");
			
		}
		catch (Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de pontos de interesse!";
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarResultadoOperacaoXML(HttpServletResponse response, ResultadoOperacao resultado)
	        throws IOException {
	    try {
	        JAXBContext context = JAXBContext.newInstance(ResultadoOperacao.class);
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	        StringWriter sw = new StringWriter();
	        marshaller.marshal(resultado, sw);
	        String xml = sw.toString();
	        sw.close();

	        response.setContentType("text/xml");
	        response.setCharacterEncoding("UTF-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().write(xml);
	        response.getWriter().flush();

	    } catch (JAXBException e) {
	        logger.error("Erro ao gerar XML de resposta: " + e.getMessage(), e);
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao gerar XML.");
	    }
	}
	
	private static List<GrupoNotificacao> MontarListaNovasConfigs(int[] idsGrupos, UUID idTipoAlertaOcorrencia, int idUsuario) {
		
		List<GrupoNotificacao> grupos = new ArrayList<>();
		
		// Tipos de registro
	    Map<String, UUID> tiposRegistro = new HashMap<>();
	    tiposRegistro.put("OCORRÊNCIA", UUID.fromString("5511CEF5-C1A0-450B-99B3-6FCA8668D243"));
	    tiposRegistro.put("ALERTA",     UUID.fromString("E7D115B9-E6B3-4E86-9083-F347A1917045"));

	    // Tipos de notificação
	    Map<String, UUID> tiposNotificacao = new HashMap<>();
	    tiposNotificacao.put("SMS",   UUID.fromString("2C434CFD-F581-4FA0-B3E3-45A7367BF05E"));
	    tiposNotificacao.put("EMAIL", UUID.fromString("3A3F1F17-6EC3-4FCA-9195-5B160A091779"));
	    tiposNotificacao.put("POPUP", UUID.fromString("778F443E-9514-44E0-BCD1-F953D91042BF"));
	    
	    for (Map.Entry<String, UUID> tipoNotificacao : tiposNotificacao.entrySet()) {
	        System.out.println("Notificação: " + tipoNotificacao.getKey() + " - " + tipoNotificacao.getValue());
	    }
				
		
	    for (int idGrupo : idsGrupos) {
	        for (Map.Entry<String, UUID> tipoRegistro : tiposRegistro.entrySet()) {
	            for (Map.Entry<String, UUID> tipoNotificacao : tiposNotificacao.entrySet()) {

	                GrupoNotificacao grupo = new GrupoNotificacao();
	                grupo.setIdGrupo(idGrupo);
	                grupo.setIdUsuario(idUsuario);
	                grupo.setIdTipoRegistro(tipoRegistro.getValue());
	                grupo.setTipoRegistro(tipoRegistro.getKey());
	                grupo.setIdTipoAlertaOcorrencia(idTipoAlertaOcorrencia);
	                grupo.setIdTipoNotificacao(tipoNotificacao.getValue());
	                grupo.setAtivo(1);

	                grupos.add(grupo);
	                }
	            }
	        }	    
	    return grupos;
	    }
}
