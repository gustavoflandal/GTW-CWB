package muralha.digital.boletim;

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

@XmlRootElement(name = "TiposOcorrencia") 
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimTipos {	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(BoletimTipos.class);
	
	@XmlElementWrapper(name = "ListaTiposOcorrencia")
	@XmlElement(name = "OcorrenciaTipo")	
	private List<OcorrenciaTipo> listaTiposOcorrencia;	

	public List<OcorrenciaTipo> getListaTiposOcorrencia() {
		return listaTiposOcorrencia;
	}

	public void setListaTiposOcorrencia(List<OcorrenciaTipo> listaTiposOcorrencia) {
		this.listaTiposOcorrencia = listaTiposOcorrencia;
	}

	public BoletimTipos() {
		super();
	}
	
	public static List<OcorrenciaTipo> ObterListaTiposOcorrencia() throws ConexaoException, SQLException {
		List<OcorrenciaTipo> listaRet = new ArrayList<OcorrenciaTipo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append("        descricao ");
		sbSQL.append(" FROM   muralha.ocorrencia_tipo ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				OcorrenciaTipo item = new OcorrenciaTipo();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao consultar tipos de ocorrência: ", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
}