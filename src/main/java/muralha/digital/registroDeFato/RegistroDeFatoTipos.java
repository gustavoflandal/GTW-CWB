package muralha.digital.registroDeFato;

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

@XmlRootElement(name = "RegistroDeFatoTipos") 
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoTipos {	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(RegistroDeFatoTipos.class);
	
	@XmlElementWrapper(name = "RegistroDeFatoTipoLista")
	@XmlElement(name = "RegistroDeFatoTipo")	
	private List<RegistroDeFatoTipo> listaTiposOcorrencia;	

	public List<RegistroDeFatoTipo> getListaTiposRegistroDeFatos() {
		return listaTiposOcorrencia;
	}

	public void setListaTiposRegistroDeFatos(List<RegistroDeFatoTipo> listaTiposOcorrencia) {
		this.listaTiposOcorrencia = listaTiposOcorrencia;
	}

	public RegistroDeFatoTipos() {
		super();
	}
	
	public static List<RegistroDeFatoTipo> ObterListaTiposRegistroDeFato() throws ConexaoException, SQLException {
		List<RegistroDeFatoTipo> listaRet = new ArrayList<RegistroDeFatoTipo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append("        tipo_desc ");
		sbSQL.append(" FROM   muralha.registro_fato_tipo ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        tipo_desc ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				RegistroDeFatoTipo item = new RegistroDeFatoTipo();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("tipo_desc"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao consultar tipos de Registro de Fato: ", e);
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