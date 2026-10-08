package muralha.digital.consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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


@XmlRootElement		(name="LinhasTempo") 
@XmlAccessorType	(XmlAccessType.FIELD)
public class LinhasTempo
{	
	@XmlTransient
	private static Logger logger = LogManager.getLogger(LinhasTempo.class);
	
	@XmlElementWrapper	(name = "ListaLinhasTempo")
	@XmlElement			(name = "LinhaTempo")	
	private List<LinhaTempo> listaLinhasTempo;	
	

	public List<LinhaTempo> getListaLinhasTempo() {
		return listaLinhasTempo;
	}


	public void setListaLinhasTempo(List<LinhaTempo> listaStatusLinhasTempo) {
		this.listaLinhasTempo = listaStatusLinhasTempo;
	}


	public LinhasTempo()
	{
		super();
	}
	
	public static List<LinhaTempo> ObterListaLinhaTempo(UUID idAlerta, UUID idVeiculo) throws ConexaoException, SQLException 
	{
		List<LinhaTempo> listaRet = new ArrayList<LinhaTempo>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" DECLARE @idAlerta UNIQUEIDENTIFIER = ?, @idVeiculo UNIQUEIDENTIFIER = ? ");
		sbSQL.append(" EXEC muralha.spu_ObterDadosLinhaTempo @idAlerta, @idVeiculo ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			if (idAlerta == null)
				ps.setNull(1, Types.OTHER);
			else
				ps.setString(1, idAlerta.toString());
			
			if (idVeiculo == null)
				ps.setNull(2, Types.OTHER);
			else
				ps.setString(2, idVeiculo.toString());
			
			rs = ps.executeQuery();
			while (rs.next()) 
			{
				LinhaTempo item = new LinhaTempo();

				item.setPasso(rs.getInt("passo"));
				item.setNomePasso(rs.getString("nome_passo"));
				item.setData(rs.getTimestamp("data"));
				item.setTempo(rs.getString("tempo"));
				
				item.setDataFormatada(item.getDataFormatada());
				
				listaRet.add(item);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL (ObterListaLinhaTempo):: ", e);
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
