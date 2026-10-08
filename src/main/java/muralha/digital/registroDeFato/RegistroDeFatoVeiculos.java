package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@XmlRootElement(name = "RegistroDeFatoVeiculosLista")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoVeiculos {

    @XmlTransient
    private static Logger logger = LogManager.getLogger(RegistroDeFatoVeiculos.class);

    @XmlElementWrapper(name = "RegistroDeFatoVeiculosLista")
    @XmlElement(name = "RegistroDeFatoVeiculos")
    private List<RegistroDeFatoVeiculo> listaVeiculos;

    public RegistroDeFatoVeiculos() {}

    public List<RegistroDeFatoVeiculo> getListaRegistroVeiculo() {
        return listaVeiculos;
    }

    public void setListaRegistroVeiculo(List<RegistroDeFatoVeiculo> listaVeiculos) {
        this.listaVeiculos = listaVeiculos;
    }
    
    public static void CadastrarVeiculos(Long registroFatoId, List<VeiculoDTO> veiculos) throws ConexaoException, SQLException {

        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("insert into muralha.registro_fato_veiculo (id_registro_fato, placa, cor, marca, modelo) ");
        sbSQL.append("values (?,?,?,?,?)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());       
            
            for(VeiculoDTO veiculo : veiculos) {
            	ps.setLong(1, registroFatoId);
            	ps.setString(2, veiculo.getPlaca().toUpperCase());
            	ps.setString(3, veiculo.getCor().toUpperCase());
            	ps.setString(4, veiculo.getMarca().toUpperCase());
            	ps.setString(5, veiculo.getModelo());
            	
            	ps.addBatch();
            }
            
            ps.executeBatch();

        } catch (Exception e) {
            throw new SQLException("Erro ao cadastrar os veículos! ", e);
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
    
    public static void CadastrarVeiculo(RegistroDeFatoVeiculo veiculo) throws ConexaoException, SQLException {
        String sql = "INSERT INTO muralha.registro_fato_veiculo " +
                     "(id_registro_fato, placa, cor, marca, modelo) " +
                     "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            ps.setLong(1, veiculo.getIdRegistroFato());
            ps.setString(2, veiculo.getPlaca() != null ? veiculo.getPlaca().toUpperCase() : null);
            ps.setString(3, veiculo.getCor() != null ? veiculo.getCor().toUpperCase() : null);
            ps.setString(4, veiculo.getMarca() != null ? veiculo.getMarca().toUpperCase() : null);
            ps.setString(5, veiculo.getModelo());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new SQLException("Erro ao cadastrar o veículo!", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão/recursos", e);
            }
        }
    }
    
    public static void CadastrarObjetos(Long idRegistroFato, List<RegistroFatoObjeto> objetos) throws ConexaoException, SQLException {

        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("insert into muralha.registro_fato_objeto (id_registro_fato, tipo, descricao) ");
        sbSQL.append("values (?, ?, ?)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());       
            
            for(RegistroFatoObjeto objeto : objetos) {
            	ps.setLong(1, idRegistroFato);
            	ps.setString(2, objeto.getTipo());
            	ps.setString(3, objeto.getDescricao());
            	
            	ps.addBatch();
            }
            
            ps.executeBatch();

        } catch (Exception e) {
            throw new SQLException("Erro ao cadastrar os objetos! ", e);
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
    
    public static void RemoverVeiculo(String placa, Long idRegistroFato) throws SQLException, ConexaoException {

        String sqlVeiculo = "DELETE FROM muralha.registro_fato_veiculo WHERE placa = ? AND id_registro_fato = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlVeiculo)) {
            ps.setString(1, placa);
            ps.setLong(2, idRegistroFato);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao remover veículo: " + placa, e);
            throw e;
        }
    }
    
    public static void InativarVeiculoMonitorado(String placa, Long idRegistroFato) throws SQLException, ConexaoException {

        String sqlVeiculo = "UPDATE muralha.cad_veiculo_monitorado "
        		+ " set data_fim = GETDATE(), \r\n"
        		+ "	motivo_exclusao = 'Veículo excluído manualmente do registro de fato',\r\n"
        		+ "	data_exclusao = GETDATE(), data_atualizacao = GETDATE()\r\n"
        		+ "	where id_registro_fato = ? and placa = ?";
        
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sqlVeiculo)) {
            ps.setLong(1, idRegistroFato);
            ps.setString(2, placa);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Erro ao remover veículo: " + placa, e);
            throw e;
        }
    }
}
