package muralha.digital.atendimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Historicos  extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@XmlTransient
	private static final Logger logger = Logger.getLogger(Historicos.class);
	
	@XmlElementWrapper(name = "listaHistoricos")
	@XmlElement(name = "historico")

	private List<Historico> historicos;
	
	public List<Historico> getHistoricos() {
		return historicos;
	}
	public void setHitoricos(List<Historico> historys) {
		this.historicos = historys;
	}
	
	
	
	public static List<Historico> ObterHistorico(int idAtendimento) {
		List<Historico> historico = new ArrayList<Historico>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		try {
            sbSQL.append(" SELECT ah.id_atendimento, aht.descricao, ah.evento, ah.data, su.nome 	");
            sbSQL.append(" FROM muralha.atendimento_historico ah			 						");
            sbSQL.append(" JOIN muralha.atendimento_historico_tipo aht		 						");
            sbSQL.append(" ON ah.id_tipo_historico = aht.id					 						");
            sbSQL.append(" JOIN sis_usuario su								 						");
            sbSQL.append(" ON su.id_usuario = ah.id_usuario					 						"); 
            sbSQL.append(" WHERE ah.id_atendimento = ?						 						");
			
			

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idAtendimento);
			rs = ps.executeQuery();

			while (rs.next()) {
				Historico item = new Historico();
				
				item.setId(rs.getInt("id_atendimento"));
				item.setTipoHistorico(rs.getString("descricao"));
				item.setEvento(rs.getString("evento"));
				item.setData(rs.getTimestamp("data"));
				item.setDataFormatada(item.getDataFormatada());	
				item.setUsuario(rs.getString("nome"));
				
				historico.add(item);
			}
		} catch (Exception e) {
			logger.error("Erro ao obter novas ocorrencias: " + e.getMessage(), e);
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
			}
		}
		return historico;
	}	
}
