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

@XmlRootElement(name = "RegistroDeFatoIndividuos")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoIndividuos {
	
    @XmlTransient
    private static Logger logger = LogManager.getLogger(RegistroDeFatoIndividuos.class);

    @XmlElementWrapper(name = "RegistroDeFatoIndividuosLista")
    @XmlElement(name = "RegistroDeFatoIndividuos")
    private List<RegistroDeFatoIndividuo> listaIndividuos;

    public RegistroDeFatoIndividuos() {}

    public List<RegistroDeFatoIndividuo> getListaRegistroIndividuos() {
        return listaIndividuos;
    }

    public void setListaRegistroIndividuos(List<RegistroDeFatoIndividuo> listaIndividuos) {
        this.listaIndividuos = listaIndividuos;
    }
    
    public static void CadastrarIndividuos(Long registroFatoId, List<EnvolvidoDTO> individuos) throws ConexaoException, SQLException {

        StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("insert into muralha.registro_fato_individuo ");
        sbSQL.append("(id_registro_fato, id_tipo_envolvimento, detalhe_envolvimento, nome, cpf, ddd, telefone, email) ");
        sbSQL.append("values (?,?,?,?,?,?,?,?)");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());       
            
            for(EnvolvidoDTO individuo : individuos) {
            	
            	ps.setLong(1, registroFatoId);
            	ps.setInt(2, individuo.getTipoEnvolvimento());
            	ps.setString(3, individuo.getDetalhamento());
            	ps.setString(4, individuo.getNome());
            	ps.setString(5, individuo.getCpf());
            	ps.setString(6, individuo.getDdd());
            	ps.setString(7, individuo.getTelefone());
            	ps.setString(8, individuo.getEmail());
            	
            	ps.addBatch();
            }
            
            ps.executeBatch();

        } catch (Exception e) {
            throw new SQLException("Erro ao cadastrar os envolvidos! ", e);
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
    
    public static boolean inserirIndividuo(RegistroDeFatoIndividuo individuo) throws SQLException, ConexaoException {
	    String sql = "INSERT INTO muralha.registro_fato_individuo " +
	        "(id_registro_fato, id_tipo_envolvimento, detalhe_envolvimento, nome, cpf, ddd, telefone, email) " +
	        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, individuo.getIdRegistroFato());
	        ps.setInt(2, individuo.getIdTipoEnvolvimento());
	        ps.setString(3, individuo.getDetalheEnvolvimento());
	        ps.setString(4, individuo.getNome());
	        ps.setString(5, individuo.getCpf());

	        if (individuo.getDdd() != null) {
	            ps.setInt(6, individuo.getDdd());
	        } else {
	            ps.setNull(6, java.sql.Types.INTEGER);
	        }

	        if (individuo.getTelefone() != null) {
	            ps.setString(7, individuo.getTelefone());
	        } else {
	            ps.setNull(7, java.sql.Types.INTEGER);
	        }

	        ps.setString(8, individuo.getEmail());

	        return ps.executeUpdate() == 1;
	    }
	}
    
    public static boolean removerIndividuo(long idIndividuo) throws SQLException, ConexaoException {
        String sql = "DELETE FROM muralha.registro_fato_individuo WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idIndividuo);

            return ps.executeUpdate() == 1;
        }
    }
}
