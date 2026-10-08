package muralha.digital.registroDeFato;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.*;

import org.apache.log4j.Logger;

import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFato/Grupo")
public class GrupoRegistroDeFatoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(GrupoRegistroDeFatoServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Acesso acesso = new Acesso(request, response, true);
        if (!acesso.verificaAcesso()) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }

        try {
            GrupoRegistroDeFatos grupoWrapper = new GrupoRegistroDeFatos();

            String acao = request.getParameter("acao");
            List<GrupoRegistroDeFato> grupos;

            if ("obterTodosPorUsuarioId".equalsIgnoreCase(acao)) {
                int usuarioId = acesso.getUsuario().getId();
                grupos = GrupoRegistroDeFatos.obterGruposPorUsuarioId(usuarioId);
                grupoWrapper.setGrupos(grupos);
                enviarRespostaXML(response, grupoWrapper);
            } else if ("obterTodos".equalsIgnoreCase(acao)) {
                GruposUsuariosResponse resposta = GrupoRegistroDeFatos.obterTodos();
                enviarRespostaXML(response, resposta);
            } else {
                enviarMensagemXML(response, false, "Parâmetro 'acao' inválido ou não informado.");
                return;
            }

        } catch (Exception e) {
            logger.error("Erro ao obter grupos: " + e.getMessage(), e);
            enviarMensagemXML(response, false, "Erro ao consultar grupos de registro de fato.");
        }
    }

    private void enviarRespostaXML(HttpServletResponse response, Object resposta)
            throws JAXBException, IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(resposta.getClass());
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

            logger.info("enviarRespostaXML():: XML enviado com sucesso (" 
                        + resposta.getClass().getSimpleName() + ")");

        } catch (Exception e) {
            logger.error("Erro ao enviarRespostaXML(): " + e.getMessage(), e);
            enviarMensagemXML(response, false, "Erro ao retornar resultado da consulta.");
        }
    }

    private void enviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
        RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
        resposta.setSucesso(sucesso);
        resposta.setMsgResposta(msg);

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
