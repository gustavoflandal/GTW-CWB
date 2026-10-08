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

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement		(name="OcorrenciasLigacao") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class OcorrenciasLigacao {
	
	@XmlElementWrapper	(name = "listaOcorrenciasLigacao")
	@XmlElement			(name = "ocorrenciasLigacao")	
	private List<OcorrenciaLigacao> listaOcorrenciasLigacao;	
	
	public List<OcorrenciaLigacao> getListaOcorrenciaLigacao() {
		return listaOcorrenciasLigacao;
	}

	public void ListaOcorrenciaLigacao(List<OcorrenciaLigacao> listaOcorrenciasLigacao) {
		this.listaOcorrenciasLigacao = listaOcorrenciasLigacao;
	}

	public OcorrenciasLigacao()
	{
		super();
	}
	
	public static List<OcorrenciaLigacao> ObterOcorrenciasLigacao() throws ConexaoException, SQLException {
		List<OcorrenciaLigacao> listaRet = new ArrayList<OcorrenciaLigacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("select * from muralha.ocorrencia_ligacao");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());		
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				OcorrenciaLigacao item = new OcorrenciaLigacao();

				item.setId(rs.getInt("id"));
				item.setDataHoraEvento(rs.getDate("data_hora_evento"));
				item.setIdTipoSolicitante(rs.getInt("id_tipo_solicitante"));
				item.setNomeSolicitante(rs.getString("nome_solicitante"));
				item.setCpfSolicitante(rs.getString("cpf_solicitante"));
				item.setIdTipoOcorrencia(rs.getInt("id_tipo_ocorrencia"));
				item.setIdCidade(rs.getInt("id_cidade"));
				item.setBairro(rs.getString("bairro"));
				item.setRua(rs.getString("rua"));
				item.setNumero(rs.getInt("numero"));
				item.setComplemento(rs.getString("complemento"));
				item.setNomeVitima(rs.getString("nome_vitima"));
				item.setDetalhamento(rs.getString("detalhamento"));
				item.setExisteArmaEnvolvida(rs.getInt("existe_arma_envolvida"));
				item.setData(rs.getDate("data"));
				item.setIdUsuario(rs.getInt("id_usuario"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterOcorrenciasLigacao):: ", e);
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
	
	public static Boolean CadastrarOcorrenciaLigacao(OcorrenciaLigacao ocorrencia) throws ConexaoException, SQLException {
	    StringBuilder sbSQL = new StringBuilder();
	    sbSQL.append("INSERT INTO muralha.ocorrencia_ligacao (")
	         .append("data_hora_evento, id_tipo_solicitante, nome_solicitante, cpf_solicitante, ")
	         .append("id_tipo_ocorrencia, id_cidade, bairro, rua, numero, complemento, ")
	         .append("nome_vitima, detalhamento, existe_arma_envolvida, data, id_usuario) ")
	         .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)");

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sbSQL.toString())) {

	        ps.setTimestamp(1, new java.sql.Timestamp(ocorrencia.getDataHoraEvento().getTime()));
	        ps.setInt(2, ocorrencia.getIdTipoSolicitante());
	        ps.setString(3, ocorrencia.getNomeSolicitante());
	        ps.setString(4, ocorrencia.getCpfSolicitante());
	        ps.setInt(5, ocorrencia.getIdTipoOcorrencia());
	        ps.setInt(6, ocorrencia.getIdCidade());
	        ps.setString(7, ocorrencia.getBairro());
	        ps.setString(8, ocorrencia.getRua());
	        ps.setInt(9, ocorrencia.getNumero());
	        ps.setString(10, ocorrencia.getComplemento());
	        ps.setString(11, ocorrencia.getNomeVitima());
	        ps.setString(12, ocorrencia.getDetalhamento());
	        ps.setInt(13, ocorrencia.getExisteArmaEnvolvida());
	        ps.setInt(14, ocorrencia.getIdUsuario());

	        ps.executeUpdate();
	        
	        return true;	
	        
	    } catch (SQLException e) {
	        throw new SQLException("Erro ao cadastrar ocorrência de ligação.", e);
	    }
	}
}
