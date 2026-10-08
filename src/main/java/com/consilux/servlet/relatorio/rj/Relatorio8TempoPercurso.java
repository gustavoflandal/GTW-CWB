package com.consilux.servlet.relatorio.rj;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.ferramenta.Percurso;
import com.consilux.model.relatorio.rj.DadosTempoPercurso;
import com.consilux.model.relatorio.rj.ItemTempoPercurso;

/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 08
 * - Média diária do tempo de percurso e volume médio de corredores mensal por
 * data.
 * 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 23/11/2016
 */
public class Relatorio8TempoPercurso extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio8TempoPercurso.class);

	private class Param1 {
		public ArrayList<Percurso> percursos;
		public Integer celula;
		public Integer celulaInicio;
		public Integer celulaFim;
		public Integer intIdCorredorAnterior;
		public Sheet sheet;
		public Row cabecalho0;
		public Row cabecalho1;
		public Row cabecalho2;
		public ArrayList<ItemTempoPercurso> dadosRelatorio;
		public Row linhaSheet = null;
		public boolean colunaData = true;
		public String strCorredorAnterior = null;
		public Integer linha_ref = 0;
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Date data = new Date();

		String strMes = request.getParameter("mes");
		String strAno = request.getParameter("ano");
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		Date dtData = null;

		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");

		Integer intMes = null, intAno = null;

		// Validações Necessárias para montar relatório
		if ((strMes.equals("")) || (strAno.equals(""))) {
			new Mensagem(response).showErro("O Mês e o Ano devem ser informados!", "javascript:window.close();");
			return;
		}

		if (!isDigit(strMes) || !isDigit(strAno)) {
			new Mensagem(response).showErro("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
			return;
		}

		try {

			intMes = Integer.parseInt(strMes);
			intAno = Integer.parseInt(strAno);

			if (intMes < 1 || intMes > 12) {
				new Mensagem(response).showErro("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
				return;
			}
			if (intAno < 1900 || intAno > 2050) {
				new Mensagem(response).showErro("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
				return;
			}

			dtData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/" + (strMes.trim().length() == 1 ? "0" + strMes.trim() : strMes.trim()) + "/" + strAno + " 00:00:00");
		} catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}

		try {

			// Criando o arquivo fisico
			String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2, 4) + "_tempo.08-" + mesExtenso.format(dtData).substring(0, 3).toLowerCase() + intAno.toString() + ".xlsx";

			response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");

			// Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
			wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);

			// Cria uma planilha Excel
			Sheet sheet = wb.createSheet("8-Consilux-Tempo-" + mesExtenso.format(dtData).substring(0, 3).toLowerCase() + intAno.toString());
			sheet.setVerticallyCenter(true);
			sheet.setHorizontallyCenter(true);

	        //Fonte
            Font f = wb.createFont();
            f.setFontHeightInPoints((short) 14);
            f.setColor(Font.COLOR_NORMAL);
            f.setBold(true);
            f.setFontName("Calibri");

            CellStyle estilo;
            estilo = wb.createCellStyle();
            estilo.setAlignment(HorizontalAlignment.LEFT);
            estilo.setVerticalAlignment(VerticalAlignment.CENTER);
            estilo.setFont(f);

			// Cria uma linha na Planilha.
			Row cabecalhoContrato = sheet.createRow(0);
			cabecalhoContrato.getCell(0).setCellValue("TEMPO DE PERCURSO, FLUXO VEICULAR, MOBILIDADE E VELOCIDADE DE CORREDORES, MENSAL POR DATA");
			cabecalhoContrato.getCell(0).setCellStyle(estilo);
			cabecalhoContrato.getCell(1).setCellValue("");
			cabecalhoContrato.getCell(1).setCellStyle(estilo);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

			GerarRelatorio(intMes, intAno, response, sheet, wb, nomeArquivo, data);

			return;
		} catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	public void GerarRelatorio(Integer intMes, Integer intAno,
			HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb,
			String nomeArquivo, Date data) throws IOException,
			ConexaoException, SQLException, ModelException, ParseException {

		ColecaoEstilos ce = new ColecaoEstilos(wb);

		Param1 p1 = new Param1();
		p1.percursos = null;
		p1.celula = 0;
		p1.celulaInicio = 0;
		p1.celulaFim = 0;
		p1.intIdCorredorAnterior = 0;
		p1.sheet = sheet;
		p1.cabecalho0 = sheet.createRow(2);
		p1.cabecalho1 = sheet.createRow(3);
		p1.cabecalho2 = sheet.createRow(4);

		p1.cabecalho0.getCell(0).setCellValue("Dados por pista-sentido");
		p1.cabecalho0.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		p1.cabecalho0.getCell(1).setCellValue("");
		p1.cabecalho0.getCell(1).setCellStyle(ce.estiloCabecalhoFiltro);
		p1.cabecalho0.getCell(2).setCellValue("");
		p1.cabecalho0.getCell(2).setCellStyle(ce.estiloCabecalhoFiltro);
		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho0.getRowNum(), p1.cabecalho0.getRowNum(), 0, 2));

		p1.cabecalho1.setHeight((short) (p1.cabecalho1.getHeight() * 3));
		p1.cabecalho2.setHeight((short) (p1.cabecalho2.getHeight() * 2));

		p1.cabecalho1.getCell(0).setCellValue("Nome do Corredor");
		p1.cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo);
		p1.cabecalho1.getCell(1).setCellValue("");
		p1.cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo);

		p1.cabecalho2.getCell(0).setCellValue("Data");
		p1.cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo);
		p1.cabecalho2.getCell(1).setCellValue("Dia da Semana");
		p1.cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo);

		sheet.setColumnWidth(0, 3500);
		sheet.setColumnWidth(1, 3500);

		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), 0, 1));

		Percurso dadosPercurso = new Percurso();
		p1.percursos = dadosPercurso.obterCorredores();

		p1.celula = 2;
		p1.celulaInicio = p1.celula;
		p1.celulaFim = p1.celula;
		p1.intIdCorredorAnterior = null;

		GerarCabecalhos(p1, false, ce);

		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), p1.celulaInicio, p1.celulaFim));

		DadosTempoPercurso dadosTempoPercurso = new DadosTempoPercurso();

		p1.celulaInicio = 0;
		p1.celulaFim = 0;

		p1.linhaSheet = null;
		p1.colunaData = true;

		p1.linha_ref = p1.cabecalho2.getRowNum() + 1;

		for (int i = 0; i < p1.percursos.size(); i++) {

			// Buscando informações para popular planilhas
			p1.dadosRelatorio = dadosTempoPercurso.relatorio8TempoPercurso(intMes, intAno, p1.percursos.get(i).getIdPercurso());

			p1.celulaInicio = p1.celulaFim;

			GerarDados(p1, ce);

			p1.colunaData = false;
			p1.celulaFim = p1.celula;
		}

		int qtdeLinhas = sheet.getLastRowNum() + 3;

		// Criando as colunas do corpo
		p1.cabecalho0 = sheet.createRow((short) qtdeLinhas++);
		p1.cabecalho1 = sheet.createRow((short) qtdeLinhas++);
		p1.cabecalho2 = sheet.createRow((short) qtdeLinhas++);

		p1.cabecalho0.getCell(0).setCellValue("Dados por pista-sentido-faixa");
		p1.cabecalho0.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		p1.cabecalho0.getCell(1).setCellValue("");
		p1.cabecalho0.getCell(1).setCellStyle(ce.estiloCabecalhoFiltro);
		p1.cabecalho0.getCell(2).setCellValue("");
		p1.cabecalho0.getCell(2).setCellStyle(ce.estiloCabecalhoFiltro);
		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho0.getRowNum(), p1.cabecalho0.getRowNum(), 0, 2));

		p1.cabecalho1.setHeight((short) (p1.cabecalho1.getHeight() * 3));
		p1.cabecalho2.setHeight((short) (p1.cabecalho2.getHeight() * 2));

		p1.cabecalho1.getCell(0).setCellValue("Nome do Corredor");
		p1.cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo);
		p1.cabecalho1.getCell(1).setCellValue("");
		p1.cabecalho1.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo);

		p1.cabecalho2.getCell(0).setCellValue("Data");
		p1.cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo);
		p1.cabecalho2.getCell(1).setCellValue("Dia da Semana");
		p1.cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo);

		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), 0, 1));

		p1.percursos = dadosPercurso.obterCorredoresPorFaixa();

		p1.celula = 2;
		p1.celulaInicio = p1.celula;
		p1.celulaFim = p1.celula;
		p1.intIdCorredorAnterior = null;

		GerarCabecalhos(p1, true, ce);

		sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), p1.celulaInicio, p1.celulaFim));

		p1.linha_ref = p1.cabecalho2.getRowNum() + 1;

		p1.celula = 0;
		p1.celulaInicio = p1.celula;
		p1.celulaFim = p1.celula;
		p1.colunaData = true;

		for (int i = 0; i < p1.percursos.size(); i++) {

			// Buscando informações para popular planilhas
			p1.dadosRelatorio = dadosTempoPercurso.relatorio8TempoPercursoFaixa(intMes, intAno, p1.percursos.get(i).getIdPercurso(), p1.percursos.get(i).getFaixa());

			p1.celulaInicio = p1.celulaFim;

			GerarDados(p1, ce);

			p1.colunaData = false;
			p1.celulaFim = p1.celula;
		}

		sheet = wb.getSheetAt(0);

		// Salvando o arquivo
		ServletOutputStream out = response.getOutputStream();
		wb.write(out);
		out.close();
	}

	private void GerarCabecalhos(Param1 p1, boolean faixa, ColecaoEstilos ce) {
		for (int i = 0; i < p1.percursos.size(); i++) {

			if (faixa) {
				if ((p1.strCorredorAnterior != null) && (!p1.strCorredorAnterior.equals(p1.percursos.get(i).getNomePercurso().trim()))) {
					p1.sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), p1.celulaInicio, p1.celulaFim));
				}
			} else {
				if ((p1.intIdCorredorAnterior != null) && (!p1.intIdCorredorAnterior.equals(p1.percursos.get(i).getIdPercurso()))) {
					p1.sheet.addMergedRegion(new CellRangeAddress(p1.cabecalho1.getRowNum(), p1.cabecalho1.getRowNum(), p1.celulaInicio, p1.celulaFim));
				}
			}

			p1.celulaInicio = p1.celula;
			p1.cabecalho1.getCell(p1.celula).setCellValue(p1.percursos.get(i).getNomePercurso().trim());
			p1.cabecalho1.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.cabecalho2.getCell(p1.celula).setCellValue("Tempo médio (s)");
			p1.cabecalho2.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.sheet.setColumnWidth(p1.celula, 3500);
			p1.celulaFim = p1.celula;
			p1.celula++;
			p1.cabecalho1.getCell(p1.celula).setCellValue("");
			p1.cabecalho1.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.cabecalho2.getCell(p1.celula).setCellValue("Indice de Mobilidade (IM)");
			p1.cabecalho2.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.sheet.setColumnWidth(p1.celula, 3500);
			p1.celulaFim = p1.celula;
			p1.celula++;
			p1.cabecalho1.getCell(p1.celula).setCellValue("");
			p1.cabecalho1.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.cabecalho2.getCell(p1.celula).setCellValue("Velocidade de Percurso");
			p1.cabecalho2.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.sheet.setColumnWidth(p1.celula, 3500);
			p1.celulaFim = p1.celula;
			p1.celula++;
			p1.cabecalho1.getCell(p1.celula).setCellValue("");
			p1.cabecalho1.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.cabecalho2.getCell(p1.celula).setCellValue("Volume Veicular");
			p1.cabecalho2.getCell(p1.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			p1.sheet.setColumnWidth(p1.celula, 3500);
			p1.celulaFim = p1.celula;
			p1.celula++;

			if (faixa)
				p1.strCorredorAnterior = p1.percursos.get(i).getNomePercurso().trim();
			else
				p1.intIdCorredorAnterior = p1.percursos.get(i).getIdPercurso();
		}
	}

	private void GerarDados(Param1 p1, ColecaoEstilos ce) {
		
		boolean fds = false;
		SimpleDateFormat formatoDia = new SimpleDateFormat("dd/MM/yyyy");

		for (int r = 0; r < p1.dadosRelatorio.size(); r++) {

			p1.celula = p1.celulaInicio;

			fds = p1.dadosRelatorio.get(r).getDiaSemana() == 7 || p1.dadosRelatorio.get(r).getDiaSemana() == 1;

			if (p1.sheet.getRow((short) (r + p1.linha_ref)) == null) {
				p1.linhaSheet = p1.sheet.createRow((short) (r + p1.linha_ref));
			} else {
				p1.linhaSheet = p1.sheet.getRow((short) (r + p1.linha_ref));
			}

			if (p1.colunaData) {
				if (p1.dadosRelatorio.get(r).getDia() == 99) {
					p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getDiaSemanaDesc());
					p1.linhaSheet.getCell(p1.celula).setCellStyle(ce.estiloItensCentralizadoCinza);
					p1.celula++;
					p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getDiaSemanaDesc());
					p1.linhaSheet.getCell(p1.celula).setCellStyle(ce.estiloItensCentralizadoCinza);
					p1.celula++;
					p1.sheet.addMergedRegion(new CellRangeAddress(p1.linhaSheet.getRowNum(), p1.linhaSheet.getRowNum(), 0, 1));
				} else {
					p1.linhaSheet.getCell(p1.celula).setCellValue(formatoDia.format(p1.dadosRelatorio.get(r).getDtData()));
					p1.linhaSheet.getCell(p1.celula).setCellStyle(fds ? ce.estiloItensCentralizadoVerde : ce.estiloItensCentralizado);
					p1.celula++;
					p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getDiaSemanaDesc());
					p1.linhaSheet.getCell(p1.celula).setCellStyle(fds ? ce.estiloItensCentralizadoVerde : ce.estiloItensCentralizado);
					p1.celula++;
				}
			}

	        if ((p1.dadosRelatorio.get(r).getTempoMedio() != null && p1.dadosRelatorio.get(r).getTempoMedio() > 0) && p1.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
	        	p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getTempoMedio());
	        } 
        	p1.linhaSheet.getCell(p1.celula).setCellStyle(p1.dadosRelatorio.get(r).getDia() == 99 ? ce.estiloItensNumCentralizadoCinza : (fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado));
        	p1.celula++;
    		
        	
        	if ((p1.dadosRelatorio.get(r).getIndiceMobilidade() != null && p1.dadosRelatorio.get(r).getIndiceMobilidade() > 0) && 
        			(p1.dadosRelatorio.get(r).getTempoMedio() != null && p1.dadosRelatorio.get(r).getTempoMedio() > 0) && p1.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
		   		p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getIndiceMobilidade());
		   	} 
		   	p1.linhaSheet.getCell(p1.celula).setCellStyle(p1.dadosRelatorio.get(r).getDia() == 99 ? ce.estiloItensIMCinza : (fds ? ce.estiloItensIMVerde : ce.estiloItensIM));
		   	p1.celula++;
    		
		   	
		   	if ((p1.dadosRelatorio.get(r).getVelocidadePercurso() != null && p1.dadosRelatorio.get(r).getVelocidadePercurso() > 0) &&
		   			(p1.dadosRelatorio.get(r).getTempoMedio() != null && p1.dadosRelatorio.get(r).getTempoMedio() > 0) && p1.dadosRelatorio.get(r).getVolumeVeicular() > 0 ) {
    			p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getVelocidadePercurso());
    		} 
    		p1.linhaSheet.getCell(p1.celula).setCellStyle(p1.dadosRelatorio.get(r).getDia() == 99 ? ce.estiloItensNumCentralizadoCinza : (fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado));
    		p1.celula++;
    		
    		
    		if (p1.dadosRelatorio.get(r).getVolumeVeicular() != null && p1.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
    			p1.linhaSheet.getCell(p1.celula).setCellValue(p1.dadosRelatorio.get(r).getVolumeVeicular());
    		} 
    		p1.linhaSheet.getCell(p1.celula).setCellStyle(p1.dadosRelatorio.get(r).getDia() == 99 ? ce.estiloItensNumCentralizadoCinza : (fds ? ce.estiloItensNumCentralizadoVerde : ce.estiloItensNumCentralizado));
    		p1.celula++;
		}
	}

	/**
	 * Validar se o valor digitado é um numero
	 * 
	 * @author Thiago Surgik - Consilux Tecnologia Data: 27/09/2016
	 */
	boolean isDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch < 48 || ch > 57)
				return false;
		}
		return true;
	}

}
