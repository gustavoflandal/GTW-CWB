package muralha.digital.notificacao;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;


@XmlRootElement		(name="TiposNotificacao") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class TiposNotificacao
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(TiposNotificacao.class);
	
	@XmlElementWrapper	(name = "ListaTiposNotificacao")
	@XmlElement			(name = "TipoNotificacao")	
	private List<TipoNotificacao> listaTiposNotificacao;	
	

	public List<TipoNotificacao> getListaTiposNotificacao() {
		return listaTiposNotificacao;
	}

	public void setListaTiposNotificacao(List<TipoNotificacao> listaTiposNotificacao) {
		this.listaTiposNotificacao = listaTiposNotificacao;
	}


	public TiposNotificacao()
	{
		super();
	}
	
	public static List<TipoNotificacao> ObterListaTiposNotificacao() throws ConexaoException, SQLException 
	{
		
		List<TipoNotificacao> listaRet = new ArrayList<TipoNotificacao>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT id, ");
		sbSQL.append(" 		  RTRIM(descricao) AS descricao ");
		sbSQL.append(" FROM   muralha.tipo_notificacao ");
		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 		  descricao ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				TipoNotificacao item = new TipoNotificacao();

				item.setId(UUID.fromString(rs.getString("id")));
				item.setDescricao(rs.getString("descricao"));
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaTiposNotificacao):: ", e);
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
