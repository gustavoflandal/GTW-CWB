package muralha.digital.cidade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

@XmlRootElement		(name="Cidades") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class Cidades {
	@XmlTransient
	private static Logger logger = LogManager.getLogger(Cidades.class);
	
	@XmlElementWrapper	(name = "listaDeCidades")
	@XmlElement			(name = "cidades")	
	
	private List<Cidade> listaDeCidades;	
	
	public List<Cidade> getListaCidades() {
		return listaDeCidades;
	}
	
	public void setListaCidades(List<Cidade> listaDeCidades) {
		this.listaDeCidades = listaDeCidades;
	}

	public Cidades()
	{
		super();
	}
	
	//Somente busca as cidades cujo idEstado é 13 - Minas Gerais
	public static List<Cidade> ObterListaCidades() throws ConexaoException, SQLException {
		List<Cidade> listaRet = new ArrayList<Cidade>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT * from muralha.cidade c where c.id_estado = 13");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				Cidade item = new Cidade();

				item.setId(rs.getInt("id"));
				item.setIdEstado(rs.getInt("id_estado"));
				item.setNome(rs.getString("nome"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaCidades):: ", e);
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
