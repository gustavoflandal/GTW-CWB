package muralha.digital.alerta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class AnotacoesContributivas 
{
	@XmlTransient
	private static final Logger logger = Logger.getLogger(AnotacoesContributivas.class);
	
	@XmlElementWrapper		(name="ListaAnotacoesContributivas")
	@XmlElement				(name="AnotacaoContributiva")
	private List<AnotacaoContributiva> anotacoesContributivas;
	
	public List<AnotacaoContributiva> getAnotacoesContributivas() {
		return anotacoesContributivas;
	}
	public void setAnotacoesContributivas(List<AnotacaoContributiva> anotacoesContributivas) {
		this.anotacoesContributivas = anotacoesContributivas;
	}
	
	public AnotacoesContributivas() {}
	
	
	/*
	 * Retornar dados do alerta para ID solicitado.
	 */
	public static List<AnotacaoContributiva> obterAnotacoesContributivasPorIdAlerta(UUID idAlerta) 
	{
		List<AnotacaoContributiva> listaAlerta = new ArrayList<AnotacaoContributiva>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuilder sbSQL = new StringBuilder();
		
		try {
			
			sbSQL.append(" SELECT ac.id, " );
			sbSQL.append(" 		  ac.id_alerta, ");
			sbSQL.append(" 		  ac.descricao, ");
			sbSQL.append(" 		  ac.data_cadastro, ");
			sbSQL.append(" 		  su.id_usuario, ");
			sbSQL.append(" 		  RTRIM(su.usuario) AS usuario, ");
			sbSQL.append(" 		  RTRIM(su.nome) AS nome_usuario ");
			sbSQL.append(" FROM   muralha.alerta a (NOLOCK) ");
			sbSQL.append(" 		  INNER JOIN muralha.anotacao_contributiva ac (NOLOCK) ");
			sbSQL.append(" 		  		ON  ac.id_alerta = a.id ");
			sbSQL.append(" 		  INNER JOIN sis_usuario su ");
			sbSQL.append(" 		 	 	ON  su.id_usuario = ac.id_usuario ");			
			sbSQL.append(" WHERE  a.enviado_cliente = 1 ");
			sbSQL.append(" 		  AND a.id = ? ");
			sbSQL.append(" ORDER BY ");
			sbSQL.append(" 		  ac.data_cadastro ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());

			rs = ps.executeQuery();
			
			while (rs.next()) 
			{
				AnotacaoContributiva anotacaoContributiva = new AnotacaoContributiva();
				
				anotacaoContributiva.setId(UUID.fromString(rs.getString("id")));
				anotacaoContributiva.setIdAlerta(UUID.fromString(rs.getString("id_alerta")));
				anotacaoContributiva.setDescricao(rs.getString("descricao"));
				anotacaoContributiva.setDataCadastro(rs.getTimestamp("data_cadastro"));
				anotacaoContributiva.setIdUsuario(rs.getInt("id_usuario"));
				anotacaoContributiva.setUsuario(rs.getString("usuario"));
				anotacaoContributiva.setNomeUsuario(rs.getString("nome_usuario"));
				
				anotacaoContributiva.setDataCadastroFormatada(anotacaoContributiva.getDataCadastroFormatada());
				anotacaoContributiva.setHoraCadastroFormatada(anotacaoContributiva.getHoraCadastroFormatada());
				
				listaAlerta.add(anotacaoContributiva);
			}
		}
		catch(Exception e) {
			logger.error("Erro ao obter Anotações Contributivas: " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		
		}	
		
		return listaAlerta;	
	}
	
	public static boolean InserirAnotacaoContributiva(UUID idAlerta, String strAnotacaoAlertaCad, Integer idUsuario) throws ConexaoException, SQLException 
	{
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		boolean retorno = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" INSERT INTO muralha.anotacao_contributiva (id_alerta, descricao, id_usuario) ");
			sbSQL.append(" VALUES (?, ?, ?) ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setString(1, idAlerta.toString());
			ps.setString(2, strAnotacaoAlertaCad);
			ps.setInt(3, idUsuario);
			
			retorno = (ps.executeUpdate() == 1);
				
		}
		catch(Exception e) {
			msgErro = "Erro ao cadastrar anotação contributiva!";
			logger.error(msgErro + ": " + e.getMessage(), e);
		}
		finally {
			
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e) {logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);}
		}
		
		return retorno;
	}
}
