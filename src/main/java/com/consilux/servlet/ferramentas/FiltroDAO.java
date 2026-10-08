package com.consilux.servlet.ferramentas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.FiltroBean;
import com.consilux.model.exception.ModelException;

public class FiltroDAO {
	
public Integer cadastrar(FiltroBean f) throws ConexaoException, SQLException, ModelException {
	
		Integer iRet = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" INSERT INTO ");
		sbSQL.append(" filtro (nome_filtro, id_enquadramento, id_processo, id_local, id_pista, id_classe, data_ini, data_fim, sql_criterio, set_id_inconsistencia, set_espera, data_validade, id_usuario) ");
			
		sbSQL.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, f.getNomeFiltro());
			
//			if (f.getIdFiltroInconsistencias() != null)
//				ps.setInt(2, f.getIdFiltroInconsistencias());
//			else
							
			
			if (f.getIdFiltroEnquadramento() != null)
				ps.setInt(2, f.getIdFiltroEnquadramento());
			else
				ps.setNull(2, Types.INTEGER);
			

			if (f.getIdFiltroProcesso() != null)
				ps.setInt(3, f.getIdFiltroProcesso());
			else
				ps.setNull(3, Types.INTEGER);
			
		
			if (f.getIdLocal() != null)
				ps.setInt(4, f.getIdLocal());
			else
				ps.setNull(4, Types.INTEGER);
			
			
			if (f.getIdPista() != null)
				ps.setInt(5, f.getIdPista());
			else
				ps.setNull(5, Types.INTEGER);

			
			//id_classe pode ser nula, para significar todos os veiculos
			if (f.getIdclasse() == null || "null".equalsIgnoreCase(f.getIdclasse()))
				ps.setNull(6, Types.VARCHAR);
			else
				ps.setString(6, f.getIdclasse());

			
			
			if (f.getDtIni() != null)
				ps.setTimestamp(7, new Timestamp(f.getDtIni().getTime()));
			else
				ps.setNull(7, Types.TIMESTAMP);
			
			
			if (f.getDtFim() != null)
				ps.setTimestamp(8, new Timestamp(f.getDtFim().getTime()));
			else
				ps.setNull(8, Types.TIMESTAMP);
			
			
			
			/**
			 * sqlcritério
			 */
			if (f.getSqlCriterio() == null || "".equalsIgnoreCase(f.getSqlCriterio()))
				ps.setNull(9, Types.VARCHAR);
			else
				ps.setString(9, f.getSqlCriterio());
			

			if (f.getSetIdInconsistencia() != null)
				ps.setInt(10, f.getSetIdInconsistencia());
			else
				ps.setNull(10, Types.INTEGER);
			
			if (f.getEspera() != null)
				ps.setBoolean(11, f.getEspera());
			else
				ps.setNull(11, Types.BOOLEAN);
			
			if (f.getDtValidade() != null)
				ps.setTimestamp(12, new Timestamp(f.getDtValidade().getTime()));
			else
				ps.setNull(12, Types.TIMESTAMP);
			
			if (f.getIdUsuario() != null)
				ps.setInt(13, f.getIdUsuario());
			else
				ps.setNull(13, Types.INTEGER);
			
			ps.execute();
			ps.close();
			
			ps = conn.prepareStatement("SELECT @@IDENTITY");
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				iRet = rs.getInt(1);
			}
			ps.close();

		} 
		finally {
			if (conn != null)
				conn.close();							
		}
		
		return iRet;
	}




	public List<Integer> buscarInfracoesExistentesParaFiltro(FiltroBean f) throws ConexaoException, SQLException, ModelException {
	
		List<Integer> listaIdInfracao = new ArrayList<Integer>();
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" DECLARE @STRING NVARCHAR(MAX)" +
		" SELECT @STRING = N'select id_infracao from infracao_completa (NOLOCK) where id_infracao in (' + dbo.fcn_getSqlVerificaFiltros( ? , ? , ? ,  ?,  ? , ? , ? , ? , ? , 0) + ') and id_enquadramento > 1' " +
		" EXECUTE sp_executesql @STRING ");
		
		//@id_infracao INT ,
		//@id_enquadramento INT,
		//@id_local INT,
		//@id_classe CHAR(1),
		//@data_ini DATETIME,
		//@data_fim DATETIME,
		//@sql_criterio varchar(400),
		//@select_count BIT

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			//nunca terei o id_infracao para passar
			ps.setNull(1, Types.INTEGER);
										
			
			if (f.getIdFiltroEnquadramento() != null)
				ps.setInt(2, f.getIdFiltroEnquadramento());
			else
				ps.setNull(2, Types.INTEGER);
										
