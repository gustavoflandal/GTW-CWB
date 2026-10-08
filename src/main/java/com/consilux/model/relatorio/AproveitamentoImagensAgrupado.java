package com.consilux.model.relatorio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

public class AproveitamentoImagensAgrupado {

	public static String getRelatorio(Date dataInicio, Date dataFim)
	throws ConexaoException, SQLException, ModelException {
		
		if(dataInicio == null)
			throw new ModelException("Argumento nulo: dataInicio");

		if(dataFim == null)
			throw new ModelException("Argumento nulo: dataFim");
		
		if(dataInicio.after(dataFim))
			throw new ModelException("Argumentos inválidos: dataInicio deve ser anterior à dataFim");
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try {
			conn = Conexao.getConexao();
			
			cs = conn.prepareCall("{? = call fcn_getAproveitamentoImagensAgrupadoPeriodo(?, ?)}");
			cs.registerOutParameter(1, Types.LONGNVARCHAR);
			cs.setTimestamp(2, new Timestamp(dataInicio.getTime()));
			cs.setTimestamp(3, new Timestamp(dataFim.getTime()));			
			cs.execute();
			
			StringBuilder sbRelatorio = new StringBuilder();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			// Tabela externa (para centralizar)
			sbRelatorio.append("<table width=\"100%\" height=\"100%\">");
			sbRelatorio.append("<tr>");
			sbRelatorio.append("<td align=\"center\" valign=\"top\">");
			
			// Tabela interna
			sbRelatorio.append("<table width=\"600px\" height=\"100¨\">");
			
			// 1 linha vazia.
			sbRelatorio.append("<tr><td>&nbsp</td><tr>");
			
			sbRelatorio.append("<tr><td><h3>Relatório Agrupado de Aproveitamento Imagens</h3></td></tr>");
			sbRelatorio.append("<tr><td>De: <b>" + dateFormat.format(dataInicio)
					+ "</b> até <b>" + dateFormat.format(dataFim) + "</b></td></tr>");

			// 2 linhas vazias
			sbRelatorio.append("<tr><td>&nbsp</td><tr>");
			sbRelatorio.append("<tr><td>&nbsp</td><tr>");			
			
			// Coloca o resultado vindo do SQL Server dentro de uma linha da tabela interna.
			sbRelatorio.append("<tr><td>");
			sbRelatorio.append(cs.getString(1));
			sbRelatorio.append("</td></tr></table>");
			
			// Fecha tabela externa
			sbRelatorio.append("</td></tr></table>");
			
			return sbRelatorio.toString();
		}
		finally {
			if (cs != null)
				cs.close();
			if (conn != null)
				conn.close();
		}
	}
	
}
