package muralha.digital.notificacao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.PrintWriter;
import javax.mail.internet.InternetAddress;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;


import org.apache.log4j.Logger;
import muralha.digital.acessos.Usuario;

@WebServlet("/MuralhaDigital/Notificacao")
public class NotificacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(NotificacaoServlet.class);
    private ServicoSMS servicosms = new ServicoSMS();
    private RecuperacaoSenha recuperacaoSenha = new RecuperacaoSenha();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String msg = null;
            String strAcao = request.getParameter("acao");

            if (strAcao.equals("listar"))
                System.setOut(null);
            // listarGuarnicoes(response);

            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (Exception e) {
            logger.error("Erro no doGet da GuarnicaoServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");

        try {
            if ("enviarsms".equalsIgnoreCase(acao)) {
                enviarSMS(request, response);
            }

            else if ("enviarEmail".equalsIgnoreCase(acao)) {
                enviarEmail(request, response);
            }

            else if ("buscarusuario".equalsIgnoreCase(acao)) {
                buscarUsuario(request, response);
            }
            
            else if ("validartoken".equalsIgnoreCase(acao)) {
                validarToken(request, response);
            }
            
            else if ("atualizarsenha".equalsIgnoreCase(acao)) {
            	atualizarsenha(request, response);
            }

            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST inválida ou não informada.");
            }
        } catch (Exception e) {
            logger.error("Erro no doPost da GuarnicaoServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    private void enviarSMS(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<String> telefone = new ArrayList<>();
        telefone.add(request.getParameter("telefone"));
        String idUsuario = request.getParameter("idUsuario");
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() 
        + request.getContextPath();
        boolean sucesso = false;

        try {
        	String token = recuperacaoSenha.criarTokenRecuperacaoSenha(idUsuario);
            sucesso = servicosms.enviaSmsFacilitaMovel(token, telefone, baseUrl);
        } catch (Exception e) {
            e.printStackTrace();
            sucesso = false;
        }

        response.getWriter().write("{\"success\": " + sucesso + "}");
    }

    private void enviarEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();

        ConfiguracaoServidorSmtp servidor = servidores.get(0);
        
        ServicoEmailMuralha servicoEmail = new ServicoEmailMuralha(
                servidor.getUser(),
                servidor.getPassword(),
                servidor.getHost(),
                servidor.getPort()
            );

        String destinatario = request.getParameter("destinatario");
        String idUsuario = request.getParameter("idUsuario");
        boolean sucesso;
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() 
        + request.getContextPath();

        try {
            String token = recuperacaoSenha.criarTokenRecuperacaoSenha(idUsuario);
            sucesso = servicoEmail.enviarEmailRecuperacaodeSenha(
            	    new InternetAddress("admin@consilux.com"),
            	    Arrays.asList(new InternetAddress(destinatario)),
            	    token,
            	    baseUrl
            	);
        } catch (Exception e) {
            e.printStackTrace();
            sucesso = false;
        }

        response.getWriter().write("{\"success\": " + sucesso + "}");
    }

    private void buscarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apelido = request.getParameter("apelido");

        Usuario usuario = RecuperacaoSenha.buscarUsuarioPorApelido(apelido);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (usuario != null) {
            String json = new com.google.gson.Gson().toJson(usuario);
            response.getWriter().write(json);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"erro\": \"Usuário não encontrado\"}");
        }
    }
    
    private void validarToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        int idUsuario = RecuperacaoSenha.validarToken(token);

        response.setContentType("application/json");
        if (idUsuario > 0) {
            response.getWriter().write("{\"valido\": true, \"idUsuario\": " + idUsuario + "}");
        } else {
            response.getWriter().write("{\"valido\": false}");
        }
    }

    private void atualizarsenha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Integer idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        String senha = request.getParameter("novaSenha");
        boolean sucesso;

        try {
            sucesso = ServicoEmailMuralha.atualizarSenhaUsuario(idUsuario, senha);
        } catch (Exception e) {
            e.printStackTrace();
            sucesso = false;
        }

        String json = String.format(
            "{\"sucesso\": %b, \"idUsuario\": %d}",
            sucesso,
            idUsuario
        );

        out.write(json);
    }

}