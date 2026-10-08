package muralha.digital.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Dashboards
{	
	private static Logger logger = LogManager.getLogger(Dashboards.class);
	
	public Dashboards()
	{
		super();
	}
	
	public static TotalizadorCategoria ObterTotalizadorCategoria(Date dataIni, Date dataFim, Integer idLocal, Integer tipoRelatorio, Integer municipio, Integer regiao) throws ConexaoException, SQLException 
	{
		TotalizadorCategoria totalizadorCategoria = new TotalizadorCategoria();
		StringBuilder sbSQL = new StringBuilder();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean erro = false;
		String msgErro = "";
		
		try {
		
			sbSQL.append(" EXEC muralha.spu_getTotalizadorPorCategoria ?, ?, ?, ?, ?, ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setDate(1, new java.sql.Date(dataIni.getTime()));
			ps.setDate(2, new java.sql.Date(dataFim.getTime()));
			
			if (idLocal == 0) 
				ps.setNull(3, Types.INTEGER);
			else
				ps.setInt(3, idLocal);
			
			ps.setInt(4, tipoRelatorio);
			
			if (municipio == null) 
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, municipio);
			
			if (regiao == null) 
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, regiao);
			
			
			rs = ps.executeQuery();
			
			if (rs.next()) 
			{
				totalizadorCategoria.setCategoria(rs.getString("categoria"));
				totalizadorCategoria.setTotal(rs.getInt("total"));
			}
		}
		catch(Exception e) {
			erro = true;
			msgErro = "Erro ao obter dados do gráfico!";
			logger.error(msgErro + ": " + e.getMessage(), e);
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
		
			if (erro) {
				throw new SQLException("Erro ao obter dados do dashboard!");
			}
		}
		
		return totalizadorCategoria;
	}
}
