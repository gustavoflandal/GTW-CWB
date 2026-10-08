package muralha.digital.atendimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Documento {
	
	private static final Logger logger = Logger.getLogger(Documento.class);
	
	private int 		id;
	private int 		idAtendimento;
	private String 		tipo;
	private String		detalhamento;
	private String 		dirArquivo;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdAtendimento() {
		return idAtendimento;
	}
	public void setIdAtendimento(int idAtendimento) {
		this.idAtendimento = idAtendimento;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getDetalhamento() {
		return detalhamento;
	}
	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}
	public String getDirArquivo() {
		return dirArquivo;
	}
	public void setDirArquivo(String dirArquivo) {
		this.dirArquivo = dirArquivo;
	}
	
	public static Documento obterPorId(int idDocumento) {
	    Documento doc = null;

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    String sql = "SELECT * FROM muralha.atendimento_documento WHERE id = ?";

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sql);
	        ps.setInt(1, idDocumento);
	        rs = ps.executeQuery();

	        if (rs.next()) {
	            doc = new Documento();
	            doc.setId(rs.getInt("id"));
	            doc.setIdAtendimento(rs.getInt("id_atendimento"));
	            doc.setTipo(rs.getString("tipo"));
	            doc.setDetalhamento(rs.getString("detalhamento"));
	            doc.setDirArquivo(rs.getString("dir_arquivo"));
	        }
	    } catch (Exception e) {
	        logger.error("Erro ao obter documento por id: " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }

	    return doc;
	}


}
