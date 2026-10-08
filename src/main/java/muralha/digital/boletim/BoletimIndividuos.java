package muralha.digital.boletim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "BoletimIndividuos")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimIndividuos {

	@XmlTransient
	private static final Logger logger = LogManager.getLogger(BoletimIndividuos.class);

	public BoletimIndividuos() {
		super();
	}

	public static boolean inserirBoletimIndividuo(BoletimIndividuo individuo) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			sbSQL.append("INSERT INTO muralha.boletim_individuo ");
			sbSQL.append("(id_boletim, id_tipo_envolvimento, detalhe_envolvimento, nome, cpf) ");
			sbSQL.append("VALUES (?, ?, ?, ?, ?)");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// 1. id_boletim
			if (individuo.getIdBoletim() != null) {
				ps.setInt(1, individuo.getIdBoletim());
			} else {
				ps.setNull(1, java.sql.Types.INTEGER);
			}

			// 2. id_tipo_envolvimento
			if (individuo.getIdTipoEnvolvimento() != null) {
				ps.setInt(2, individuo.getIdTipoEnvolvimento());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}

			// 3. detalhe_envolvimento
			if (individuo.getDetalheEnvolvimento() != null && !individuo.getDetalheEnvolvimento().trim().isEmpty()) {
				ps.setString(3, individuo.getDetalheEnvolvimento());
			} else {
				ps.setNull(3, java.sql.Types.VARCHAR);
			}

			// 4. nome
			if (individuo.getNome() != null && !individuo.getNome().trim().isEmpty()) {
				ps.setString(4, individuo.getNome());
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);
			}

			// 5. cpf
			if (individuo.getCpf() != null && !individuo.getCpf().trim().isEmpty()) {
				ps.setString(5, individuo.getCpf());
			} else {
				ps.setNull(5, java.sql.Types.VARCHAR);
			}

			int rowsAffected = ps.executeUpdate();
			return rowsAffected == 1;

		} catch (Exception e) {
			logger.error("Erro ao inserir BoletimIndividuo: " + e.getMessage(), e);
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados!", e);
			}
		}
	}

	public static boolean inserirListaBoletimIndividuo(List<BoletimIndividuo> lista)
			throws ConexaoException, SQLException {
		boolean sucessoTotal = true;

		for (BoletimIndividuo item : lista) {
			boolean sucesso = inserirBoletimIndividuo(item);
			if (!sucesso) {
				sucessoTotal = false;
				logger.warn("Falha ao inserir indivíduo: " + item);
			}
		}

		return sucessoTotal;
	}
}
