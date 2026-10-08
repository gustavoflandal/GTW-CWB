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

@XmlRootElement(name = "BoletimVeiculos")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimVeiculos {

    @XmlTransient
    private static final Logger logger = LogManager.getLogger(BoletimVeiculos.class);

    public BoletimVeiculos() {
        super();
    }

    public static boolean inserirBoletimVeiculo(BoletimVeiculo veiculo) throws ConexaoException, SQLException {
        StringBuilder sbSQL = new StringBuilder();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            sbSQL.append("INSERT INTO muralha.boletim_veiculo ");
            sbSQL.append("(id_boletim, placa, cor, marca, modelo) ");
            sbSQL.append("VALUES (?, ?, ?, ?, ?)");

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            // 1. id_boletim
            if (veiculo.getIdBoletim() != null) {
                ps.setInt(1, veiculo.getIdBoletim());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            // 2. placa
            if (veiculo.getPlaca() != null && !veiculo.getPlaca().trim().isEmpty()) {
                ps.setString(2, veiculo.getPlaca());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            // 3. cor
            if (veiculo.getCor() != null && !veiculo.getCor().trim().isEmpty()) {
                ps.setString(3, veiculo.getCor());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }

            // 4. marca
            if (veiculo.getMarca() != null && !veiculo.getMarca().trim().isEmpty()) {
                ps.setString(4, veiculo.getMarca());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            // 5. modelo
            if (veiculo.getModelo() != null && !veiculo.getModelo().trim().isEmpty()) {
                ps.setString(5, veiculo.getModelo());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;

        } catch (Exception e) {
            String msgErro = "Erro ao inserir BoletimVeiculo!";
            logger.error(msgErro + ": " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar conexão com banco de dados!", e);
            }
        }
    }

    public static boolean inserirListaBoletimVeiculo(List<BoletimVeiculo> lista) throws ConexaoException, SQLException {
        boolean sucessoTotal = true;

        for (BoletimVeiculo item : lista) {
            boolean sucesso = inserirBoletimVeiculo(item);
            if (!sucesso) {
                sucessoTotal = false;
                logger.warn("Falha ao inserir veículo: " + item);
            }
        }

        return sucessoTotal;
    }
}
