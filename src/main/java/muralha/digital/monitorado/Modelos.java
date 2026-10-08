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

@XmlRootElement(name = "Modelos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Modelos {

    @XmlTransient
    private static Logger logger =
            LogManager.getLogger(Modelos.class);

    @XmlElementWrapper(name = "listaDeModelos")
    @XmlElement(name = "modelos")
    private List<ModeloEntidade> listaDeModelos;

    public List<ModeloEntidade> getListaModelos() {
        return listaDeModelos;
    }

    public void setListaModelos(List<ModeloEntidade> listaDeModelos) {
        this.listaDeModelos = listaDeModelos;
    }

    public Modelos() {
        super();
    }

    public static List<ModeloEntidade> ObterListaModelos()
            throws ConexaoException, SQLException {

        List<ModeloEntidade> listaRet =
                new ArrayList<ModeloEntidade>();

        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("SELECT id, descricao FROM ia.cad_modelo ORDER BY descricao");

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

                ModeloEntidade item =
                        new ModeloEntidade();

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
                    "Erro ao montar SQL (ObterListaModelos):: ",
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