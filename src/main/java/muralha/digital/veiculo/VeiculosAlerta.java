package muralha.digital.veiculo;

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


@XmlRootElement		(name="VeiculosAlerta") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class VeiculosAlerta
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(VeiculosAlerta.class);
	
	@XmlElementWrapper	(name = "ListaVeiculosAlerta")
	@XmlElement			(name = "VeiculoAlerta")	
	private List<VeiculoAlerta> listaVeiculosAlerta;	
	

	public List<VeiculoAlerta> getListaVeiculosAlerta() {
		return listaVeiculosAlerta;
	}


	public void setListaVeiculosAlerta(List<VeiculoAlerta> listaVeiculosAlerta) {
		this.listaVeiculosAlerta = listaVeiculosAlerta;
	}


	public VeiculosAlerta()
	{
		super();
	}
	
	public static List<VeiculoAlerta> ObterListaVeiculosPorIdAlerta(UUID idAlerta) throws ConexaoException, SQLException 
	{
		
		List<VeiculoAlerta> listaRet = new ArrayList<VeiculoAlerta>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT av.id_alerta, ");
		sbSQL.append(" 		  av.id_veiculo_tempo_real, ");
		sbSQL.append(" 		  vtri.id AS id_imagem_tempo_real ");
		sbSQL.append(" FROM   muralha.alerta_veiculo av ");
		sbSQL.append(" 		  INNER JOIN muralha.veiculo_tempo_real vtr ");
		sbSQL.append(" 		  		ON  vtr.id = av.id_veiculo_tempo_real ");
		sbSQL.append(" 		  INNER JOIN muralha.veiculo_tempo_real_imagem vtri ");
		sbSQL.append(" 		  		ON  vtri.id_veiculo_tempo_real = av.id_veiculo_tempo_real ");
		sbSQL.append(" WHERE  av.id_alerta = ? ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  vtr.data, ");
		sbSQL.append(" 		  vtri.indice_imagem ");


		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());
			
			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				VeiculoAlerta item = new VeiculoAlerta();

				item.setIdAlerta(UUID.fromString(rs.getString("id_alerta")));
				item.setIdVeiculoTempoReal(UUID.fromString(rs.getString("id_veiculo_tempo_real")));
				item.setIdImagemTempoReal(UUID.fromString(rs.getString("id_imagem_tempo_real")));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaVeiculosAlerta):: ", e);
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
