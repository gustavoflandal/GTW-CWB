package muralha.digital.guarnicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
@XmlRootElement(name = "guarnicoes")
public class Guarnicoes {

	private List<Guarnicao> guarnicoes;
	@XmlElementWrapper(name = "listaGuarnicoes")
    @XmlElement(name = "guarnicao")
	public List<Guarnicao> getGuarnicoes() {
	return guarnicoes;
	}
	public void setGuarnicoes(List<Guarnicao> guarnicoes) {
	this.guarnicoes = guarnicoes;
	}
	// incluindo no commit

	private static final Logger logger = Logger.getLogger(Guarnicoes.class);

	/**
	 * Lista todas as guarnições cadastradas no sistema.
	 * @return Lista de objetos Guarnicao
	 */
	public static List<Guarnicao> listarTodas() {
	    List<Guarnicao> lista = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        StringBuilder sb = new StringBuilder();
	        sb.append("SELECT ");
	        sb.append("  g.id, ");
	        sb.append("  g.nome, ");
	        sb.append("  g.id_usuario_responsavel, ");
	        sb.append("  resp.nome AS responsavel, ");
	        sb.append("  g.data_criacao, ");
	        sb.append("  g.id_usuario_criacao, ");
	        sb.append("  g.data_ult_alt, ");
	        sb.append("  g.id_usuario_alt, ");
	        sb.append("  g.ativo ");
	        sb.append("FROM muralha.guarnicao g ");
	        sb.append("INNER JOIN sis_usuario resp ON resp.id_usuario = g.id_usuario_responsavel ");
	        sb.append("ORDER BY g.data_criacao DESC, g.nome ASC");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sb.toString());
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            Guarnicao g = new Guarnicao();
	            g.setId(rs.getInt("id"));
	            g.setNome(rs.getString("nome"));
	            g.setId_usuario_responsavel(rs.getInt("id_usuario_responsavel"));
	            g.setResponsavel(rs.getString("responsavel")); 
	            g.setData_criacao(rs.getDate("data_criacao"));
	            g.setId_usuario_criacao(rs.getInt("id_usuario_criacao"));
	            g.setData_ult_alt(rs.getDate("data_ult_alt"));
	            g.setId_usuario_alt(rs.getInt("id_usuario_alt"));
	            g.setAtivo(rs.getInt("ativo"));

	            lista.add(g);
	        }

	        logger.info("Total de guarnições carregadas: " + lista.size());
	    } catch (Exception e) {
	        logger.error("Erro ao listar guarnições!", e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos (listarTodas): " + e.getMessage(), e);
	        }
	    }
	    return lista;
	}
}
