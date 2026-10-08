package muralha.digital.consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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


@XmlRootElement		(name="StatusAlertasOcorrencias") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class StatusAlertasOcorrencias
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(StatusAlertasOcorrencias.class);
	
	@XmlElementWrapper	(name = "ListaStatusAlertasOcorrencias")
	@XmlElement			(name = "StatusAlertaOcorrencia")	
	private List<StatusAlertaOcorrencia> listaStatusAlertasOcorrencias;	
	

	public List<StatusAlertaOcorrencia> getListaStatusAlertasOcorrencias() {
		return listaStatusAlertasOcorrencias;
	}


	public void setListaStatusAlertasOcorrencias(List<StatusAlertaOcorrencia> listaStatusAlertasOcorrencias) {
		this.listaStatusAlertasOcorrencias = listaStatusAlertasOcorrencias;
	}


	public StatusAlertasOcorrencias()
	{
		super();
	}
	
	public static List<StatusAlertaOcorrencia> ObterListaStatusAlerta() throws ConexaoException, SQLException 
	{
		
		List<StatusAlertaOcorrencia> listaRet = new ArrayList<StatusAlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.status_alerta ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				StatusAlertaOcorrencia item = new StatusAlertaOcorrencia();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaStatusAlerta):: ", e);
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
	
	public static List<StatusAlertaOcorrencia> ObterListaStatusOcorrencia() throws ConexaoException, SQLException 
	{
		
		List<StatusAlertaOcorrencia> listaRet = new ArrayList<StatusAlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.status_ocorrencia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				StatusAlertaOcorrencia item = new StatusAlertaOcorrencia();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaStatusOcorrencia):: ", e);
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
	
	public static List<StatusAlertaOcorrencia> ObterListaStatusAlertaVinculado() throws ConexaoException, SQLException 
	{
		
		List<StatusAlertaOcorrencia> listaRet = new ArrayList<StatusAlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT s.id, ");
		sbSQL.append(" 		  RTRIM(s.descricao) AS descricao, ");
		sbSQL.append(" 		  sav.status_padrao ");
		sbSQL.append(" FROM   muralha.status_alerta s ");
		sbSQL.append(" 		  JOIN muralha.v_status_alerta_vinculado sav ");
		sbSQL.append(" 		  	   ON  sav.id = s.id ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  s.descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				StatusAlertaOcorrencia item = new StatusAlertaOcorrencia();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setDescricao(rs.getString("descricao"));
				item.setStatusPadrao(rs.getBoolean("status_padrao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaStatusAlertaVinculado):: ", e);
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
}
