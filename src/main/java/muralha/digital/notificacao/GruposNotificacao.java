package muralha.digital.notificacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;


@XmlRootElement		(name="GruposNotificacao") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class GruposNotificacao
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(GruposNotificacao.class);
	
	@XmlElementWrapper	(name = "ListaGruposNotificacao")
	@XmlElement			(name = "GrupoNotificacao")	
	private List<GrupoNotificacao> listaGruposNotificacao;	
	

	public List<GrupoNotificacao> getListaGruposNotificacao() {
		return listaGruposNotificacao;
	}

	public void setListaGruposNotificacao(List<GrupoNotificacao> listaGruposNotificacao) {
		this.listaGruposNotificacao = listaGruposNotificacao;
	}


	public GruposNotificacao()
	{
		super();
	}
	
	public static List<GrupoNotificacao> ObterListaGruposNotificacao(UUID idTipoRegistro, UUID idTipoAlertaOcorrencia, UUID idTipoNotificacao) throws ConexaoException, SQLException 
	{
		
		List<GrupoNotificacao> listaRet = new ArrayList<GrupoNotificacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT cgn.id, ");
		sbSQL.append(" 		  tr.id AS id_tipo_registro, ");
		sbSQL.append(" 		  RTRIM(tr.descricao) AS tipo_registro, ");
		sbSQL.append(" 		  tao.id AS id_tipo_alerta_ocorrencia, ");
		sbSQL.append(" 		  RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
		sbSQL.append(" 		  sg.id_grupo, ");
		sbSQL.append(" 		  RTRIM(sg.descricao) AS grupo, ");
		sbSQL.append(" 		  tn.id AS id_tipo_notificacao, ");
		sbSQL.append(" 		  RTRIM(tn.descricao) AS tipo_notificacao ");
		sbSQL.append(" FROM   muralha.config_grupo_permissao cgn ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_registro tr ");
		sbSQL.append(" 		   	   ON  tr.id = cgn.id_tipo_registro ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_alerta_ocorrencia tao "); 
		sbSQL.append(" 		   	   ON  tao.id = cgn.id_tipo_alerta_ocorrencia ");
		sbSQL.append(" 		  INNER JOIN sis_grupo sg ");
		sbSQL.append(" 		   	   ON  sg.id_grupo = cgn.id_grupo ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_notificacao tn ");
		sbSQL.append(" 		  	   ON  tn.id = cgn.id_tipo_notificacao ");
		sbSQL.append(" WHERE  cgn.ativo = 1 ");
		sbSQL.append(" 		  AND cgn.id_tipo_registro = ? ");
		sbSQL.append(" 		  AND cgn.id_tipo_alerta_ocorrencia = ? ");
		sbSQL.append(" 		  AND cgn.id_tipo_notificacao = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  sg.descricao ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoRegistro.toString());
			ps.setString(2, idTipoAlertaOcorrencia.toString());
			ps.setString(3, idTipoNotificacao.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				GrupoNotificacao item = new GrupoNotificacao();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
				item.setTipoRegistro(rs.getString("tipo_registro"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setIdGrupo(rs.getInt("id_grupo"));
				item.setGrupo(rs.getString("grupo"));
				item.setIdTipoNotificacao(UUID.fromString(rs.getString("id_tipo_notificacao")));
				item.setTipoNotificacao(rs.getString("tipo_notificacao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaGruposNotificacao):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
	

	public static List<GrupoNotificacao> ObterListaGruposPorOcorrenciaTipoNotificacao(UUID idOcorrencia, UUID idTipoNotificacao, UUID idTipoRegistro) throws ConexaoException, SQLException 
	{
		
		List<GrupoNotificacao> listaRet = new ArrayList<GrupoNotificacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT ocn.id, ");
		sbSQL.append(" 		  tr.id AS id_tipo_registro, ");
		sbSQL.append(" 		  RTRIM(tr.descricao) AS tipo_registro, ");
		sbSQL.append(" 		  tao.id AS id_tipo_alerta_ocorrencia, ");
		sbSQL.append(" 		  RTRIM(tao.tipo) AS tipo_alerta_ocorrencia, ");
		sbSQL.append(" 		  sg.id_grupo, ");
		sbSQL.append(" 		  RTRIM(sg.descricao) AS grupo, ");
		sbSQL.append(" 		  tn.id AS id_tipo_notificacao, ");
		sbSQL.append(" 		  RTRIM(tn.descricao) AS tipo_notificacao ");
		sbSQL.append(" FROM   muralha.ocorrencia_notificacao ocn ");
		sbSQL.append(" 		  INNER JOIN muralha.ocorrencia o ");
		sbSQL.append(" 		  	   ON  o.id = ocn.id_ocorrencia ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_alerta_ocorrencia tao ");
		sbSQL.append(" 		  	   ON  tao.id = o.id_tipo_alerta_ocorrencia ");
		sbSQL.append(" 		  INNER JOIN sis_grupo sg ");
		sbSQL.append(" 		  	   ON  sg.id_grupo = ocn.id_grupo ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_notificacao tn ");
		sbSQL.append(" 		  	   ON  tn.id = ocn.id_tipo_notificacao ");
		sbSQL.append(" 		  INNER JOIN muralha.tipo_registro tr ");
		sbSQL.append(" 		  	   ON  tr.id = ? ");
		sbSQL.append(" WHERE  ocn.id_ocorrencia = ? ");
		sbSQL.append(" 		  AND ocn.id_tipo_notificacao =  ?");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  sg.descricao ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoRegistro.toString());
			ps.setString(2, idOcorrencia.toString());
			ps.setString(3, idTipoNotificacao.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				GrupoNotificacao item = new GrupoNotificacao();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
				item.setTipoRegistro(rs.getString("tipo_registro"));
				item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
				item.setTipoAlertaOcorrencia(rs.getString("tipo_alerta_ocorrencia"));
				item.setIdGrupo(rs.getInt("id_grupo"));
				item.setGrupo(rs.getString("grupo"));
				item.setIdTipoNotificacao(UUID.fromString(rs.getString("id_tipo_notificacao")));
				item.setTipoNotificacao(rs.getString("tipo_notificacao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaGruposNotificacao):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
	
	
	public static List<InternetAddress> ObterEmailsPorIdGrupo(Integer idGrupo) throws ConexaoException, SQLException 
	{
		
		List<InternetAddress> listaEmails = new ArrayList<InternetAddress>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT sg.id_grupo, ");
		sbSQL.append(" 		  su.id_usuario, ");
		sbSQL.append(" 		  RTRIM(su.email) AS email ");
		sbSQL.append(" FROM   sis_grupo sg ");
		sbSQL.append(" 		  INNER JOIN sis_usuario_grupo sug ");
		sbSQL.append(" 			   ON  sug.id_grupo = sg.id_grupo ");
		sbSQL.append(" 		  INNER JOIN sis_usuario su ");
		sbSQL.append(" 			   ON  su.id_usuario = sug.id_usuario ");
		sbSQL.append(" WHERE  sg.id_grupo = ? ");
		sbSQL.append(" 		  AND dbo.VerificarEmail(su.email) = 1 ");
//		sbSQL.append(" 		  AND su.id_usuario = 4 ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idGrupo);
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				InternetAddress email = new InternetAddress(rs.getString("email"));
				listaEmails.add(email);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaEmailsPorIdGrupo):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaEmails;
	}
	
	public static List<String> ObterTelefonesPorIdGrupo(Integer idGrupo) throws ConexaoException, SQLException 
	{
		
		List<String> listaTelefone = new ArrayList<String>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT sg.id_grupo, ");
		sbSQL.append(" 		  su.id_usuario, ");
		sbSQL.append(" 		  RTRIM(su.telefone) AS telefone ");
		sbSQL.append(" FROM   sis_grupo sg ");
		sbSQL.append(" 		  INNER JOIN sis_usuario_grupo sug ");
		sbSQL.append(" 			   ON  sug.id_grupo = sg.id_grupo ");
		sbSQL.append(" 		  INNER JOIN sis_usuario su ");
		sbSQL.append(" 			   ON  su.id_usuario = sug.id_usuario ");
		sbSQL.append(" WHERE  sg.id_grupo = ? ");
		sbSQL.append(" 		  AND su.telefone IS NOT NULL AND LTRIM(RTRIM(su.telefone)) != '' ");
//		sbSQL.append(" 		  AND su.id_usuario = 4 ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, idGrupo);
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				String telefone = rs.getString("telefone");
				listaTelefone.add(telefone);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterTelefonesPorIdGrupo):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaTelefone;
	}
	
	public static List<GrupoNotificacao> ConsultarConfiguracaoPorTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) 
	        throws ConexaoException, SQLException {

		List<GrupoNotificacao> listaRet = new ArrayList<GrupoNotificacao>();	
		String sql = "SELECT * FROM muralha.config_grupo_permissao WHERE id_tipo_alerta_ocorrencia = ?";

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement ps = conn.prepareStatement(sql);
	    ) {
	    	
	    	if (idTipoAlertaOcorrencia != null) {
	    	    ps.setString(1, idTipoAlertaOcorrencia.toString());
	    	} else {
	    	    ps.setNull(1, java.sql.Types.VARCHAR);
	    	}

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            GrupoNotificacao item = new GrupoNotificacao();

	            item.setId(UUID.fromString(rs.getString("id")));
	            item.setIdTipoRegistro(UUID.fromString(rs.getString("id_tipo_registro")));
	            item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));
	            item.setIdGrupo(rs.getInt("id_grupo"));
	            item.setIdTipoNotificacao(UUID.fromString(rs.getString("id_tipo_notificacao")));

	            listaRet.add(item);
	        }

	        return listaRet;

	    } catch (Exception e) {
	        throw new SQLException("Erro ao configurações por grupoId", e);
	    }
	}
	
	public static Boolean DeletarConfigsPorIds(UUID[] ids) 
	        throws ConexaoException, SQLException {

	    String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
	    String sql = "delete from muralha.config_grupo_permissao where id in (" + placeholders + ")";

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement ps = conn.prepareStatement(sql);
	    ) {
	        
	        for (int i = 0; i < ids.length; i++) {
	            ps.setObject(i + 1, ids[i]);
	        }
	        
	        ps.executeUpdate();

	        return true;

	    } catch (Exception e) {
	        throw new SQLException("Erro deletar as configurações por id", e);
	    }
	}
	
	public static Boolean InserirConfigsPorLista(List<GrupoNotificacao> grupos) 
	        throws ConexaoException, SQLException {

	    String sql = "INSERT INTO muralha.config_grupo_permissao " +
	                 "(id, id_tipo_registro, id_tipo_alerta_ocorrencia, id_grupo, id_tipo_notificacao,"
	                 + " id_usuario, data_cadastro, data_atualizacao, ativo) " +
	                 "VALUES (NEWID(), ?, ?, ?, ?,?, GETDATE(), NULL, 1)";

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement ps = conn.prepareStatement(sql);
	    ) {
	        for (GrupoNotificacao grupo : grupos) {
	        	ps.setObject(1, grupo.getIdTipoRegistro());
	        	ps.setObject(2, grupo.getIdTipoAlertaOcorrencia());
	        	ps.setInt(3, grupo.getIdGrupo());
	        	ps.setObject(4, grupo.getIdTipoNotificacao());
	        	ps.setInt(5, grupo.getIdUsuario());
            
	            ps.addBatch(); // adiciona a execução em lote
	        }
	        	      

	        ps.executeBatch(); // executa tudo de uma vez
	        
	        return true;

	    } catch (Exception e) {
	        throw new SQLException("Erro ao inserir configurações de grupos por lista", e);
	    }
	}
	
	public static List<GrupoNotificacao> BuscarGruposSelecionados() 
	        throws ConexaoException, SQLException {

		List<GrupoNotificacao> listaRet = new ArrayList<GrupoNotificacao>();
	    String sql = "select vg.id_grupo, cg.id_tipo_alerta_ocorrencia\r\n"
	    		+ "from muralha.v_grupo_alertas vg\r\n"
	    		+ "left join muralha.config_grupo_permissao cg\r\n"
	    		+ "on cg.id_grupo = vg.id_grupo\r\n"
	    		+ "where cg.id_tipo_alerta_ocorrencia is not null\r\n"
	    		+ "group by vg.id_grupo, vg.id_grupo_pai, vg.pagina_inicial, vg.nivel_ligacao, vg.descricao, cg.id_tipo_alerta_ocorrencia\r\n"
	    		+ "order by vg.id_grupo asc";

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement ps = conn.prepareStatement(sql);
	    ) {
	    	ResultSet rs = ps.executeQuery();
	    	
	        while (rs.next()) {
	            GrupoNotificacao item = new GrupoNotificacao();

	            item.setIdGrupo(rs.getInt("id_grupo"));
	            item.setIdTipoAlertaOcorrencia(UUID.fromString(rs.getString("id_tipo_alerta_ocorrencia")));

	            listaRet.add(item);
	        }
	        
	        return listaRet;
	     
	    } catch (Exception e) {
	        throw new SQLException("Erro ao inserir configurações de grupos por lista", e);
	    }
	}

	public static Boolean ConfigAgenteGuarnicao(Boolean status) throws ConexaoException, SQLException 
	{
		String sql = "UPDATE sis_grupo SET config_muralha = ? WHERE LOWER(RTRIM(LTRIM(descricao))) = LOWER(RTRIM(LTRIM('Agente de Guarnição')))";
		
		try (Connection conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sql)) {
			
			ps.setInt(1, status ? 1 : 0);
			int rowsAffected = ps.executeUpdate();
			
			return rowsAffected > 0;
		}
	}

	public static Integer ObterStatusAgenteGuarnicao() throws ConexaoException, SQLException 
	{
		String sql = "SELECT config_muralha FROM sis_grupo WHERE LOWER(RTRIM(LTRIM(descricao))) = LOWER(RTRIM(LTRIM('Agente de Guarnição')))";
		
		try (Connection conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			
			if (rs.next()) {
				return rs.getInt("config_muralha");
			} else {
				return 0;
			}
		}
	}
}
