package muralha.digital.monitorado;

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

@XmlRootElement(name = "Cores")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cores {

    @XmlTransient
    private static Logger logger = LogManager.getLogger(Cores.class);

    @XmlElementWrapper(name = "listaDeCores")
    @XmlElement(name = "cores")
    private List<CorEntidade> listaDeCores;

    public List<CorEntidade> getListaCores() {
        return listaDeCores;
    }

    public void setListaCores(List<CorEntidade> listaDeCores) {
        this.listaDeCores = listaDeCores;
    }

    public Cores() {
        super();
    }

    public static List<CorEntidade> ObterListaCores()
            throws ConexaoException, SQLException {

        List<CorEntidade> listaRet =
                new ArrayList<CorEntidade>();

        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("SELECT id, descricao FROM ia.cad_cor ORDER BY descricao");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            conn = Conexao.getConexao();

            ps = conn.prepareStatement(
                    sbSQL.toString()
            );

            rs = ps.executeQuery();

            while (rs.next()) {

                CorEntidade item =
                        new CorEntidade();

                item.setId(
                        rs.getInt("id")
                );

                item.setDescricao(
                        rs.getString("descricao")
                );

                listaRet.add(item);
            }

        } catch (Exception e) {

            throw new SQLException(
                    "Erro ao montar SQL (ObterListaCores):: ",
                    e
            );

        } finally {

            try {

                if (rs != null)
                    rs.close();

                if (ps != null)
                    ps.close();

                if (conn != null)
                    conn.close();

            } catch (SQLException e) {

                throw new ConexaoException(
                        "ERRO de SQL",
                        e
                );
            }
        }

        return listaRet;
    }
}