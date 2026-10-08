package muralha.digital.boletim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "BoletimApreensoes")
@XmlAccessorType(XmlAccessType.FIELD)
public class BoletimApreensoes {

    @XmlTransient
    private static final Logger logger = LogManager.getLogger(BoletimApreensoes.class);

    public BoletimApreensoes() {
        super();
    }

    public static boolean inserirBoletimApreensao(BoletimApreensao apreensao) throws ConexaoException, SQLException {
        StringBuilder sbSQL = new StringBuilder();

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            sbSQL.append("INSERT INTO muralha.boletim_apreensao ");
            sbSQL.append("(id_boletim, tipo, descricao) ");
            sbSQL.append("VALUES (?, ?, ?)");

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            // 1. id_boletim
            if (apreensao.getIdBoletim() != null) {
                ps.setInt(1, apreensao.getIdBoletim());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            // 2. tipo
            if (apreensao.getTipo() != null && !apreensao.getTipo().trim().isEmpty()) {
                ps.setString(2, apreensao.getTipo());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            // 3. descricao
            if (apreensao.getDescricao() != null && !apreensao.getDescricao().trim().isEmpty()) {
                ps.setString(3, apreensao.getDescricao());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;

        } catch (Exception e) {
            String msgErro = "Erro ao inserir BoletimApreensao!";
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

    public static boolean inserirListaBoletimApreensao(List<BoletimApreensao> lista) throws ConexaoException, SQLException {
        boolean sucessoTotal = true;

        for (BoletimApreensao item : lista) {
            boolean sucesso = inserirBoletimApreensao(item);
            if (!sucesso) {
                sucessoTotal = false;
                logger.warn("Falha ao inserir item de apreensão: " + item);
            }
        }

        return sucessoTotal;
    }
}
