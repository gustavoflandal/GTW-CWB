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

@XmlRootElement(name = "ClassesVeiculo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassesVeiculo {

    @XmlTransient
    private static Logger logger =
            LogManager.getLogger(ClassesVeiculo.class);

    @XmlElementWrapper(name = "listaDeClassesVeiculo")
    @XmlElement(name = "classesVeiculo")
    private List<ClasseVeiculoEntidade> listaDeClassesVeiculo;

    public List<ClasseVeiculoEntidade> getListaClassesVeiculo() {
        return listaDeClassesVeiculo;
    }

    public void setListaClassesVeiculo(
            List<ClasseVeiculoEntidade> listaDeClassesVeiculo) {

        this.listaDeClassesVeiculo = listaDeClassesVeiculo;
    }

    public ClassesVeiculo() {
        super();
    }

    public static List<ClasseVeiculoEntidade> ObterListaClassesVeiculo()
            throws ConexaoException, SQLException {

        List<ClasseVeiculoEntidade> listaRet =
                new ArrayList<ClasseVeiculoEntidade>();

        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append(
            "SELECT id_classe, descricao, id_classe_git, id_classe_tr " +
            "FROM dbo.classe_veiculo " +
            "ORDER BY descricao"
        );

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

                ClasseVeiculoEntidade item =
                        new ClasseVeiculoEntidade();

                item.setIdClasse(
                        rs.getString("id_classe")
                );

                item.setDescricao(
                        rs.getString("descricao")
                );

                Object idClasseGit = rs.getObject("id_classe_git");

                if (idClasseGit != null) {
                	item.setIdClasseGit(((Number) idClasseGit).intValue());
                } else {
                	item.setIdClasseGit(null);
                }

                item.setIdClasseTr(
                        rs.getString("id_classe_tr")
                );

                listaRet.add(item);
            }

        } catch (Exception e) {

            throw new SQLException(
                    "Erro ao montar SQL (ObterListaClassesVeiculo):: ",
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