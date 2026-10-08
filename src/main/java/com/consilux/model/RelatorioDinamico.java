package com.consilux.model;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.Periodo;
import com.consilux.model.beans.RelatorioDinamicoBean;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.RelatorioDinamicoGwtBean;

/**
 * Classe de modelo que representa um relatório dinâmico.
 * @author raoni
 */
public class RelatorioDinamico {

	/**
	 * Busca um relatório
	 * @return
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static RelatorioDinamicoBean buscarRelatorioDisponivelById(int idRelatorio) throws ConexaoException, SQLException {

		RelatorioDinamicoBean ret = null;
		
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT TOP 1 id_relatorio, nome, executa_function FROM sis_relatorio WHERE id_relatorio = ?");
			ps.setInt(1, idRelatorio);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				ret = new RelatorioDinamicoBean(rs.getInt("id_relatorio"), rs.getString("nome"), rs.getString("executa_function"));
			}
			
		}
		finally {
			if (conn != null)
			{
				try {
					conn.close();							
				} catch (SQLException e) {
					throw new ConexaoException("ERRO ao fechar conexão", e);
				} 
			}
		}
			
		return ret;
	}	
	
	/**
	 * Busca os relatórios disponíveis
	 * @return
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<RelatorioDinamicoBean> buscarRelatoriosDisponiveis(Integer idUsuario) throws ConexaoException, SQLException {

		List<RelatorioDinamicoBean> lRet = new ArrayList<RelatorioDinamicoBean>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT DISTINCT r.id_relatorio,");
		sbSQL.append("		nome,");
		sbSQL.append("		executa_function");
		sbSQL.append(" FROM");
		sbSQL.append(" 		sis_relatorio r");
		sbSQL.append(" JOIN");
		sbSQL.append(" 		sis_relatorio_direitos rd ON rd.id_relatorio = r.id_relatorio");
		sbSQL.append(" WHERE");
		sbSQL.append(" 		rd.id_usuario = ?");
		sbSQL.append("	OR rd.id_grupo IN (");
		sbSQL.append("		SELECT ug.id_grupo");
		sbSQL.append("		FROM sis_usuario_grupo ug WITH (NOLOCK) ");
		sbSQL.append("		WHERE ug.id_usuario = ?)");
		sbSQL.append(" ORDER BY");
		sbSQL.append(" 		nome");

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idUsuario);
			ps.setInt(2, idUsuario);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				lRet.add(new RelatorioDinamicoBean(rs.getInt("id_relatorio"), rs.getString("nome"), rs.getString("executa_function")));
			}
			
		}
		finally {
			if (conn != null)
			{
				try {
					conn.close();							
				} catch (SQLException e) {
					throw new ConexaoException("ERRO ao fechar conexão", e);
				}
			}
		}
			
		return lRet;
	}
	
	/**
	 * Exporta
	 * @param nomeFunction a função do SQL que deverá ser executada
	 * @param periodo o período desejado
	 * @param streamSaida o stream para escrever o XLS que vai ser criado 
	 * @throws ModelException
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void exportaXLS(String nomeFunction, Periodo periodo, OutputStream streamSaida) throws ModelException, ConexaoException,
		SQLException, IOException {
		
		if (streamSaida == null)
			throw new ModelException("Parâmetro inválido: streamSaida não pode ser nulo");		

		Connection conn = null;
		XSSFWorkbook workBook = new XSSFWorkbook();
		try {
			
			// Dispara a query para o banco		
			conn = Conexao.getConexao();
			ResultSet rs = buscarDados(conn, nomeFunction, periodo);
			
			// Recupera os metadados do ResultSet
			ResultSetMetaData rsMetaData = rs.getMetaData();
			
			// Monta uma planilha para colocar os resultados
			
			workBook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
			XSSFSheet sheet = workBook.createSheet();

			// Cria um linha para servir de cabeçalho, com o nome das colunas.
			XSSFRow row = sheet.createRow(0);
			
		    for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
			    XSSFCell cell = row.getCell(i);
			    cell.setCellValue(new XSSFRichTextString(rsMetaData.getColumnName(i+1)));
		    }

		    // Prepara um estilo para fazer formatação de datas (que pode ser usado ou não, vai depender das
		    // colunas que estão vindo do ResultSet).
		    XSSFDataFormat dataFormat = workBook.createDataFormat();
	    	XSSFCellStyle cellStyle = workBook.createCellStyle();
	    	cellStyle.setDataFormat(dataFormat.getFormat("dd/mm/yyyy"));			    
		    
			// Itera o resultset e coloca na planilha
			int i = 1;
			while (rs.next()) {
				
				// Cria uma nova linha
			    row = sheet.createRow(i);
			    
			    // Variáveis temporárias que armazenam o que está vindo do ResultSet
			    BigDecimal tmpNumber;
			    Boolean tmpBoolean;
			    String tmpString;
			    Timestamp tmpStamp;
			    
			    // Cria as células da linha e popula os valores
			    for (int j = 0; j < rsMetaData.getColumnCount(); j++) {
			    	
			    	XSSFCell cell = row.getCell(j);
			    	int tipoColuna = rsMetaData.getColumnType(j + 1);
			    	
					if (tipoColuna == Types.BIGINT || tipoColuna == Types.INTEGER  ||
							tipoColuna == Types.SMALLINT || tipoColuna == Types.TINYINT ||
							tipoColuna == Types.DOUBLE || tipoColuna == Types.FLOAT) {
						
						// No Excel, estes acabam virando "Tipos numéricos"
		    			tmpNumber = rs.getBigDecimal(j + 1);
		    			if (!rs.wasNull()) {
		    				cell.setCellValue(tmpNumber.doubleValue());
		    			}

					} else if (tipoColuna == Types.BIT || tipoColuna == Types.BOOLEAN) {
						
						// No Excel, estes acabam virando "Tipo boolean"
		    			tmpBoolean = rs.getBoolean(j + 1);
		    			if (!rs.wasNull()) {
		    				cell.setCellValue(tmpBoolean);
		    			}
		   			
					} else if (tipoColuna == Types.LONGNVARCHAR || tipoColuna == Types.LONGVARCHAR || 
							tipoColuna == Types.NVARCHAR || tipoColuna == Types.VARCHAR ||
							tipoColuna == Types.NCHAR || tipoColuna == Types.CHAR) {
					
						// No Excel, estes acabam virando "Tipos String"
		    			tmpString = rs.getString(j + 1);
		    			if (!rs.wasNull()) {
		    				cell.setCellValue(new XSSFRichTextString(tmpString));
		    			}
		    			
					} else if (tipoColuna == Types.DATE || tipoColuna == Types.TIME || 
							tipoColuna == Types.TIMESTAMP) {
					
						// No Excel, estes acabam virando "Tipos Data"
						tmpStamp = rs.getTimestamp(j + 1);
		    			if (!rs.wasNull()) {
		    				cell.setCellValue(tmpStamp);
		    				cell.setCellStyle(cellStyle);		    				
		    			}
					} else {
						// Não descobriu o tipo da coluna. Levanta ERRO
						throw new ModelException("Tipo não suportado para o relatório: [" + tipoColuna + "]");
					}			    	
			    }
			    
			    i++;
			}
			workBook.write(streamSaida);
		}
		finally {
			if (conn != null)
				conn.close();
			
			if (workBook != null)
				workBook.close();
		}
	}		
	
	/**
	 * Conta os dados (no de registros) que seriam gerados em um relatório.
	 * @param nomeFunction a função do SQL que deverá ser executada
	 * @param periodo o período desejado
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static int contarDados(String nomeFunction, Periodo periodo) throws ModelException, ConexaoException, SQLException {

		if (nomeFunction == null || nomeFunction.length() == 0)
			throw new ModelException("Parâmetro inválido: nomeFunction");
		
		if (periodo == null || periodo.getDataInicio() == null || periodo.getDataFim() == null)
			throw new ModelException("Parâmetro inválido: periodo");

		Connection conn = null;
		
		try {
			conn = Conexao.getConexao();
			// Cria a query, executando uma chamada a função desejada
			PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + nomeFunction + "(?, ?)");
	
			// Ajusta os parâmetros
			ps.setDate(1, new java.sql.Date(periodo.getDataInicio().getTime()));
			ps.setDate(2, new java.sql.Date(periodo.getDataFim().getTime()));
				
			if (ps.execute())
			{
				ResultSet rs = ps.getResultSet();
				rs.next();
				return rs.getInt(1);
			}
			else
			{
				throw new ModelException("Não foi possível contar os registros.");			
			}
		}
		finally {
			if (conn != null)
				conn.close();
		}		
		
	}		
	
	/**
	 * Busca os dados de um relatório e monta um ResultSet.
	 * @param conn uma conexão com o banco de dados
	 * @param nomeFunction a função do SQL que deverá ser executada
	 * @param periodo o período desejado
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	private static ResultSet buscarDados(Connection conn, String nomeFunction, Periodo periodo) throws ModelException, ConexaoException, SQLException {

		if (nomeFunction == null || nomeFunction.length() == 0)
			throw new ModelException("Parâmetro inválido: nomeFunction");
		
		if (periodo == null || periodo.getDataInicio() == null || periodo.getDataFim() == null)
			throw new ModelException("Parâmetro inválido: periodo");

		// Cria a query, executando uma chamada a função desejada
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + nomeFunction + "(?, ?)");

		// Ajusta os parâmetros
		ps.setDate(1, new java.sql.Date(periodo.getDataInicio().getTime()));
		ps.setDate(2, new java.sql.Date(periodo.getDataFim().getTime()));
			
		if (ps.execute())
		{
			return ps.getResultSet();
		}
		else
		{
			throw new ModelException("Não foi possível obter os resultados.");			
		}
	}	
	
	/**
	 * Converte um bean de relatório dinâmico (do GTW) para o seu equivalente do GWT. 
	 * @param beanOriginal o bean original (do GTW) que se deseja converter
	 * @return
	 */
	public static RelatorioDinamicoGwtBean toGwtBean(RelatorioDinamicoBean beanOriginal) {
		RelatorioDinamicoGwtBean beanGwt = new RelatorioDinamicoGwtBean(beanOriginal.getId(), beanOriginal.getNome());
		return beanGwt;
	}
	
	/**
	 * Converte uma coleção beans de relatório dinâmico (do GTW) para os seu equivalentes do GWT. 
	 * @param beanOriginal uma coleção com os beans originais (do GTW) que se deseja converter.
	 * @return
	 */
	public static List<RelatorioDinamicoGwtBean> toGwtBeans(Iterable<RelatorioDinamicoBean> listaOriginal) {
		
		List<RelatorioDinamicoGwtBean> lRet = new ArrayList<RelatorioDinamicoGwtBean>();
		
		for (RelatorioDinamicoBean beanOriginal : listaOriginal)
		{
			RelatorioDinamicoGwtBean beanGwt = new RelatorioDinamicoGwtBean(beanOriginal.getId(), beanOriginal.getNome());
			lRet.add(beanGwt);
		}
		
		return lRet;
	}
	
}
