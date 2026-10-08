package com.consilux.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CSVUtils {

	public static StringBuilder ObterCVSdeResultSet(ResultSet rs) throws SQLException {
		return ObterCVSdeResultSet(rs,"");
	}
	public static StringBuilder ObterCVSdeResultSet(ResultSet rs, String se_nulo) throws SQLException {
		StringBuilder sb1 = new StringBuilder();
		String newline = System.getProperty("line.separator");
		
		int colunas;
		
		// Obter o Cabeçalho do arquivo XML
		{
			List<String> cabecalho = new ArrayList<String>();

			ResultSetMetaData meta = rs.getMetaData();
			colunas = meta.getColumnCount();

			for (int t = 1; t <= colunas; t++)
				cabecalho.add(meta.getColumnName(t));

//			sb1.append(meta.getTableName(0));
//			sb1.append('^');
			sb1.append(StringUtils.join(cabecalho, ';'));
			sb1.append(newline);

			cabecalho.clear();
		}
		
		List<String> elementos = new ArrayList<String>();
		while (rs.next())
		{
			for (int t = 1; t <= colunas; t++)
				elementos.add(rs.getObject(t) != null ? rs.getObject(t).toString().trim() : se_nulo);
			
			sb1.append(StringUtils.join(elementos, ';'));
			sb1.append(newline);
			
			elementos.clear();
		}
		
		return sb1;
	}

}
