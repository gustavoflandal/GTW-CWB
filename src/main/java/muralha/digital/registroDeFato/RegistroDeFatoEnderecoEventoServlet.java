package muralha.digital.registroDeFato;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.*;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFato/EnderecoEvento")
public class RegistroDeFatoEnderecoEventoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RegistroDeFatoEnderecoEventoServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }

        try {
        	RegistroDeFatoEnderecoEventos resposta = new RegistroDeFatoEnderecoEventos();
            resposta.setListaEnderecoEvento(RegistroDeFatoEnderecoEventos.ObterListaEnderecosEvento());

            EnviarRespostaXML(response, resposta);

        } catch (Exception e) {
            logger.error("Erro ao obter registro_fato_endereco_evento: " + e.getMessage(), e);
            EnviarMensagemXML(response, false, "Erro ao consultar registro_fato_endereco_evento.");
        }
    }

    private void EnviarRespostaXML(HttpServletResponse response, RegistroDeFatoEnderecoEventos dados) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(RegistroDeFatoEnderecoEventos.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(dados, sw);
        String xml = sw.toString();
        sw.close();

        response.setHeader("Content-Type", "text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(xml);
        response.getWriter().flush();
    }

    private void EnviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
        try {
            RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
            resposta.setSucesso(sucesso);
            resposta.setMsgResposta(msg);

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
            logger.error("Erro ao gerar XML de erro: " + e.getMessage(), e);
        }
    }
}
