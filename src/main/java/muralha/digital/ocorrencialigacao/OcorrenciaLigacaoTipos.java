package muralha.digital.ocorrencialigacao;

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

@XmlRootElement		(name="OcorrenciaLigacaoTipos") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class OcorrenciaLigacaoTipos {
	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(OcorrenciaLigacaoTipos.class);
	
	@XmlElementWrapper	(name = "listaTiposOCorrencias")
	@XmlElement			(name = "tiposOcorrencias")	
	private List<OcorrenciaLigacaoTipo> listaTiposOCorrencias;	
	
	public List<OcorrenciaLigacaoTipo> getListaTiposOCorrencias() {
		return listaTiposOCorrencias;
	}


	public void setListaTiposOCorrencias(List<OcorrenciaLigacaoTipo> listaTiposOCorrencias) {
		this.listaTiposOCorrencias = listaTiposOCorrencias;
	}

	public OcorrenciaLigacaoTipos()
	{
		super();
	}
	
	public static List<OcorrenciaLigacaoTipo> ObterTiposOcorrencias() throws ConexaoException, SQLException {
		List<OcorrenciaLigacaoTipo> listaRet = new ArrayList<OcorrenciaLigacaoTipo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("select * from muralha.ocorrencia_tipo");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				OcorrenciaLigacaoTipo item = new OcorrenciaLigacaoTipo();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterTiposOcorrencias):: ", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return listaRet;
	}
}
