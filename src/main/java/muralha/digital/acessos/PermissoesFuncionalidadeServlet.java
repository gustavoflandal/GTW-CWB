package muralha.digital.acessos;


import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.Usuario;

@WebServlet("/MuralhaDigital/PermissoesFuncionalidade")
public class PermissoesFuncionalidadeServlet 
								extends javax.servlet.http.HttpServlet 
								implements javax.servlet.Servlet 
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PermissoesFuncionalidadeServlet.class); 		

	public PermissoesFuncionalidadeServlet()
	{}
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{
			String sAcao = request.getParameter("acao");		
			
			if( sAcao.equals("ListaFuncionalidadesByUsuarioId") )
			{
				int idUsuario = ((Usuario) request.getSession().getAttribute("[usuario]")).getId();
				
				PermissoesFuncionalidade perms = ObterPermissoesFuncionalidadePorUsuario(idUsuario);				
				EnviarRespostaRequisicaoXML(response, perms);
			}else if(sAcao.equals("listarPermissaoGrupoByUsuarioId")) {
				int idUsuario = ((Usuario) request.getSession().getAttribute("[usuario]")).getId();
				List<Grupo> listaGrupos = ObterPermissaoGrupoPorUsuarioId(idUsuario);

				// Envolvendo em wrapper
				GruposRetorno gruposWrapper = new GruposRetorno(listaGrupos);

				// Envia a resposta como XML
				EnviarRespostaRequisicaoXML(response, gruposWrapper);
			}else if (sAcao.equals("verificarPermissaoGrupo")) {
				int idUsuario = ((Usuario) request.getSession().getAttribute("[usuario]")).getId();

				boolean temPermissao = UsuarioPossuiPermissaoGrupo(idUsuario);

				RespostaPermissao resposta = new RespostaPermissao(temPermissao);
				EnviarRespostaRequisicaoXML(response, resposta);
			}else if (sAcao.equals("verificarPermissaoGrupoEspecifico")) {
				int idUsuario = ((Usuario) request.getSession().getAttribute("[usuario]")).getId();
				int idGrupo = Integer.parseInt(request.getParameter("idGrupo"));
				
				boolean temPermissao = UsuarioPossuiPermissaoGrupoEspecifico(idUsuario, idGrupo);

				RespostaPermissao resposta = new RespostaPermissao(temPermissao);
				EnviarRespostaRequisicaoXML(response, resposta);
			}
			
			
			else
				logger.warn("doGet() sem String de AÇÃO esperada!!");
			
		}
		catch(Exception err) {
			logger.error("Erro ao executar PermissoesFuncionalidadeServlet()::Get():: ", err);
			return;
		}		
	}  
	
	public static List<Grupo> ObterPermissaoGrupoPorUsuarioId(int idUsuario) {
	    List<Grupo> grupos = new ArrayList<>();

	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        conn = Conexao.getConexao();

	        String sql = "";
	        sql += "SELECT g.id_grupo, g.descricao, g.pagina_inicial, g.nivel_ligacao ";
	        sql += "FROM sis_grupo g (NOLOCK) ";
	        sql += "INNER JOIN sis_usuario_grupo ug (NOLOCK) ON g.id_grupo = ug.id_grupo ";
	        sql += "WHERE ug.id_usuario = ?";

	        stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, idUsuario);
	        rs = stmt.executeQuery();

	        while (rs.next()) {
	            Grupo grupo = new Grupo();
	            grupo.setIdGrupo(rs.getInt("id_grupo"));
	            grupo.setDescricao(rs.getString("descricao"));
	            grupo.setPaginaInicial(rs.getString("pagina_inicial"));
	            grupo.setNivel(rs.getInt("nivel_ligacao"));

	            grupos.add(grupo);
	            logger.debug("Grupo encontrado: " + grupo.getDescricao());
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao obter grupos do usuário " + idUsuario + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }

	    return grupos;
	}
	
	public static boolean UsuarioPossuiPermissaoGrupo(int idUsuario) {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    boolean possuiPermissao = false;

	    try {
	        conn = Conexao.getConexao();

	        String sql = "";
	        sql += "SELECT 1 ";
	        sql += "FROM sis_usuario su ";
	        sql += "JOIN sis_usuario_grupo sug ON sug.id_usuario = su.id_usuario ";
	        sql += "JOIN muralha.v_grupo_supervisionado vgs ON vgs.id_grupo = sug.id_grupo ";
	        sql += "WHERE su.id_usuario = ?";

	        stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, idUsuario);
	        rs = stmt.executeQuery();

	        if (rs.next()) {
	            possuiPermissao = true;
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao verificar grupo supervisionado para o usuário " + idUsuario + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }

	    return possuiPermissao;
	}
	
	public static boolean UsuarioPossuiPermissaoGrupoEspecifico(int idUsuario, int idGrupo) {
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    boolean possuiPermissao = false;

	    try {
	        conn = Conexao.getConexao();

	        String sql = "";
	        sql += "SELECT 1 ";
	        sql += "FROM sis_usuario su ";
	        sql += "JOIN sis_usuario_grupo sug ON sug.id_usuario = su.id_usuario ";
	        sql += "JOIN muralha.v_grupo_supervisionado vgs ON vgs.id_grupo = sug.id_grupo ";
	        sql += "WHERE su.id_usuario = ? and vgs.id_grupo = ?";

	        stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, idUsuario);
	        stmt.setInt(2, idGrupo);
	        rs = stmt.executeQuery();

	        if (rs.next()) {
	            possuiPermissao = true;
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao verificar grupo supervisionado específico para o usuário " + idUsuario + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (stmt != null) stmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }

	    return possuiPermissao;
	}
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, RespostaPermissao resposta) {
		try {
			JAXBContext context = JAXBContext.newInstance(RespostaPermissao.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error("Erro ao enviar resposta XML (RespostaPermissao): " + e.getMessage(), e);
		}
	}
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, GruposRetorno gruposWrapper) {
		try {
			JAXBContext context = JAXBContext.newInstance(GruposRetorno.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(gruposWrapper, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

		} catch (Exception e) {
			logger.error("Erro ao enviar resposta XML com lista de grupos: " + e.getMessage(), e);
		}
	}
	
	public static PermissoesFuncionalidade ObterPermissoesFuncionalidadePorUsuario(int idUsuario)
	{
		
		Connection 			conn = null;
		PreparedStatement	stmt = null;
		ResultSet 			rs = null;
		
		PermissoesFuncionalidade perms = new PermissoesFuncionalidade();
		perms.setListaPermissoesFuncionalidade(new ArrayList<PermissaoFuncionalidade>());
		
		try 
		{
			conn = Conexao.getConexao();		
			
			String strSQL =   "";
			strSQL = strSQL + " SELECT 																";
			strSQL = strSQL + " 	dbo.InitCap(tp.descricao) + 's -- ' + tao.tipo AS descricao,	";
			strSQL = strSQL + " 	cgp.id_tipo_alerta_ocorrencia                               	";
			strSQL = strSQL + " FROM muralha.config_grupo_permissao cgp                         	";
			strSQL = strSQL + " INNER JOIN muralha.tipo_registro tp                             	";
			strSQL = strSQL + " 	ON tp.id = cgp.id_tipo_registro                             	";
			strSQL = strSQL + " INNER JOIN muralha.tipo_alerta_ocorrencia tao                   	";
			strSQL = strSQL + " 	ON tao.id = cgp.id_tipo_alerta_ocorrencia                   	";
			strSQL = strSQL + " INNER JOIN sis_grupo sp                                         	";
			strSQL = strSQL + " 	ON sp.id_grupo = cgp.id_grupo                               	";
			strSQL = strSQL + " INNER JOIN sis_usuario_grupo sug                                	";
			strSQL = strSQL + " 	ON sug.id_grupo = sp.id_grupo                               	";
			strSQL = strSQL + " INNER JOIN sis_usuario su                                       	";
			strSQL = strSQL + " 	ON su.id_usuario =sug.id_usuario                            	";
			strSQL = strSQL + " WHERE su.id_usuario = ?                                         	";
			strSQL = strSQL + " GROUP BY                                                        	";
			strSQL = strSQL + " 	cgp.id_tipo_registro,                                       	";
			strSQL = strSQL + " 	tp.descricao,                                               	";
			strSQL = strSQL + " 	cgp.id_tipo_alerta_ocorrencia,                              	";
			strSQL = strSQL + " 	tao.tipo                                                    	";
			strSQL = strSQL + " ORDER BY                                                        	";
			strSQL = strSQL + " 	tp.descricao,                                               	";
			strSQL = strSQL + " 	tao.tipo                                                    	";

			
			stmt = conn.prepareStatement(strSQL);
			
			stmt.setInt(1, idUsuario);
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				PermissaoFuncionalidade perm = new PermissaoFuncionalidade();
				
				perm.setIdPermFunc(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				perm.setDescricao(rs.getString("descricao"));
				
				logger.info("Permissao de usuario: " + perm.getDescricao() + " Usuario: " + idUsuario);				
				perms.getListaPermissoesFuncionalidade().add(perm);
			}
		} 		
		
		catch (Exception e) 
		{
			logger.error("Falha na obtendo da lista de Permissoes de Funcionalidades por Usuario! id:" + idUsuario + " Falha: " + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravissimo ao destruir conexao com banco de dados! " + e.getMessage(), e);}
		}		
		
		return perms;
	}	
	
	public void EnviarRespostaRequisicaoXML(HttpServletResponse response, PermissoesFuncionalidade perms) 
	{

    	//Formando dados para envio
		JAXBContext perms_context;
		try
		{
			perms_context = JAXBContext.newInstance(PermissoesFuncionalidade.class);
			Marshaller marsHall = perms_context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(perms, sw);
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

