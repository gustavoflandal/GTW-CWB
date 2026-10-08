package muralha.digital.areamonitorada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/AreaMonitorada")
public class AreaMonitoradaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AreaMonitoradaServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");

		// Validando acesso do usuário
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErroMuralha("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao == "") {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if(strAcao.equals("buscarAreas")) {
				BuscarAreas(request, response);
			}else if(strAcao.equals("buscarPorNome")) {
				BuscarAreaPorNome(request, response);
			}
			else if(strAcao.equals("obterEquipamentos")) {
		    	ObterEquipamentos(request, response);
			};

		} catch (Exception e) {
			String msg = "Ocorreu um erro relacionado à área monitorada!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");

		// Validando acesso do usuário
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErroMuralha("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao == "") {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			else if (strAcao.equals("cadastrar"))
				CadastrarAreaMonitorada(request, response);
			
			else if (strAcao.equals("excluir"))
				ExcluirAreaMonitorada(request, response);
			
		} catch (Exception e) {
			String msg = "Ocorreu um erro relacionado à área monitorada!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private void CadastrarAreaMonitorada(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String jsonData = sb.toString();

			String nome = request.getParameter("nome");

			AreasMonitoradas repository = new AreasMonitoradas();
			int idResult = AreasMonitoradas.CadastrarAreaMonitorada(nome, jsonData);

			 if(idResult > 0) {
				 ObjectMapper mapper = new ObjectMapper(); JsonNode rootNode =
				 mapper.readTree(jsonData); JsonNode equipamentosNode =
				 rootNode.get("equipamentos");
				 
				 if (equipamentosNode != null && equipamentosNode.isArray()) {
		                int[] idsEquipamentos = new int[equipamentosNode.size()];
		                for (int i = 0; i < equipamentosNode.size(); i++) {
		                    idsEquipamentos[i] = equipamentosNode.get(i).asInt();
		                    }	
		                
		                AreasMonitoradas.CadastrarEquipamentosAreaMonitorada(idResult, idsEquipamentos);
				 }
			 }			 
			 EnviarRespostaXML(response, repository);
		} catch (Exception e) {
			logger.error("Erro ao cadastrar área monitorada: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao cadastrar a área monitorada";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	@SuppressWarnings("unused")
	private void CadastrarEquipamentosAreaMonitorada(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String jsonData = sb.toString();

			AreasMonitoradas repository = new AreasMonitoradas();
			AreasMonitoradas.CadastrarEquipamentosAreaMonitorada(0, null);
			
		} catch (Exception e) {
			logger.error("Erro ao cadastrar área monitorada: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao cadastrar a área monitorada";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
    protected void BuscarAreas(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{			
			//int offset = Integer.parseInt(request.getParameter("offset"));
			String nome = request.getParameter("nome");
									
			List<AreaMonitorada> listaAreas = AreasMonitoradas.BuscarAreasCadastradas(nome, 0);
			
			AreasMonitoradas areas = new AreasMonitoradas();
			areas.setAreasMonitoradas(listaAreas);
			
			EnviarRespostaXML(response, areas);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao BuscarAreas: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao buscar as áreas!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	}

    protected void BuscarAreaPorNome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{			
			String nome = request.getParameter("nome");
									
			AreaMonitorada area = AreasMonitoradas.BuscarAreaPorNome(nome);
									
			EnviarRespostaXML(response, area);			
		}
		catch(Exception e)
		{
			logger.error("Erro ao BuscarAreaPorNome: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao buscar área por nome!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}		
	}
    
    protected void ExcluirAreaMonitorada(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {			
            int id = Integer.parseInt(request.getParameter("id"));							
            int result = AreasMonitoradas.ExcluirAreaMonitorada(id);
            
            System.out.println("Result: "+result);
            
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            if (result > 0) {    			
                out.write("{\"sucesso\":true,\"mensagem\":\"Área excluída com sucesso.\"}");
            } else {
                out.write("{\"sucesso\":false,\"mensagem\":\"Não foi possível excluir a área monitorada.\"}");
            }
            out.flush();

        } catch(Exception e) {
            logger.error("Erro ao ExcluirAreaMonitorada: " + e.getMessage(), e);
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"sucesso\":false,\"mensagem\":\"Ocorreu um erro ao excluir a área monitorada!\"}");
        }
    }
    
	private void ObterEquipamentos(HttpServletRequest request, HttpServletResponse response)
	{
		try 
		{						
			List<EquipamentoDTO> result = new ArrayList<EquipamentoDTO>();
			result = AreasMonitoradas.ObterEquipamentos();
			EquipamentoDTOListWrapper wrapper = new EquipamentoDTOListWrapper(result);
			EnviarRespostaXML(response, wrapper);
			
		}
		catch(Exception e) {
			logger.error("Erro ao obter equipamentos: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao consultar os equipamentos para a área monitorada";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

    private void EnviarRespostaXML(HttpServletResponse response, Object resposta) throws JAXBException, IOException {
        if (resposta == null) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        JAXBContext context = JAXBContext.newInstance(resposta.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        response.setContentType("application/xml; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            marshaller.marshal(resposta, writer);
            writer.flush();
        }

        logger.info("EnviarRespostaXML():: Resposta enviada");
    }
}
