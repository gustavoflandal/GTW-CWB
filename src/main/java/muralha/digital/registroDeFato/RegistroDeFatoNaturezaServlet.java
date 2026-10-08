package muralha.digital.registroDeFato;

import java.io.IOException;
import java.io.StringWriter;
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

import muralha.digital.util.RespostaRequisicaoXML;


@WebServlet("/MuralhaDigital/RegistroDeFato/Natureza")
public class RegistroDeFatoNaturezaServlet extends HttpServlet
{
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RegistroDeFatoNaturezaServlet.class);
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
        
        try 
        {
            String idTipoParam = request.getParameter("idTipo");
            
            RegistroDeFatoNaturezas naturezasRegistroDeFato = new RegistroDeFatoNaturezas();
            naturezasRegistroDeFato.setListaNaturezas(new ArrayList<RegistroDeFatoNatureza>());
                
            List<RegistroDeFatoNatureza> listaNaturezas;
            
            if (idTipoParam != null && !idTipoParam.isEmpty()) {
                Integer idTipo = Integer.parseInt(idTipoParam);
                listaNaturezas = RegistroDeFatoNaturezas.ObterNaturezasPorTipo(idTipo);
            } else {
                listaNaturezas = RegistroDeFatoNaturezas.ObterListaNaturezas();
            }
            
            naturezasRegistroDeFato.setListaNaturezas(listaNaturezas);

            EnviarRespostaXML(response, naturezasRegistroDeFato);
            
        }
        catch(NumberFormatException e) {
            logger.error("Erro ao converter parâmetro idTipo: " + e.getMessage(), e);
            String msg = "Parâmetro idTipo inválido!";
            logger.error(msg);    
            EnviarMensagemXML(response, false, msg);
            return;
        }
        catch(Exception e) {
            logger.error("Erro ao obter naturezas de registro de fato: " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao consultar naturezas de registro de fato!";
            logger.error(msg);    
            EnviarMensagemXML(response, false, msg);
            return;
        }
    }
    
    private void EnviarRespostaXML(HttpServletResponse response, RegistroDeFatoNaturezas registroDeFatoNaturezas) throws JAXBException, IOException
    {
        JAXBContext context;
        try
        {
            context = JAXBContext.newInstance(RegistroDeFatoNaturezas.class);
            Marshaller marsHall = context.createMarshaller();
            marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marsHall.marshal(registroDeFatoNaturezas, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
            logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(registroDeFatoNaturezas.getListaNaturezas().size()) );
            registroDeFatoNaturezas = null;
            
        }
        catch(Exception e) {
            logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao retornar o resultado da consulta de naturezas de registro de fato!";
            logger.error(msg);
            EnviarMensagemXML(response, false, msg);
            return;
        }    
    }
    
    private void EnviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) 
    {
        RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
        resposta.setSucesso(sucesso);
        resposta.setMsgResposta(msg);
        
        JAXBContext evidencia_context;
        try
        {
            evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
            Marshaller marsHall = evidencia_context.createMarshaller();
            marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marsHall.marshal(resposta, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
        
        }
        catch (Exception e)
        {
            String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
            logger.error(msgErro, e);
            return;
        }
    }
}