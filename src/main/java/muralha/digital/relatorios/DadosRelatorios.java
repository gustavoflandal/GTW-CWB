/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 28/09/2016

*********************************************************************************/

package muralha.digital.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio para busca de dados para os relatórios e gráficos da muralha digital
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 10/01/2021
 */
public class DadosRelatorios
{
	/**
	 * Método para busca de dados para o relatório distribuição por faixa de velocidade.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 10/01/2021
	 */
	public ArrayList<ItemRelatorios> RelDistribuicaoFaixaVelocidade(Date dtDia, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException
	{
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Data		DATE = ? ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelDistribuicaoFaixaVelocidade(@Data, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorios> listDadosRelatorio =  new ArrayList<ItemRelatorios>();
		ItemRelatorios itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setDate(1, new java.sql.Date(dtDia.getTime()));
			ps.setInt(2, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(3, intIdPista);
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[18];
				
				for (int i = 1; i <= 18; i++) {
					
					Integer valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemRelatorios(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas
				);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Método para busca de dados para o relatório distribuição por porte veicular.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/01/2021
	 */
	public ArrayList<ItemRelatorios> RelDistribuicaoPorteVeicular(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   muralha.fcn_getRelDistribuicaoPorteVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorios> listDadosRelatorio =  new ArrayList<ItemRelatorios>();
		ItemRelatorios itens;
		Integer[] celulasFluxoMoto;
		Integer[] celulasFluxoPequeno;
		Integer[] celulasFluxoMedio;
		Integer[] celulasFluxoGrande;
		Integer[] celulasFluxo;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxoMoto = new Integer[31];
				celulasFluxoPequeno = new Integer[31];
				celulasFluxoMedio = new Integer[31];
				celulasFluxoGrande = new Integer[31];
				celulasFluxo = new Integer[31];
				
				Integer valorFluxoMoto = 0;
				Integer valorFluxoPequeno = 0;
				Integer valorFluxoMedio = 0;
				Integer valorFluxoGrande = 0;
				Integer valorFluxo = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxoMoto = (int) rs.getInt("moto_" + String.valueOf(i));
					celulasFluxoMoto[i-1] = Integer.valueOf(valorFluxoMoto);
					
					valorFluxoPequeno = (int) rs.getInt("pequeno_" + String.valueOf(i));
					celulasFluxoPequeno[i-1] = Integer.valueOf(valorFluxoPequeno);
					
					valorFluxoMedio = (int) rs.getInt("medio_" + String.valueOf(i));
					celulasFluxoMedio[i-1] = Integer.valueOf(valorFluxoMedio);
					
					valorFluxoGrande = (int) rs.getInt("grande_" + String.valueOf(i));
					celulasFluxoGrande[i-1] = Integer.valueOf(valorFluxoGrande);
					
					valorFluxo = (int) rs.getInt("total_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
				}
				
				itens = new ItemRelatorios(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxoMoto,
											  celulasFluxoPequeno,
											  celulasFluxoMedio,
											  celulasFluxoGrande,
											  celulasFluxo,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_moto"),
											  rs.getInt("total_pequeno"),
											  rs.getInt("total_medio"),
											  rs.getInt("total_grande")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	/**
	 * Método para busca de dados para o relatório Fluxo Mensal por Classificação.
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/01/2021
	 */
	public ArrayList<ItemRelatorios> RelFluxoMensalPorClassificacao(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   muralha.fcn_getRelFluxoMensalPorClassificacao(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorios> listDadosRelatorio =  new ArrayList<ItemRelatorios>();
		ItemRelatorios itens;
		Integer[] celulasFluxoMoto;
		Integer[] celulasFluxoPasseio;
		Integer[] celulasFluxoUtilitario;
		Integer[] celulasFluxoCaminhao;
		Integer[] celulasFluxoOnibus;
		Integer[] celulasFluxo;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulasFluxoMoto = new Integer[31];
				celulasFluxoPasseio = new Integer[31];
				celulasFluxoUtilitario = new Integer[31];
				celulasFluxoCaminhao = new Integer[31];
				celulasFluxoOnibus = new Integer[31];
				celulasFluxo = new Integer[31];
				
				Integer valorFluxoMoto = 0;
				Integer valorFluxoPasseio = 0;
				Integer valorFluxoUtilitario = 0;
				Integer valorFluxoCaminhao = 0;
				Integer valorFluxoOnibus = 0;
				Integer valorFluxo = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valorFluxoMoto = (int) rs.getInt("moto_" + String.valueOf(i));
					celulasFluxoMoto[i-1] = Integer.valueOf(valorFluxoMoto);
					
					valorFluxoPasseio = (int) rs.getInt("Passeio_" + String.valueOf(i));
					celulasFluxoPasseio[i-1] = Integer.valueOf(valorFluxoPasseio);
					
					valorFluxoUtilitario = (int) rs.getInt("Utilitario_" + String.valueOf(i));
					celulasFluxoUtilitario[i-1] = Integer.valueOf(valorFluxoUtilitario);
					
					valorFluxoCaminhao = (int) rs.getInt("Caminhao_" + String.valueOf(i));
					celulasFluxoCaminhao[i-1] = Integer.valueOf(valorFluxoCaminhao);
					
					valorFluxoOnibus = (int) rs.getInt("Onibus_" + String.valueOf(i));
					celulasFluxoOnibus[i-1] = Integer.valueOf(valorFluxoOnibus);
					
					valorFluxo = (int) rs.getInt("total_" + String.valueOf(i));
					celulasFluxo[i-1] = Integer.valueOf(valorFluxo);
					
				}
				
				itens = new ItemRelatorios(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulasFluxoMoto,
											  celulasFluxoPasseio,
											  celulasFluxoUtilitario,
											  celulasFluxoCaminhao,
											  celulasFluxoOnibus,
											  celulasFluxo,
											  rs.getInt("total_fluxo"),
											  rs.getInt("total_moto"),
											  rs.getInt("total_Passeio"),
											  rs.getInt("total_Utilitario"),
											  rs.getInt("total_Onibus"),
											  rs.getInt("total_Caminhao")
										);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	public ArrayList<ItemRelatorios> relatorioVelocidadeMedia(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista)
		throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   dbo.fcn_getRelatorio6MedicaoFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorios> listDadosRelatorio =  new ArrayList<ItemRelatorios>();
		ItemRelatorios itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[31];
				Integer valor = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0.0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemRelatorios(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
	
	public ArrayList<ItemRelatorios> RelQuantidadePassagens(Integer intMes, Integer intAno, Integer intIdLocal, Integer intIdPista) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" DECLARE @Ano			INT = ? ");
		sbSQL.append(" DECLARE @Mes			INT = ? ");
		sbSQL.append(" DECLARE @Data_Ini	DATETIME ");
		sbSQL.append(" DECLARE @Data_Fim	DATETIME ");
		sbSQL.append(" DECLARE @Id_Local	INT = ? ");
		sbSQL.append(" DECLARE @Id_Pista	INT = ? ");
		
		sbSQL.append(" SET @Data_Ini = CAST(CAST(@Ano AS VARCHAR)+'-'+RIGHT('00'+CAST(@Mes AS VARCHAR),2)+'-01 00:00:00.000' AS DATETIME) ");
		sbSQL.append(" SET @Data_Fim = CAST(CONVERT(VARCHAR(10), DATEADD(DAY, -1, DATEADD(MONTH, 1, CAST(@Data_Ini AS DATETIME))), 120) + ' 23:59:59.000' AS DATETIME) ");

		sbSQL.append(" SELECT * ");
		sbSQL.append(" FROM   muralha.fcn_getRelatorioFluxoVeicular(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) rel ");

		sbSQL.append(" ORDER BY ");
		sbSQL.append(" 	 	  rel.hora ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<ItemRelatorios> listDadosRelatorio =  new ArrayList<ItemRelatorios>();
		ItemRelatorios itens;
		Integer[] celulas;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, intAno);
			ps.setInt(2, intMes);
			ps.setInt(3, intIdLocal);
			if (intIdPista != null) {
				ps.setInt(4, intIdPista);
			} else {
				ps.setNull(4, Types.INTEGER);
			}
			
			rs = ps.executeQuery();
			while (rs.next()){
				
				celulas = new Integer[31];
				Integer valor = 0;
				
				for (int i = 1; i <= 31; i++) {
					
					valor = 0;
					
					valor = (int) rs.getInt(String.valueOf(i));
					
					if (valor <= 0.0) {
						celulas[i-1] = 0;
					} else {
						celulas[i-1] = Integer.valueOf(valor);
					}
				}
				
				itens = new ItemRelatorios(rs.getInt("hora"),
											  rs.getString("hora_desc"),
											  celulas,
											  rs.getInt("total_hora")
											);
				
				listDadosRelatorio.add(itens);
			}
			
			return listDadosRelatorio;
				
		}catch (SQLException e) {
			throw new ModelException("ERRO de SQL", e);
		}	
		finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (sbSQL != null) {
				sbSQL = null;
			}
		}
	}
}
