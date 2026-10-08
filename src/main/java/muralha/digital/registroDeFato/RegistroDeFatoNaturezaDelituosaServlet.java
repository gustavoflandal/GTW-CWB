package muralha.digital.registroDeFato;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFatoNaturezaDelituosa")
public class RegistroDeFatoNaturezaDelituosaServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RegistroDeFatoNaturezaDelituosaServlet.class);
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }
        
        try {
            String acao = request.getParameter("acao");
            
            if (acao == null || acao.isEmpty()) {
                acao = "obterLista";
            }
            
            if (acao.equals("obterLista")) {
                obterListaNaturezasDelituosas(request, response);
            } else {
                String msg = "Ação não reconhecida: " + acao;
                logger.error(msg);
                enviarMensagemXML(response, false, msg);
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao processar a requisição de naturezas delituosas!";
            logger.error(msg, e);
            enviarMensagemXML(response, false, msg);
        }
    }
    
    private void obterListaNaturezasDelituosas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<RegistroDeFatoNaturezaDelituosa> listaNaturezas = RegistroDeFatoNaturezaDelituosas.obterListaNaturezasDelituosas();
            RegistroDeFatoNaturezaDelituosas naturezasDelituosas = new RegistroDeFatoNaturezaDelituosas();
            naturezasDelituosas.setListaNaturezasDelituosas(listaNaturezas);
            
            enviarRespostaXML(response, naturezasDelituosas);
            
        } catch (ConexaoException | SQLException e) {
            String msg = "Erro ao obter lista de naturezas delituosas!";
            logger.error(msg, e);
            enviarMensagemXML(response, false, msg);
        }
    }
    
    private void enviarRespostaXML(HttpServletResponse response, RegistroDeFatoNaturezaDelituosas naturezasDelituosas) {
        try {
            JAXBContext context = JAXBContext.newInstance(RegistroDeFatoNaturezaDelituosas.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marshaller.marshal(naturezasDelituosas, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
            logger.info("Lista de naturezas delituosas enviada com sucesso. Total: " + 
                    naturezasDelituosas.getListaNaturezasDelituosas().size());
            
        } catch (JAXBException e) {
            logger.error("Erro ao serializar XML: " + e.getMessage(), e);
            enviarMensagemXML(response, false, "Erro ao preparar resposta da requisição!");
        } catch (IOException e) {
            logger.error("Erro de I/O ao enviar resposta: " + e.getMessage(), e);
            enviarMensagemXML(response, false, "Erro ao enviar resposta!");
        } catch (Exception e) {
            logger.error("Erro inesperado ao enviar resposta XML: " + e.getMessage(), e);
            enviarMensagemXML(response, false, "Erro inesperado ao preparar resposta!");
        }
    }
    
    private void enviarMensagemXML(HttpServletResponse response, boolean sucesso, String mensagem) {
        RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
        resposta.setSucesso(sucesso);
        resposta.setMsgResposta(mensagem);
        
        try {
            JAXBContext context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marshaller.marshal(resposta, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
        } catch (Exception e) {
            logger.error("Erro gravíssimo ao preparar resposta: " + e.getMessage(), e);
        }
    }
}