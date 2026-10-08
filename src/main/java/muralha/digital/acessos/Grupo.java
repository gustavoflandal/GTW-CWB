package muralha.digital.acessos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class Grupo {

	private static Logger logger = LogManager.getLogger(Grupo.class);
	
	private Integer idGrupo;
	private String descricao;
	private String paginaInicial;
	private int	nivel;
	
	public Grupo(Integer idGrupo, String descricao, String paginaInicial) {
		this.idGrupo = idGrupo;
		this.descricao = descricao;
		this.paginaInicial = paginaInicial;
	}
	
	public Grupo(){
		
	}
	
	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getPaginaInicial() {
		return paginaInicial;
	}
	public void setPaginaInicial(String paginaInicial) {
		this.paginaInicial = paginaInicial;
	}
	
	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public static List<Grupo> ObterGrupos() {
		
		logger.debug("Obtendo lista de Grupos");
		
		List<Grupo> grupos = new ArrayList<Grupo>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT id_grupo,	descricao,	pagina_inicial FROM sis_grupo (NOLOCK)");
			rs = ps.executeQuery();
			while(rs.next()) {
				Grupo g = new Grupo(rs.getInt(1), rs.getString(2), rs.getString(3));
				logger.debug("Grupo " + g.getDescricao());
				grupos.add(g);
			}
		}
		catch(Exception e) {
			
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
		
		return grupos;
		
	}
	
	public static Grupo ObterProxNivelAtendimento(int nivelAtual) {
		
		logger.debug("Obtendo lista de Grupos");
		
		Connection 			conn = null;
		ResultSet 			rs = null;
		PreparedStatement 	stmt = null;
		Grupo 				grupo = null;
		
		try {
			conn = Conexao.getConexao();
			
			String strSQL  = "";
			strSQL = strSQL + " select 																	  ";
			strSQL = strSQL + " 	id_grupo,                                                       	  ";
			strSQL = strSQL + " 	descricao,                                                      	  ";
			strSQL = strSQL + " 	id_grupo_pai,                                                   	  ";
			strSQL = strSQL + " 	nivel_ligacao                                                   	  ";
			strSQL = strSQL + " from sis_grupo  (NOLOCK)                                                    	  ";
			strSQL = strSQL + " where nivel_ligacao = (                                             	  ";
			strSQL = strSQL + " 						select                                      	  ";
			strSQL = strSQL + " 							min(s.nivel_ligacao) as nivel_ligacao   	  ";
			strSQL = strSQL + " 						from sis_grupo s (NOLOCK)                          	  ";
			strSQL = strSQL + " 						where s.nivel_ligacao > ?                   	  ";
			strSQL = strSQL + " 					  )	                                            	  ";
						                                                                                	 
			stmt = conn.prepareStatement(strSQL);

			stmt.setInt(1, nivelAtual);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) 
			{
				grupo = new Grupo();
				grupo.setIdGrupo(rs.getInt(1)); 
				grupo.setDescricao(rs.getString(2));
				grupo.setNivel(rs.getInt(4));
				
				logger.debug("ObterProxNivelAtendimento:  Atual --> Nivel: " + nivelAtual + " Prox --> " + grupo.getNivel() + " IdGrupo: " + grupo.getIdGrupo());
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter consulta ObterProxNivelAtendimento()", e);
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
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return grupo;
		
	}	
	
}
