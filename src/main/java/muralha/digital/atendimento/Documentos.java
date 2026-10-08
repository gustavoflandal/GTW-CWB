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
public class Documentos extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@XmlTransient
	private static final Logger logger = Logger.getLogger(Atendimentos.class);

	@XmlElementWrapper(name = "listaDocumentos")
	@XmlElement(name = "documento")
	private List<Documento> documentos;

	public List<Documento> getDocumentos() {
		return documentos;
	}
	public void setDocumentos(List<Documento> docs) {
		this.documentos = docs;
	}
	
	
	public static List<Documento> ObterDocumentos(int idAtendimento){
		List<Documento> docs = new ArrayList<Documento>();		

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		StringBuilder sbSQL = new StringBuilder();

		
		try {
            sbSQL.append(" SELECT * FROM					");
            sbSQL.append(" muralha.atendimento_documento	");          
            sbSQL.append(" WHERE id_atendimento = ?			");
           

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			 ps.setInt(1, idAtendimento);
			rs = ps.executeQuery();

			while (rs.next()) {
				Documento item = new Documento();				
				
				item.setId(rs.getInt("id"));
				item.setIdAtendimento(rs.getInt("id_atendimento"));
				item.setTipo(rs.getString("tipo"));
				item.setDetalhamento(rs.getString("detalhamento"));
				item.setDirArquivo(rs.getString("dir_arquivo"));			
				
				docs.add(item);			
		
			}
		} catch (Exception e) {
			logger.error("Erro ao obter ObterDocumentos(): " + e.getMessage(), e);
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
		return docs;
		
	}

}
