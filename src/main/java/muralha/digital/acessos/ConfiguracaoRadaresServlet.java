package muralha.digital.acessos;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

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
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/ConfiguracaoRadares")
public class ConfiguracaoRadaresServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ConfiguracaoRadaresServlet.class);
    private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();

    public ConfiguracaoRadaresServlet() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String msg = null;
            String strAcao = request.getParameter("acao");
            
            if (strAcao == null || strAcao == "") {
                msg = "Ação não informada!";
                logger.error(msg);    
                respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
                return;
            }
            
            if(strAcao.equals("obterRaioRadaresMapa")) {
                obterRaioRadaresMapaXML(response);
            }
        } catch(Exception e) {
            logger.error("Erro no processo doGet() de ConfiguracaoRadares: " + e.getMessage(), e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Acesso acesso = new Acesso(request, response, true);
        if (!acesso.verificaAcesso()) {
            new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
            return;
        }
        
        try {
            String acao = request.getParameter("acao");
            
            if(acao.equals("configurarRaioRadaresMapa")) {
                Integer raio = Integer.parseInt(request.getParameter("raio"));
                Integer idUsuario = acesso.getUsuario().getId();
                
                configurarRaioRadaresMapa(raio, idUsuario, response);
                return;
            }
        } catch (ConexaoException e) {
            logger.error("Erro ao processar requisicao. " + e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Erro SQL ao processar requisicao. " + e.getMessage(), e);
        }
    }
    
    public static Integer obterRaioRadaresMapa() throws ConexaoException, SQLException {
        
        StringBuilder sbSQL = new StringBuilder();
        Integer raio = 1000;

        sbSQL.append("select * from muralha.config_chave_valor where chave = 'raio_radares_mapa'");
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {        
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                raio = rs.getInt("valor");
            }
        }                
        finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
        }
        
        if (raio == null) {
            raio = 1000;
        }
        
        return raio;
    }
    
    public static void configurarRaioRadaresMapa(Integer raio, Integer idUsuario, HttpServletResponse response) throws ConexaoException, SQLException, IOException {

        StringBuilder sbSQL = new StringBuilder();

        Integer raioAnterior = obterRaioRadaresMapa();       
        
        sbSQL.append("update muralha.config_chave_valor set valor = ? where chave = 'raio_radares_mapa'");    

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            ps.setInt(1, raio);
           
            ps.executeUpdate();
            
            String eventoHistorico = "raio_radares_mapa = " + raioAnterior + " -> " + raio;
            
            gerarHistorico("raio_radares_mapa", eventoHistorico, idUsuario);
            
            respostaXML.EnviarRespostaRequisicaoXML(response, true, "Configuração salva com sucesso");
        }
        finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }
    
    public static void gerarHistorico(String chave, String msg, Integer idUsuario) throws SQLException, ConexaoException {
        
        StringBuilder sbSQL = new StringBuilder();
        Timestamp agora = Timestamp.from(Instant.now());
                
        sbSQL.append("insert into muralha.config_chave_valor_hist (id_usuario, data_atualizacao, evento) values (?,?,?)");
        
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            ps.setInt(1, idUsuario);
            ps.setTimestamp(2, agora);
            ps.setString(3, msg);

            ps.executeUpdate();
        }
        finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }
    
    public void obterRaioRadaresMapaXML(HttpServletResponse response) throws ConexaoException, SQLException, JAXBException, IOException {
        
        StringBuilder sbSQL = new StringBuilder();
        ConfiguracaoRadaresResult result = new ConfiguracaoRadaresResult();
        ConfiguracaoRadares configs = new ConfiguracaoRadares();

        sbSQL.append("select * from muralha.config_chave_valor where chave = 'raio_radares_mapa'");
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {        
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                Integer valor = rs.getInt("valor");
                configs.setRaioRadaresMapa(valor);
            }
            
            result.setConfiguracao(configs);
            result.setSucesso(true);
            result.setMsgResposta("");
            
        }                
        finally {
            if (rs != null)
                rs.close();
            if (ps != null)
                ps.close();
            if (conn != null)
                conn.close();
        }
        
        EnviarRespostaXML(response, result);
    }
    
    private void EnviarRespostaXML(HttpServletResponse response, ConfiguracaoRadaresResult result) throws JAXBException, IOException
    {
        JAXBContext context;
        try
        {
            context = JAXBContext.newInstance(ConfiguracaoRadaresResult.class);
            Marshaller marsHall = context.createMarshaller();
            marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter sw = new StringWriter();
            marsHall.marshal(result, sw);
            String xml = sw.toString();
            sw.close();
            
            response.setHeader("Content-Type", "text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(xml);
            response.getWriter().flush();
            
            result = null;
            
        }
        catch(Exception e) {
            logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
            String msg = "Ocorreu um erro ao retornar o resultado da consulta do raio de radares!";
            logger.error(msg);
            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
            return;
        }    
    }
}