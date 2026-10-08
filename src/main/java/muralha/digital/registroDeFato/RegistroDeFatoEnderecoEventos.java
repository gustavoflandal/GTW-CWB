package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "RegistroDeFatoEnderecoEventos")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoEnderecoEventos {

    @XmlTransient
    private static Logger logger = LogManager.getLogger(RegistroDeFatoEnderecoEventos.class);

    @XmlElementWrapper(name = "RegistroDeFatoEnderecoEventoLista")
    @XmlElement(name = "RegistroDeFatoEnderecoEvento")
    private List<RegistroDeFatoEnderecoEvento> listaEnderecoEvento;

    public RegistroDeFatoEnderecoEventos() {}

    public List<RegistroDeFatoEnderecoEvento> getListaEnderecoEvento() {
        return listaEnderecoEvento;
    }

    public void setListaEnderecoEvento(List<RegistroDeFatoEnderecoEvento> listaEnderecoEvento) {
        this.listaEnderecoEvento = listaEnderecoEvento;
    }

    public static List<RegistroDeFatoEnderecoEvento> ObterListaEnderecosEvento() throws ConexaoException, SQLException {
        List<RegistroDeFatoEnderecoEvento> listaRet = new ArrayList<>();

        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("SELECT id, descricao ");
        sbSQL.append("FROM muralha.registro_fato_endereco_evento ");
        sbSQL.append("ORDER BY descricao");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                RegistroDeFatoEnderecoEvento item = new RegistroDeFatoEnderecoEvento();
                item.setId(rs.getInt("id"));
                item.setDescricao(rs.getString("descricao"));

                listaRet.add(item);
            }

        } catch (Exception e) {
            throw new SQLException("Erro ao consultar tipos de endereços de evento de Registro de Fato: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("ERRO ao fechar conexão/recursos", e);
            }
        }

        return listaRet;
    }
    
    public static void CadastrarEndereco(Long registroFatoId, Localizacao endereco) throws ConexaoException, SQLException {

        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("insert into muralha.registro_fato_endereco (id_registro_fato, id_tipo_evento, id_cidade, cep, bairro, rua, numero, complemento, latitude, longitude) ");
        sbSQL.append("values (?,?,?,?,?,?,?,?,?,?)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            
            ps.setLong(1, registroFatoId);
            ps.setInt(2, endereco.getTipoEnderecoEvento());
            ps.setInt(3, endereco.getCidadeId());
            ps.setString(4, endereco.getCep());
            ps.setString(5, endereco.getBairro());
            ps.setString(6, endereco.getRua());
            ps.setString(7, endereco.getNumero());
            ps.setString(8, endereco.getComplemento());
            ps.setDouble(9, endereco.getLatitude());
            ps.setDouble(10, endereco.getLongitude());
            
            ps.executeUpdate(); 

        } catch (Exception e) {
            throw new SQLException("Erro ao cadastrar o endereço relacionado ao fato ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("ERRO ao fechar conexão/recursos", e);
            }
        }
    }
}