/*			//AGORA PESQUISA EM TODO O PROCESSAMENTO INDIFERENTE DO PROCESSO.
			if (f.getIdFiltroProcesso() != null)
				ps.setInt(3, f.getIdFiltroProcesso());
			else*/
				ps.setNull(3, Types.INTEGER);
			
		
			if (f.getIdLocal() != null)
				ps.setInt(4, f.getIdLocal());
			else
				ps.setNull(4, Types.INTEGER);
			
			
			if (f.getIdPista() != null)
				ps.setInt(5, f.getIdPista());
			else
				ps.setNull(5, Types.INTEGER);

			
			//id_classe pode ser nula, para significar todos os veiculos
			if (f.getIdclasse() == null || "null".equalsIgnoreCase(f.getIdclasse()))
				ps.setNull(6, Types.VARCHAR);
			else
				ps.setString(6, f.getIdclasse());

			if (f.getDtIni() != null)
				ps.setTimestamp(7, new Timestamp(f.getDtIni().getTime()));
			else
				ps.setNull(7, Types.TIMESTAMP);
			
			
			if (f.getDtFim() != null)
				ps.setTimestamp(8, new Timestamp(f.getDtFim().getTime()));
			else
				ps.setNull(8, Types.TIMESTAMP);
			
			/**
			 * sqlcritério
			 */
			if (f.getSqlCriterio() == null || "".equalsIgnoreCase(f.getSqlCriterio()))
				ps.setNull(9, Types.VARCHAR);
			else
				ps.setString(9, f.getSqlCriterio());

			rs = ps.executeQuery();
			
			while (rs.next()) {
				Integer fb = rs.getInt("id_infracao");
				listaIdInfracao.add(fb);
			}

		} catch (SQLException e) {
			throw e;
		} catch (ConexaoException e) {
			throw e;
		} catch (Exception e) {
			throw new ModelException("Erro fatal: "+e.getMessage(), e);
		}
		finally {
			if (conn != null)
				conn.close();							
		}
		return listaIdInfracao;
	}

	
	public FiltroBean buscarFiltro( int idFiltro ) throws ConexaoException, SQLException{
		
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT TOP 1 "); 
		sbSQL.append("	 [id_filtro] ");
		sbSQL.append("  ,[nome_filtro] ");
		sbSQL.append("  ,[id_enquadramento] ");
		sbSQL.append("	,[id_processo] ");
		sbSQL.append("	,[id_local] ");
		sbSQL.append("	,[id_classe] ");
		sbSQL.append("	,[data_ini] ");
		sbSQL.append("	,[data_fim] ");
		sbSQL.append("	,[sql_criterio] ");
		sbSQL.append("	,[set_id_inconsistencia] ");
		sbSQL.append("	,[set_espera] ");
		sbSQL.append("	,[data_validade] ");
		sbSQL.append("	,[id_usuario] ");
		sbSQL.append("	,[id_pista] ");
		sbSQL.append(" FROM [filtro] ");
		sbSQL.append(" WHERE ");
		sbSQL.append("	[id_filtro] = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());				
			ps.setInt(1, idFiltro);

			rs = ps.executeQuery();
			if (rs.next()) {
				
				FiltroBean fb = new FiltroBean();
				
				fb.setNomeFiltro(rs.getString("nome_filtro") );
				fb.setDtIni(rs.getString("data_ini") != null ? rs.getTimestamp("data_ini")  : null );
				fb.setDtFim(rs.getString("data_fim") != null ? rs.getTimestamp("data_fim") : null );
				fb.setDtValidade(rs.getString("data_validade") != null ? rs.getTimestamp("data_validade")  : null);
				fb.setIdclasse(rs.getString("id_classe"));
				fb.setIdFiltroEnquadramento(rs.getString("id_enquadramento") != null ? rs.getInt("id_enquadramento")  : null );
				fb.setIdFiltroProcesso(rs.getString("id_processo") != null ? rs.getInt("id_processo")  : null );
				fb.setIdLocal(rs.getString("id_local") != null ? rs.getInt("id_local") : null);
				fb.setIdPista(rs.getString("id_pista") != null ? rs.getInt("id_pista")  : null );
				fb.setSetIdInconsistencia(rs.getString("set_id_inconsistencia") != null ? rs.getInt("set_id_inconsistencia") : null );
				fb.setSetEspera(rs.getString("set_espera") != null ? rs.getBoolean("set_espera")  : null );
				fb.setSqlCriterio(rs.getString("sql_criterio"));
				fb.setIdUsuario(rs.getString("id_usuario") != null ? rs.getInt("id_usuario") : null );
				
				return fb;
			}
			else {
				return null;
			}

		}		
		finally {
			if (conn != null)
				conn.close();							
		}
	}
	
}
