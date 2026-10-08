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

@XmlRootElement(name = "RegistroDeFatoSituacoes") 
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoSituacoes {	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(RegistroDeFatoSituacoes.class);
	
	@XmlElementWrapper(name = "ListaSituacoes")
	@XmlElement(name = "Situacao")	
	private List<RegistroDeFatoSituacao> ListaBoletimSituacoes;	

	public List<RegistroDeFatoSituacao> getSituacoes() {
		return ListaBoletimSituacoes;
	}

	public void setSituacoes(List<RegistroDeFatoSituacao> situacoes) {
		this.ListaBoletimSituacoes = situacoes;
	}

	public RegistroDeFatoSituacoes() {
		super();
	}
	
	public static List<RegistroDeFatoSituacao> obterListaSituacoes() throws ConexaoException, SQLException {
		List<RegistroDeFatoSituacao> listaRet = new ArrayList<RegistroDeFatoSituacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append("        descricao ");
		sbSQL.append(" FROM   muralha.registro_fato_status ");
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
				RegistroDeFatoSituacao item = new RegistroDeFatoSituacao();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			logger.error("Erro ao consultar situações de registro de fato: ", e);
			throw new SQLException("Erro ao consultar situações de registro de fato: ", e);
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