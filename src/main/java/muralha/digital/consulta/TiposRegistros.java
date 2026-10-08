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


@XmlRootElement		(name="TiposRegistro") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class TiposRegistros
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(TiposRegistros.class);
	
	@XmlElementWrapper	(name = "ListaTiposRegistro")
	@XmlElement			(name = "TipoRegistro")	
	private List<TipoRegistro> listaTiposRegistro;	
	

	public List<TipoRegistro> getListaTiposRegistro() {
		return listaTiposRegistro;
	}


	public void setListaTiposRegistro(List<TipoRegistro> listaTiposRegistro) {
		this.listaTiposRegistro = listaTiposRegistro;
	}


	public TiposRegistros()
	{
		super();
	}
	
	public static List<TipoRegistro> ObterListaTiposRegistro() throws ConexaoException, SQLException 
	{
		
		List<TipoRegistro> listaRet = new ArrayList<TipoRegistro>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.tipo_registro ");
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
				TipoRegistro item = new TipoRegistro();

				item.setId(UUID.fromString(rs.getString("id")));
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
}
