package muralha.digital.ocorrencia;

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


@XmlRootElement		(name="StatusOcorrencias") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class StatusOcorrencias
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(StatusOcorrencias.class);
	
	@XmlElementWrapper	(name = "ListaStatusOcorrencias")
	@XmlElement			(name = "StatusOcorrencia")	
	private List<StatusOcorrencia> listaStatusOcorrencias;	
	

	public List<StatusOcorrencia> getListaStatusOcorrencias() {
		return listaStatusOcorrencias;
	}


	public void setListaStatusOcorrencias(List<StatusOcorrencia> listaStatusOcorrencias) {
		this.listaStatusOcorrencias = listaStatusOcorrencias;
	}


	public StatusOcorrencias()
	{
		super();
	}
	
	public static List<StatusOcorrencia> ObterListaStatusOcorrenciaFinalizacao(UUID idTipoOcorrencia) throws ConexaoException, SQLException 
	{
		
		List<StatusOcorrencia> listaRet = new ArrayList<StatusOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT s.id_status, ");
		sbSQL.append(" 		  s.descricao ");
		sbSQL.append(" FROM   muralha.v_status_ocorrencia_finalizacao s ");
		sbSQL.append(" 		  JOIN muralha.tipo_ocorrencia_status t ");
		sbSQL.append(" 			   ON  t.id_status_ocorrencia = s.id_status ");
		sbSQL.append(" WHERE  t.id_tipo_ocorrencia = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  s.descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoOcorrencia.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				StatusOcorrencia item = new StatusOcorrencia();

				item.setId(UUID.fromString(rs.getString("id_status")));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaStatusOcorrenciaFinalizacao):: ", e);
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
	
	public static boolean isStatusOcorrenciaFinalizacao(UUID idTipoOcorrencia, UUID idStatusOcorrencia) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT s.id_status ");
		sbSQL.append(" FROM   muralha.v_status_ocorrencia_finalizacao s ");
		sbSQL.append(" 		  JOIN muralha.tipo_ocorrencia_status t ");
		sbSQL.append(" 			   ON  t.id_status_ocorrencia = s.id_status ");
		sbSQL.append(" WHERE  t.id_tipo_ocorrencia = ? ");
		sbSQL.append(" 		  AND t.id_status_ocorrencia = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idTipoOcorrencia.toString());
			ps.setString(2, idStatusOcorrencia.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) { retorno = true; }
			
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (isStatusOcorrenciaFinalizacao):: ", e);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			}
			catch (SQLException e)
			{
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		
		return retorno;
	}
}
