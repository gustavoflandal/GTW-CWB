package muralha.digital.acessos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Usuario 
{
	private static final Logger logger = Logger.getLogger(Usuario.class);
	
	private Integer idUsuario;
	private String usuario;
	private String nome;
	private String email;
	private boolean ativo;
	private String paginaInicial;
	private int		idGrupo;
	private String	descGrupo;
	private String  token;
	private String telefone;
	
	public Usuario() {
    }
	
	public Usuario(Integer idUsuario, String usuario, String nome, String email, boolean ativo, String paginaInicial) 
	{
		this.idUsuario = idUsuario;
		this.usuario = usuario;
		this.nome = nome;
		this.email = email;
		this.ativo = ativo;
		this.paginaInicial = paginaInicial;		
	}
	
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean setPossuiEmail(boolean possuiEmail) {
		return possuiEmail;
	}

	public boolean setPossuiTelefone(boolean possuiTelefone) {
		return possuiTelefone;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getPaginaInicial() {
		
		if (paginaInicial != null)
			return paginaInicial;
		else 
			return "";	
	}

	public void setPaginaInicial(String paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	public int getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getDescGrupo() {
		return descGrupo;
	}

	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}		

	public static Usuario validarUsuario(String nome, String senhaMD5, String endereco_ip)
	{
		Usuario 		usr = null;
		Connection 			conn = null;
		PreparedStatement 	ps = null;
		ResultSet 			rs = null;		
		CallableStatement   cs1 = null;
		
		try 
		{
			
			String strSQL = "";
	        
			strSQL = strSQL + " select 													";
			strSQL = strSQL + " 	su.id_usuario,                                      ";
			strSQL = strSQL + " 	su.usuario,                                         ";
			strSQL = strSQL + " 	su.nome,                                            ";
			strSQL = strSQL + " 	su.email,                                           ";
			strSQL = strSQL + " 	su.ativo,                                           ";
			strSQL = strSQL + " 	su.pagina_inicial                                   ";
			strSQL = strSQL + " from v_sis_usuario su (NOLOCK)                          ";
			strSQL = strSQL + " where                                                   ";
			strSQL = strSQL + " 	su.usuario = ?                             			";
			strSQL = strSQL + " 	and su.senha = ?     								";  
			strSQL = strSQL + " 	and su.ativo = 1                                    ";
						
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(strSQL);
			
			ps.setString(1, nome);
			ps.setString(2, senhaMD5);
			rs = ps.executeQuery();
	        
	        if (rs.next()) 
	        {
	        	usr = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5), rs.getString(6));
	        	logger.info("Usuário logado no sistema: ID: " + usr.getIdUsuario() + " Nome: " + usr.getNome());
	        	
	        	try
	        	{
		        	cs1 = conn.prepareCall("{call spu_ppv_sis_usuario_token (?,?,?)}");
		        	cs1.setInt(1, usr.getIdUsuario());
		        	cs1.setString(2, endereco_ip);
		        	cs1.registerOutParameter(3, Types.VARCHAR);
		        	cs1.execute();
		        	String token = cs1.getString(3);
		        	usr.setToken(token);
		        	
		        	logger.info("Gerou Token " + token + " para usuario " + usr.getIdUsuario() + " e endereco IP " + endereco_ip);
	        	}
	        	catch(Exception e) {
	        		logger.error("Erro ao obter Token", e);
	        	}
	        	finally{
	        		if (cs1 != null)
	        			cs1.close();
	        	}
	        }
	        else{
	        	logger.info("Tentativa de login no sistema. Nome Usuario:" + nome);
	        }
		} 
		
		catch (Exception e) 
		{
			logger.error("Falha na validacao de usuario!" + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravissimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return usr;
	}
	
	public static Usuario validarUsuario(Integer idUsuario)
	{
		Usuario 		usr = null;
		Connection 			conn = null;
		PreparedStatement 	ps = null;
		ResultSet 			rs = null;		
		
		try 
		{
			
			String strSQL = "";
	        
			strSQL = strSQL + " select 													";
			strSQL = strSQL + " 	su.id_usuario,                                      ";
			strSQL = strSQL + " 	su.usuario,                                         ";
			strSQL = strSQL + " 	su.nome,                                            ";
			strSQL = strSQL + " 	su.email,                                           ";
			strSQL = strSQL + " 	su.ativo,                                           ";
			strSQL = strSQL + " 	su.senha,                                           ";
			strSQL = strSQL + " 	su.pagina_inicial                                   ";
			strSQL = strSQL + " from v_sis_usuario su (NOLOCK)                          ";
			strSQL = strSQL + " where                                                   ";
			strSQL = strSQL + " 	su.id_usuario = ?                            		";
			strSQL = strSQL + " 	and su.ativo = 1                                    ";
			
			//ConexaoBD_Basic_Init conn = new ConexaoBD_Basic_Init();
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(strSQL);
			
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();
	        
	        if (rs.next()) 
	        {
	        	usr = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5), rs.getString(6));
	        	logger.info("Usuario logado no sistema: ID: " + usr.getIdUsuario() + " Nome: " + usr.getNome());
	        }
	        else
	        {
	        	logger.info("Tentativa de login no sistema. ID Usuario:" + idUsuario);
	        }
		} 
		
		catch (Exception e) 
		{
			logger.error("Falha na validacao de usuario!" + e.getMessage(), e);
		}
		
		finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}		
		
		return usr;
	}
	
	public static String acessoViaToken(Integer idUsuario, String dataCriacao, String baseUrl, String path) {
	    String redirectUrl = "";

	    try (
	        Connection conn = Conexao.getConexao();
	        PreparedStatement ps = conn.prepareStatement(
	            "INSERT INTO muralha.token_acesso_mobilidade_urbana ([key], id_usuario, data_criacao) VALUES (?, ?, ?)",
	            PreparedStatement.RETURN_GENERATED_KEYS
	        )
	    ) {
	        String key = UUID.randomUUID().toString().replace("-", "");

	        ps.setString(1, key);
	        ps.setInt(2, idUsuario);
	        ps.setString(3, dataCriacao);

	        int linhasAfetadas = ps.executeUpdate();

	        if (linhasAfetadas > 0) {
	            try (ResultSet rs = ps.getGeneratedKeys()) {
	                if (rs.next()) {
	                    Integer idGerado = rs.getInt(1);
	                    redirectUrl = baseUrl + path + "token=" + key;
	                    logger.info("Token gerado e persistido. ID: " + idGerado + " | Usuario: " + idUsuario);
	                }
	            }
	        }
	    } catch (Exception e) {
	        logger.error("Erro ao gerar token: " + e.getMessage(), e);
	    }

	    return redirectUrl; 
	}

}
