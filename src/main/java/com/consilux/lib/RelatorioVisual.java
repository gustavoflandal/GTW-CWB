/**
 * 
 */
package com.consilux.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.consilux.exportalista.ExportaLista;
import com.consilux.infra.exception.ConexaoException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * @author fos
 */
public class RelatorioVisual {

	private static Logger logger = Logger.getLogger(RelatorioVisual.class); 
	private Map<String, Object> parametros = new HashMap<String, Object>();
	public Map<String, Object> getParametros() {
		return parametros;
	}

	public void setParametros(Map<String, Object> parametros) {
		this.parametros = parametros;
	}

	private JasperPrint relatorio;
	public JasperPrint getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(JasperPrint relatorio) {
		this.relatorio = relatorio;
	}

	private String arquivoLayout;

	public RelatorioVisual(String arquivoLayout) {
		this.arquivoLayout = arquivoLayout;
	}

	public void preencheRelatorio() throws JRException, ConexaoException {
	    if (parametros.containsKey("REPORT_DATA_SOURCE")) {
	        JRBeanCollectionDataSource ds = (JRBeanCollectionDataSource) parametros.get("REPORT_DATA_SOURCE");
	        relatorio = JasperFillManager.fillReport(arquivoLayout, parametros, ds);
	    } else {
	        preencheRelatorio((Connection) null);
	    }
	}
	
	public void preencheRelatorio(Connection conn) throws JRException, ConexaoException {
		Connection connInt = conn;
		
		try {
			if (connInt == null)
				connInt = Conexao.getConexao();
			Locale locale = new Locale("pt", "BR");
			parametros.put(JRParameter.REPORT_LOCALE, locale);
			relatorio = JasperFillManager.fillReport(arquivoLayout, parametros, connInt);
		}
		finally {
			try {
				if (conn == null && connInt != null) //Só fecha a conexão se não for passada.
					connInt.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}		
	}
	
	
	
	public void preencheRelatorio(List<? extends ExportaLista> lista) throws JRException, ConexaoException {

		try {
			
			relatorio = JasperFillManager.fillReport(arquivoLayout, parametros, new JRBeanCollectionDataSource (lista)); 
			
		}
		finally {			
		}		
	}
	public void adicParametro(String chave, InputStream valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, Date valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, Integer valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, String valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, Boolean valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, Double valor) {
		parametros.put(chave, valor);
	}

	public void adicParametro(String chave, List<? extends Object> valor) {
		parametros.put(chave, valor);
	}
	
	public void adicDataSource(List<?> lista) {
	    parametros.put("REPORT_DATA_SOURCE", new JRBeanCollectionDataSource(lista));
	}

	public void exportReportToPdfStream(OutputStream out) throws JRException {
		JasperExportManager.exportReportToPdfStream(relatorio, out);
	}
	
	public void exportReportToCSV(OutputStream out) throws JRException {
		JRCsvExporter csv = new JRCsvExporter();
		
//		csv.setParameter(JRCsvExporterParameter.JASPER_PRINT, relatorio);
//		csv.setParameter(JRCsvExporterParameter.OUTPUT_STREAM,out);
//		csv.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
//		csv.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "UTF-8");
		
		csv.setExporterInput(new SimpleExporterInput(relatorio));
		csv.setExporterOutput(new SimpleWriterExporterOutput(out, Charset.forName("ISO-8859-1").displayName()));
		
		SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration ();
		configuration.setFieldDelimiter(";");
		csv.setConfiguration(configuration);
		
		csv.exportReport();
		
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("Erro ao exportar o relatório para CSV.", e);
			new ServletException("Erro ao gerar o relatório: "+e.getMessage());
		}
	}
	
	public void exportReportToXlsStream(OutputStream out) throws JRException {
		
//		JExcelApiExporter xls = new JExcelApiExporter();
//		
//		xls.setParameter(JRXlsExporterParameter.JASPER_PRINT, relatorio);
//		xls.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,out);
//		xls.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
//		xls.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);
//		xls.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET,Integer.decode("65000"));
//		xls.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
//		xls.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		
		JRXlsExporter xls = new JRXlsExporter();
		
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
//		configuration.setOnePagePerSheet(true);
		configuration.setIgnoreGraphics(false);
		
		xls.setExporterInput(new SimpleExporterInput(relatorio));
		xls.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		xls.setConfiguration(configuration);
		
		xls.exportReport();

		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("Erro ao exportar o relatório para XLS.", e);
			new ServletException("Erro ao gerar o relatório: "+e.getMessage());
		}
	}
}
