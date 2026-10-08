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
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.model.ferramenta.Percurso;
import com.consilux.model.relatorio.rj.DadosTempoPercurso;
import com.consilux.model.relatorio.rj.ItemTempoPercurso;

/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 06 - Tempo de percurso, fluxo veicular, mobilidade e velocidade de corredores em dias úteis por mês. 
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 23/11/2016
 */
public class Relatorio6TempoPercurso extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(Relatorio6TempoPercurso.class);

	private class Param {
		public ArrayList<Percurso> percursos;
		public Integer celula;
		public Integer celulaInicio;
		public Integer celulaFim;
		public Sheet sheet;
		public Row cabecalho0;
		public Row cabecalho1;
		public Row cabecalho2;
		public Row cabecalho3;
		public ArrayList<ItemTempoPercurso> dadosRelatorio;
		public Row linhaSheet = null;
		public Integer intIdCorredorAnterior;
		public String strCorredorAnterior = null;
		public Integer linha_ref = 0;
		public Integer intMes = 0;
		public Integer intAno = 0;
		public boolean colunaHorario = true;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer idUsuario = null;
		String usuarioURL = request.getParameter("sistema");
		if(usuarioURL == null){
			final Acesso acessoUsuario = new Acesso(request, response, true);
			if (!acessoUsuario.verificaAcesso()){return;}else{
				usuarioURL = acessoUsuario.getUsuario().getNome();
				idUsuario = acessoUsuario.getUsuario().getId();
			}
		}else
			usuarioURL = "SISTEMA";
		
        Date data = new Date();  
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        String userGerador = "Relatório gerado em: " + formatador.format(data) + ", pelo usuário: " + usuarioURL;
		
		String strMes = request.getParameter("mes");
		String strAno = request.getParameter("ano");
		
		SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
		Date dtData = null;
		
		String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
		
		Integer intMes = null, intAno = null;

		//Validações Necessárias para montar relatório
		if((strMes.equals("")) || (strAno.equals(""))){
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
			
			if(intMes < 1 || intMes > 12){
				new Mensagem(response).showErro("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
				return; 
			}
			if(intAno < 1900 || intAno > 2050){
				new Mensagem(response).showErro("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
				return; 
			}
			
			dtData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/" + (strMes.trim().length() == 1 ? "0"+strMes.trim() : strMes.trim()) +"/"+ strAno + " 00:00:00");
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
			
			// Criando o arquivo fisico
	        String nomeArquivo = numeroContrato + "-" + anoContrato.substring(2,4) + "_tempo.06-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
	        
	        response.setContentType(TipoMime.XLSX.getTipo());
			response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");		
 
	        // Criando area de trabalho para o excel
			SXSSFWorkbook wb = new SXSSFWorkbook();
			wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
	        
		    //Cria uma planilha Excel
	        Sheet sheet = wb.createSheet("6-Consilux-Tempo-" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString()); 
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
            
	        //Cria uma linha na Planilha.
            Row cabecalhoContrato = sheet.createRow(0);
            cabecalhoContrato.getCell(0).setCellValue("TEMPO DE PERCURSO, FLUXO VEICULAR, MOBILIDADE E VELOCIDADE DE CORREDORES EM DIAS ÚTEIS POR MÊS");
            cabecalhoContrato.getCell(0).setCellStyle(estilo);
            cabecalhoContrato.getCell(1).setCellValue("");
            cabecalhoContrato.getCell(1).setCellStyle(estilo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            
            GerarRelatorio(intMes, intAno, response, sheet, wb, userGerador, nomeArquivo, idUsuario, data, usuarioURL);

			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public void GerarRelatorio(Integer intMes, Integer intAno,
							   HttpServletResponse response, Sheet sheet, SXSSFWorkbook wb, String userGerador,
					 		   String nomeArquivo, Integer idUsuario, Date data, String usuarioURL
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        ColecaoEstilos ce = new ColecaoEstilos(wb);
        
        Param p = new Param();
	    p.percursos = null;
	    p.celula = 0;
	    p.celulaInicio = 0;
	    p.celulaFim = 0;
	    p.intIdCorredorAnterior = null;
	    p.strCorredorAnterior = null;
	    p.sheet = sheet;
	    p.intMes = intMes;
	    p.intAno = intAno;
        
		//Criando as colunas do corpo
        p.cabecalho0 = p.sheet.createRow((short)2);
        p.cabecalho1 = p.sheet.createRow((short)3);
        p.cabecalho2 = p.sheet.createRow((short)4);
        p.cabecalho3 = p.sheet.createRow((short)5);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat formatoMesAno = new SimpleDateFormat("MM/yyyy");
		
	    p.cabecalho0.getCell(0).setCellValue("Dados por pista-sentido");
	    p.cabecalho0.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		
	    p.cabecalho1.getCell(0).setCellValue("MÊS/ANO: " + formatoMesAno.format(sdf.parse(String.format("%04d", p.intAno) + "-" + String.format("%02d", p.intMes) + "-01")));
	    p.cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
	    p.cabecalho2.getCell(0).setCellValue("Nome do Corredor");
	    p.cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    p.cabecalho3.getCell(0).setCellValue("Horário");
	    p.cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
	    p.sheet.setColumnWidth(0, 4500);
	    p.sheet.getRow(p.cabecalho2.getRowNum()).setHeight((short) 800);
	    p.sheet.getRow(p.cabecalho3.getRowNum()).setHeight((short) 600);
	    

	    Percurso dadosPercurso = new Percurso();
	    p.percursos = dadosPercurso.obterCorredores();
	    
	    p.celula = 1;
	    p.celulaInicio = p.celula;
	    p.celulaFim = p.celula;
	    p.intIdCorredorAnterior = null;
	    
	    //Por pista-sentido
	    GerarCabecalhos(p, ce, false);
	    
	    DadosTempoPercurso dadosTempoPercurso = new DadosTempoPercurso();
        
	    p.celulaInicio = 0;
	    p.celulaFim = 0;
	    
	    p.linhaSheet = null;
	    p.colunaHorario = true;
	    p.linha_ref = p.cabecalho3.getRowNum() + 1;
	    
	    for (int i = 0; i < p.percursos.size(); i++) {
	    	
	    	//Buscando informações para popular planilhas
	    	p.dadosRelatorio = dadosTempoPercurso.relatorio6TempoPercurso(p.intMes, p.intAno, p.percursos.get(i).getIdPercurso());

	    	p.celulaInicio = p.celulaFim;
	    
	    	GerarDados(p, ce);
         	
         	p.colunaHorario = false;
        	p.celulaFim = p.celula;
	    }

	    
	    int qtdeLinhas = p.sheet.getLastRowNum()+3;
	    
		//Criando as colunas do corpo
	    p.cabecalho0 = p.sheet.createRow((short)qtdeLinhas++);
        p.cabecalho1 = p.sheet.createRow((short)qtdeLinhas++);
        p.cabecalho2 = p.sheet.createRow((short)qtdeLinhas++);
        p.cabecalho3 = p.sheet.createRow((short)qtdeLinhas++);
		
        p.cabecalho0.getCell(0).setCellValue("Dados por pista-sentido-faixa");
        p.cabecalho0.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
		
        p.cabecalho1.getCell(0).setCellValue("MÊS/ANO: " + formatoMesAno.format(sdf.parse(String.format("%04d", p.intAno) + "-" + String.format("%02d", p.intMes) + "-01")));
        p.cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
        p.cabecalho2.getCell(0).setCellValue("Nome do Corredor");
        p.cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
        p.cabecalho3.getCell(0).setCellValue("Horário");
        p.cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    
        p.sheet.setColumnWidth(0, 4500);
        p.sheet.getRow(p.cabecalho2.getRowNum()).setHeight((short) 800);
        p.sheet.getRow(p.cabecalho3.getRowNum()).setHeight((short) 600);
	    

        p.percursos = dadosPercurso.obterCorredoresPorFaixa();
	    
        p.celula = 1;
        p.celulaInicio = p.celula;
        p.celulaFim = p.celula;
        p.strCorredorAnterior = null;
	    
	    //Por pista-sentido-faixa
	    GerarCabecalhos(p, ce, true);
	    
	    dadosTempoPercurso = null;
	    dadosTempoPercurso = new DadosTempoPercurso();

	    p.celulaInicio = 0;
	    p.celulaFim = 0;
	    
	    p.colunaHorario = true;
	    
	    p.linha_ref = p.sheet.getLastRowNum() + 1;
	    
	    for (int i = 0; i < p.percursos.size(); i++) {
	    	
	    	//Buscando informações para popular planilhas
	    	p.dadosRelatorio = dadosTempoPercurso.relatorio6TempoPercursoFaixa(p.intMes, p.intAno, p.percursos.get(i).getIdPercurso(), p.percursos.get(i).getFaixa());

	    	p.celulaInicio = p.celulaFim;
	    
         	GerarDados(p, ce);
         	
         	p.colunaHorario = false;
         	p.celulaFim = p.celula;
	    }
        
	    p.sheet = wb.getSheetAt(0);

        // Salvando o arquivo
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
	}

	private void GerarCabecalhos (Param p, ColecaoEstilos ce, boolean faixa) { 
		
	    try {
		    
		    for (int i = 0; i < p.percursos.size(); i++) {
		    	
		    	if (faixa) {
		    		if ( (p.strCorredorAnterior != null) && (!p.strCorredorAnterior.equals(p.percursos.get(i).getNomePercurso())) ) {
		    			p.sheet.addMergedRegion(new CellRangeAddress(p.cabecalho2.getRowNum(), p.cabecalho2.getRowNum(), p.celulaInicio, p.celulaFim));
		    		}
		    	} else {
			    	if ( (p.intIdCorredorAnterior != null) && (!p.intIdCorredorAnterior.equals(p.percursos.get(i).getIdPercurso())) ) {
			    		p.sheet.addMergedRegion(new CellRangeAddress(p.cabecalho2.getRowNum(), p.cabecalho2.getRowNum(), p.celulaInicio, p.celulaFim));
		    		}
		    	}
		    	
		    	p.celulaInicio = p.celula;
		    	p.cabecalho1.getCell(p.celula).setCellValue("");
		    	p.cabecalho1.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    	p.cabecalho2.getCell(p.celula).setCellValue(p.percursos.get(i).getNomePercurso().trim());
		    	p.cabecalho2.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    	p.cabecalho3.getCell(p.celula).setCellValue("Tempo médio (s)");
		    	p.cabecalho3.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    	p.sheet.setColumnWidth(p.celula, 3500);
		    	p.celulaFim = p.celula;
		    	p.celula++;
				p.cabecalho1.getCell(p.celula).setCellValue("");
			    p.cabecalho1.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			    p.cabecalho2.getCell(p.celula).setCellValue(p.percursos.get(i).getNomePercurso().trim());
				p.cabecalho2.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
				p.cabecalho3.getCell(p.celula).setCellValue("Indice de Mobilidade (IM)");
			    p.cabecalho3.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			    p.sheet.setColumnWidth(p.celula, 3500);
			    p.celulaFim = p.celula;
				p.celula++;
				p.cabecalho1.getCell(p.celula).setCellValue("");
			    p.cabecalho1.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			    p.cabecalho2.getCell(p.celula).setCellValue(p.percursos.get(i).getNomePercurso().trim());
				p.cabecalho2.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
				p.cabecalho3.getCell(p.celula).setCellValue("Velocidade de Percurso");
			    p.cabecalho3.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			    p.sheet.setColumnWidth(p.celula, 3500);
			    p.celulaFim = p.celula;
				p.celula++;
				p.cabecalho1.getCell(p.celula).setCellValue("");
			    p.cabecalho1.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpo3);
			    p.cabecalho2.getCell(p.celula).setCellValue(p.percursos.get(i).getNomePercurso().trim());
				p.cabecalho2.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
				p.cabecalho3.getCell(p.celula).setCellValue("Volume Veicular");
			    p.cabecalho3.getCell(p.celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
			    p.sheet.setColumnWidth(p.celula, 3500);
			    p.celulaFim = p.celula;
				p.celula++;
			    
			    if (faixa) {
					p.strCorredorAnterior = p.percursos.get(i).getNomePercurso();
				} else {
					p.intIdCorredorAnterior = p.percursos.get(i).getIdPercurso();
				}
		    }
		    
		    p.cabecalho1.getCell(p.celula - 1).setCellStyle(ce.estiloCabecalhoCorpo2);
		    
		    p.sheet.addMergedRegion(new CellRangeAddress(p.cabecalho2.getRowNum(), p.cabecalho2.getRowNum(), p.celulaInicio, p.celulaFim));
		} 
	    catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	
	private void GerarDados(Param p, ColecaoEstilos ce) {
		
     	for (int r = 0; r < p.dadosRelatorio.size(); r++) {
        	
     		p.celula = p.celulaInicio;
    		
	        if (p.sheet.getRow((short)(r+p.linha_ref)) == null) {
	        	p.linhaSheet = p.sheet.createRow((short)(r+p.linha_ref));
	        } else {
	        	p.linhaSheet = p.sheet.getRow((short)(r+p.linha_ref));
	        }
	        
	        if (p.colunaHorario) {
	        	p.linhaSheet.getCell(p.celula).setCellValue(p.dadosRelatorio.get(r).getHoraDesc());
	        	p.linhaSheet.getCell(p.celula).setCellStyle(p.dadosRelatorio.get(r).getHora() == 99 ? ce.estiloItensCentralizadoCinza : ce.estiloItensCentralizado);
	        	p.celula++;
	        }
	        
    		
	        if ((p.dadosRelatorio.get(r).getTempoMedio() != null && p.dadosRelatorio.get(r).getTempoMedio() > 0) && p.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
	        	p.linhaSheet.getCell(p.celula).setCellValue(p.dadosRelatorio.get(r).getTempoMedio());
	        } 
        	p.linhaSheet.getCell(p.celula).setCellStyle(p.dadosRelatorio.get(r).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : ce.estiloItensNumCentralizado);
        	p.celula++;
    		
        	
        	if ((p.dadosRelatorio.get(r).getIndiceMobilidade() != null && p.dadosRelatorio.get(r).getIndiceMobilidade() > 0) && 
        			(p.dadosRelatorio.get(r).getTempoMedio() != null && p.dadosRelatorio.get(r).getTempoMedio() > 0) && p.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
		   		p.linhaSheet.getCell(p.celula).setCellValue(p.dadosRelatorio.get(r).getIndiceMobilidade());
		   	} 
		   	p.linhaSheet.getCell(p.celula).setCellStyle(p.dadosRelatorio.get(r).getHora() == 99 ? ce.estiloItensIMCinza : ce.estiloItensIM);
		   	p.celula++;
    		
		   	
		   	if ((p.dadosRelatorio.get(r).getVelocidadePercurso() != null && p.dadosRelatorio.get(r).getVelocidadePercurso() > 0) &&
		   			(p.dadosRelatorio.get(r).getTempoMedio() != null && p.dadosRelatorio.get(r).getTempoMedio() > 0) && p.dadosRelatorio.get(r).getVolumeVeicular() > 0 ) {
    			p.linhaSheet.getCell(p.celula).setCellValue(p.dadosRelatorio.get(r).getVelocidadePercurso());
    		} 
    		p.linhaSheet.getCell(p.celula).setCellStyle(p.dadosRelatorio.get(r).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : ce.estiloItensNumCentralizado);
    		p.celula++;
    		
    		
    		if (p.dadosRelatorio.get(r).getVolumeVeicular() != null && p.dadosRelatorio.get(r).getVolumeVeicular() > 0) {
    			p.linhaSheet.getCell(p.celula).setCellValue(p.dadosRelatorio.get(r).getVolumeVeicular());
    		} 
    		p.linhaSheet.getCell(p.celula).setCellStyle(p.dadosRelatorio.get(r).getHora() == 99 ? ce.estiloItensNumCentralizadoCinza : ce.estiloItensNumCentralizado);
    		p.celula++;
    	}

	}

	/**
	 * Validar se o valor digitado é um numero
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 27/09/2016
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
