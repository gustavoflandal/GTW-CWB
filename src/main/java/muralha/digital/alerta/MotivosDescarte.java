package muralha.digital.alerta;

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


@XmlRootElement		(name="MotivosDescarte") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class MotivosDescarte
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(MotivosDescarte.class);
	
	@XmlElementWrapper	(name = "ListaMotivosDescarte")
	@XmlElement			(name = "MotivoDescarte")	
	private List<MotivoDescarte> listaMotivosDescarte;	
	

	public List<MotivoDescarte> getListaMotivosDescarte() {
		return listaMotivosDescarte;
	}

	public void setListaMotivosDescarte(List<MotivoDescarte> listaMotivosDescarte) {
		this.listaMotivosDescarte = listaMotivosDescarte;
	}


	public MotivosDescarte()
	{
		super();
	}
	
	public static List<MotivoDescarte> ObterListaMotivosDescarte() throws ConexaoException, SQLException 
	{
		
		List<MotivoDescarte> listaRet = new ArrayList<MotivoDescarte>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.motivo_descarte ");
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
				MotivoDescarte item = new MotivoDescarte();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaMotivosDescarte):: ", e);
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
	
	public static MotivoDescarte ObterMotivoDescartePorId(UUID id) throws ConexaoException, SQLException 
	{
		
		MotivoDescarte motivo = new MotivoDescarte();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.motivo_descarte ");
		sbSQL.append(" WHERE  id = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, id.toString());
			
			rs = ps.executeQuery();
			if (rs.next()) 
			{
				motivo.setId(UUID.fromString(rs.getString("id")));
				motivo.setDescricao(rs.getString("descricao"));
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterMotivoDescartePorId):: ", e);
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
		
		return motivo;
	}
}
