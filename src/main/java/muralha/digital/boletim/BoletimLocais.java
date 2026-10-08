package muralha.digital.boletim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "BoletimLocais")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimLocais {
	@XmlTransient
	private static Logger logger = LogManager.getLogger(BoletimLocais.class);

	public BoletimLocais() {
		super();
	}

	public static Integer inserirBoletimLocal(BoletimLocal boletimLocal) throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer idGerado = null;

		try {
			sbSQL.append("INSERT INTO muralha.boletim_local ");
			sbSQL.append("(rua, numero, bairro, complemento, id_cidade) ");
			sbSQL.append("VALUES (?, ?, ?, ?, ?)");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

			// 1. Rua
			if (boletimLocal.getRua() != null && !boletimLocal.getRua().trim().isEmpty()) {
				ps.setString(1, boletimLocal.getRua());
			} else {
				ps.setNull(1, java.sql.Types.VARCHAR);
			}

			// 2. Número
			if (boletimLocal.getNumero() != null) {
				ps.setInt(2, boletimLocal.getNumero());
			} else {
				ps.setNull(2, java.sql.Types.VARCHAR);
			}

			// 3. Bairro
			if (boletimLocal.getBairro() != null && !boletimLocal.getBairro().trim().isEmpty()) {
				ps.setString(3, boletimLocal.getBairro());
			} else {
				ps.setNull(3, java.sql.Types.VARCHAR);
			}

			// 4. Complemento
			if (boletimLocal.getComplemento() != null && !boletimLocal.getComplemento().trim().isEmpty()) {
				ps.setString(4, boletimLocal.getComplemento());
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);
			}

			// 5. id_cidade
			if (boletimLocal.getIdCidade() != null) {
				ps.setInt(5, boletimLocal.getIdCidade());
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 1) {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					idGerado = rs.getInt(1); // ID auto-gerado
				}
			}

		} catch (Exception e) {
			String msgErro = "Erro ao inserir BoletimLocal!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar conexão com banco de dados!", e);
			}
		}

		return idGerado;
	}
}
