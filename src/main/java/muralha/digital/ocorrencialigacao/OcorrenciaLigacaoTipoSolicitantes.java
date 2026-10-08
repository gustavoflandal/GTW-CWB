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

@XmlRootElement		(name="OcorrenciaLigacaoTipoSolicitantes") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class OcorrenciaLigacaoTipoSolicitantes {
	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(OcorrenciaLigacaoTipoSolicitantes.class);
	
	@XmlElementWrapper	(name = "listaDeSolicitantes")
	@XmlElement			(name = "solicitantes")	
	private List<OcorrenciaLigacaoTipoSolicitante> listaSolicitantes;	
	

	public List<OcorrenciaLigacaoTipoSolicitante> getListaSolicitantes() {
		return listaSolicitantes;
	}


	public void setListaSolicitantes(List<OcorrenciaLigacaoTipoSolicitante> listaSolicitantes) {
		this.listaSolicitantes = listaSolicitantes;
	}

	public OcorrenciaLigacaoTipoSolicitantes()
	{
		super();
	}
	
	public static List<OcorrenciaLigacaoTipoSolicitante> ObterListaSolicitantes() throws ConexaoException, SQLException {
		List<OcorrenciaLigacaoTipoSolicitante> listaRet = new ArrayList<OcorrenciaLigacaoTipoSolicitante>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("select * from muralha.ocorrencia_ligacao_tipo_solicitante");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				OcorrenciaLigacaoTipoSolicitante item = new OcorrenciaLigacaoTipoSolicitante();

				item.setId(rs.getInt("id"));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaSolicitantes):: ", e);
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
