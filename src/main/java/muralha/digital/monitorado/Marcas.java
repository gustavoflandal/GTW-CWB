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

@XmlRootElement(name = "Marcas")
@XmlAccessorType(XmlAccessType.FIELD)
public class Marcas {

    @XmlTransient
    private static Logger logger = LogManager.getLogger(Marcas.class);

    @XmlElementWrapper(name = "listaDeMarcas")
    @XmlElement(name = "marcas")
    private List<MarcaEntidade> listaDeMarcas;

    public List<MarcaEntidade> getListaMarcas() {
        return listaDeMarcas;
    }

    public void setListaMarcas(List<MarcaEntidade> listaDeMarcas) {
        this.listaDeMarcas = listaDeMarcas;
    }

    public Marcas() {
        super();
    }

    public static List<MarcaEntidade> ObterListaMarcas()
            throws ConexaoException, SQLException {

        List<MarcaEntidade> listaRet =
                new ArrayList<MarcaEntidade>();

        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("SELECT id, descricao FROM ia.cad_marca ORDER BY descricao");

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

                MarcaEntidade item =
                        new MarcaEntidade();

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
                    "Erro ao montar SQL (ObterListaMarcas):: ",
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