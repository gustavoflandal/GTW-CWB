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

@XmlRootElement(name = "BoletimCidades")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimCidades {
	@XmlTransient
	private static Logger logger = LogManager.getLogger(BoletimCidades.class);

	@XmlElementWrapper(name = "ListaCidades")
	@XmlElement(name = "Cidade")
	private List<Cidade> listaCidades;

	public List<Cidade> getListaCidades() {
		return listaCidades;
	}

	public void setListaCidades(List<Cidade> listaCidades) {
		this.listaCidades = listaCidades;
	}

	public BoletimCidades() {
		super();
	}

	public static List<Cidade> obterListaCidades() throws ConexaoException, SQLException {
		List<Cidade> listaRet = new ArrayList<Cidade>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT id, ");
		sbSQL.append("        id_estado, ");
		sbSQL.append("        nome ");
		sbSQL.append(" FROM   muralha.cidade ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append("        nome ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			rs = ps.executeQuery();
			while (rs.next()) {
				Cidade item = new Cidade();

				item.setId(rs.getInt("id"));
				item.setIdEstado(rs.getInt("id_estado"));
				item.setNome(rs.getString("nome"));

				listaRet.add(item);
			}

		} catch (Exception e) {
			logger.error("Erro ao consultar cidades: ", e);
			throw new SQLException("Erro ao consultar cidades: ", e);
		} finally {
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