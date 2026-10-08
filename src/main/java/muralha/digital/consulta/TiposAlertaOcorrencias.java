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


@XmlRootElement		(name="TiposAlertasOcorrencias") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class TiposAlertaOcorrencias
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(TiposAlertaOcorrencias.class);
	
	@XmlElementWrapper	(name = "ListaTiposAlertasOcorrencias")
	@XmlElement			(name = "TipoAlertaOcorrencia")	
	private List<TipoAlertaOcorrencia> listaTiposAlertasOcorrencias;	
	

	public List<TipoAlertaOcorrencia> getListaTiposAlertasOcorrencias() {
		return listaTiposAlertasOcorrencias;
	}


	public void setListaTiposAlertasOcorrencias(List<TipoAlertaOcorrencia> listaTiposAlertasOcorrencias) {
		this.listaTiposAlertasOcorrencias = listaTiposAlertasOcorrencias;
	}


	public TiposAlertaOcorrencias()
	{
		super();
	}
	
	public static List<TipoAlertaOcorrencia> ObterListaTiposAlertasOcorrencias() throws ConexaoException, SQLException 
	{
		
		List<TipoAlertaOcorrencia> listaRet = new ArrayList<TipoAlertaOcorrencia>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(tipo) AS tipo, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.tipo_alerta_ocorrencia ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  tipo ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				TipoAlertaOcorrencia item = new TipoAlertaOcorrencia();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setTipo(rs.getString("tipo"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaTiposRegistro):: ", e);
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
	
	public static boolean PermiteCadMonitoradoSemPlaca(UUID id) throws ConexaoException, SQLException 
	{
		boolean retorno = false;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT permite_monitorado_sem_placa ");
		sbSQL.append(" FROM   muralha.tipo_alerta_ocorrencia ");
		sbSQL.append(" WHERE  id = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());
			
			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				retorno = rs.getBoolean("permite_monitorado_sem_placa");
			}
		}
		catch (Exception e)
		{
			throw new SQLException("Erro ao montar SQL (ObterListaTiposRegistro):: ", e);
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
