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

@XmlRootElement(name = "BoletimSituacoes") 
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimSituacoes {	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(BoletimSituacoes.class);
	
	@XmlElementWrapper(name = "ListaSituacoes")
	@XmlElement(name = "Situacao")	
	private List<BoletimSituacao> ListaBoletimSituacoes;	

	public List<BoletimSituacao> getSituacoes() {
		return ListaBoletimSituacoes;
	}

	public void setSituacoes(List<BoletimSituacao> situacoes) {
		this.ListaBoletimSituacoes = situacoes;
	}

	public BoletimSituacoes() {
		super();
	}
	
	public static List<BoletimSituacao> obterListaSituacoes() throws ConexaoException, SQLException {
		List<BoletimSituacao> listaRet = new ArrayList<BoletimSituacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append("        descricao ");
		sbSQL.append(" FROM   muralha.boletim_situacao ");
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
				BoletimSituacao item = new BoletimSituacao();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			logger.error("Erro ao consultar situações de boletim: ", e);
			throw new SQLException("Erro ao consultar situações de boletim: ", e);
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