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

@XmlRootElement(name = "TiposIndividuo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimIndividuosTipos {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(BoletimIndividuosTipos.class);

	@XmlElementWrapper(name = "ListaTiposIndividuo")
	@XmlElement(name = "IndividuoTipo")
	private List<BoletimIndividuoTipo> listaTiposIndividuo;

	public List<BoletimIndividuoTipo> getListaTiposIndividuo() {
		return listaTiposIndividuo;
	}

	public void setListaTiposIndividuo(List<BoletimIndividuoTipo> listaTiposIndividuo) {
		this.listaTiposIndividuo = listaTiposIndividuo;
	}

	public BoletimIndividuosTipos() {
		super();
	}

	public static List<BoletimIndividuoTipo> ObterListaTiposIndividuo() throws ConexaoException, SQLException {
		List<BoletimIndividuoTipo> listaRet = new ArrayList<>();

		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" SELECT id, ");
		sbSQL.append("        descricao ");
		sbSQL.append(" FROM   muralha.boletim_individuo_tipo ");
		sbSQL.append(" ORDER BY descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			rs = ps.executeQuery();

			while (rs.next()) {
				BoletimIndividuoTipo tipo = new BoletimIndividuoTipo();
				tipo.setId(rs.getInt("id"));
				tipo.setDescricao(rs.getString("descricao"));
				listaRet.add(tipo);
			}

		} catch (Exception e) {
			logger.error("Erro ao consultar tipos de indivíduo", e);
			throw new SQLException("Erro ao consultar tipos de indivíduo: ", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("Erro ao fechar conexão", e);
			}
		}

		return listaRet;
	}
}
