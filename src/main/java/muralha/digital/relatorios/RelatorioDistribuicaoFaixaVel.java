package muralha.digital.relatorios;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.exception.ModelException;
import com.consilux.servlet.relatorio.rj.ColecaoEstilos;

/**
 * Servlet para a geração de relatório de Fiscalização Eletrônica - Relatório 09 - Cada endereço diário por hora - perfil de velocidade.
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 06/10/2016
 */
@WebServlet("/Relatorio/DistribuicaoFaixaVelocidade")
public class RelatorioDistribuicaoFaixaVel extends HttpServlet {

	private static final long serialVersionUID = -419706786492469948L;
	private static final Logger logger = Logger.getLogger(RelatorioDistribuicaoFaixaVel.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		@SuppressWarnings("unused")
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
		String strIdLocal = request.getParameter("equipamento").equals("0") ? "" : request.getParameter("equipamento");
		
		Integer intMes = null, intAno = null, intIdLocal = null;
		
		Date dtData = null;
		
		
		//Validações Necessárias para montar relatório
		if((strMes.equals("")) || (strAno.equals(""))){
			new Mensagem(response).showErroMuralha("O Mês e o Ano devem ser informados!", "javascript:window.close();");
			return;
		}
		
		if (!isDigit(strMes) || !isDigit(strAno)) {
			new Mensagem(response).showErroMuralha("Favor informar os dados corretos de mês e ano! Somente números são válidos.", "javascript:window.close();");
			return; 
		}
			
		try {

			intMes = Integer.parseInt(strMes);  
			intAno = Integer.parseInt(strAno);
			
			if(intMes < 1 || intMes > 12){
				new Mensagem(response).showErroMuralha("Favor informar um valor de mês válido. Entre 1 e 12!", "javascript:window.close();");
				return; 
			}
			if(intAno < 1900 || intAno > 2050){
				new Mensagem(response).showErroMuralha("Favor informar um valor de ano válido. Maior que 1900 e menor que 2050!", "javascript:window.close();");
				return; 
			}
			
			dtData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("01/" + (strMes.trim().length() == 1 ? "0"+strMes.trim() : strMes.trim()) +"/"+ strAno + " 00:00:00");
			
			if(!(strIdLocal.equals(""))){
				if (!ExpValida.NATURAL.validar(strIdLocal)) {
					new Mensagem(response).showErroMuralha("Identificador de Local enviado inválido!", "javascript:window.close();");
					return;
				} else {
					intIdLocal = Integer.parseInt(strIdLocal);
				}
			}
		} 
		catch (Exception e) {
			logger.error("Erro ao preparar dados para o relatório.", e);
			return;
		}
		
		try {
 	        
			if (intIdLocal != null && intIdLocal > 0) {
				CriarArquivoExcel(intMes, intAno, response, userGerador, dtData, intIdLocal);
			} else {
				CriarArquivoZip(intMes, intAno, response, userGerador, dtData, intIdLocal);
			}
 	        
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	public void CriarArquivoZip(Integer intMes, Integer intAno, HttpServletResponse response, String userGerador, Date dtData, Integer intIdLocal
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
			//Buscar os locais para geração do relatório
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> locais = localVigente.buscaListaLocalVigenteRelFluxoRJ(intIdLocal);
            
 			if (locais.isEmpty()) {
				new Mensagem(response).showErroMuralha("Não existem dados para geração do relatório!", "javascript:window.close();");
				return;
			}
 			
 			
			SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
			
			String nomeArquivoZip = "TABELA DE DISTRIBUIÇÃO POR FAIXA DE VELOCIDADE_" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".zip";
			
			Calendar primeiroDiaMes = Calendar.getInstance();
			primeiroDiaMes.setTime(dtData);
			Integer intUltimoDiaMes = primeiroDiaMes.getActualMaximum(Calendar.DATE);
			
			ZipOutputStream arquivoZIP = criaArquivoSaidaZip(nomeArquivoZip, response);
			
 	        LocalVigente localRelat = new LocalVigente();
 	        for (Integer i = 0; i < locais.size(); i++) {
 	        	
 	        	localRelat = locais.get(i);
 	        	
 	        	ByteArrayOutputStream arquivoExcel = new ByteArrayOutputStream();
 	        	
// 	        	String strNomeLocal = Funcoes.removeAcentos(localRelat.getSerieEquipamento().toString().trim()) + "_" + String.format("%03d", localRelat.getIdLocal());
 	        	String nomeArquivoExcel = "TABELA DE DISTRIBUIÇÃO POR FAIXA DE VELOCIDADE_" + localRelat.getSerieEquipamento().toString() + "_" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
 	        	
 	        	//Criando area de trabalho para o excel
 				SXSSFWorkbook wb = new SXSSFWorkbook();
 		        wb.setCompressTempFiles(true);
 		        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
 	        	
 		        for (Integer intDia = 1; intDia <= intUltimoDiaMes; intDia++) {
 		        	
 		        	SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
 		            String strData = String.format("%02d", intDia) + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno);
 		        	Date dtDia = dataFormat.parse(strData);
 		        	
 		        	CriarPlanilha(intDia, intMes, intAno, dtDia, response, wb, userGerador, localRelat);
 		        	
 		        }
 		        
 		        wb.write(arquivoExcel);
 		        
 		        byte[] conteudo = arquivoExcel.toByteArray();
 		        
 		        adicionarArquivoNoZip(arquivoZIP, nomeArquivoExcel, conteudo);
 		        
 	 	        {
 		 	        wb.dispose();
 		 	        wb = null;
 		        }
 	        }
 	        
 	        arquivoZIP.close();
			
 	        {
	 	        localVigente = null;
	 	        locais = null;
	 	        response = null;
	 	        arquivoZIP = null;
 	        }

 	        return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	public void CriarArquivoExcel(Integer intMes, Integer intAno, HttpServletResponse response, String userGerador, Date dtData, Integer intIdLocal
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
			//Buscar os locais para geração do relatório
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> locais = localVigente.buscaListaLocalVigenteRelFluxoRJ(intIdLocal);
            
 			if (locais.isEmpty()) {
				new Mensagem(response).showErroMuralha("Não existem dados para geração do relatório!", "javascript:window.close();");
				return;
			}
 			
 			
			SimpleDateFormat mesExtenso = new SimpleDateFormat("MMMM");
			Calendar primeiroDiaMes = Calendar.getInstance();
			primeiroDiaMes.setTime(dtData);
			Integer intUltimoDiaMes = primeiroDiaMes.getActualMaximum(Calendar.DATE);
			
 	        LocalVigente localRelat = new LocalVigente();
 	        for (Integer i = 0; i < locais.size(); i++) {
 	        	
 	        	localRelat = locais.get(i);
 	        	
// 	        	String strNomeLocal = Funcoes.removeAcentos(localRelat.getNome().trim()) + "_" + String.format("%03d", localRelat.getIdLocal());
 	        	String nomeArquivoExcel = "TABELA DE DISTRIBUIÇÃO POR FAIXA DE VELOCIDADE_" + localRelat.getSerieEquipamento().toString() + "_" + mesExtenso.format(dtData).substring(0,3).toLowerCase() + intAno.toString() + ".xlsx";
 	        	
 	        	response.setContentType(TipoMime.XLSX.getTipo());
 				response.setHeader("Content-disposition", "attachment; filename=\"" + nomeArquivoExcel + "\"");
 				
 				//Criando area de trabalho para o excel
 				SXSSFWorkbook wb = new SXSSFWorkbook();
 		        wb.setCompressTempFiles(true);
 		        wb.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
 	        	
 		        for (Integer intDia = 1; intDia <= intUltimoDiaMes; intDia++) {
 		        	
 		        	SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
 		            String strData = String.format("%02d", intDia) + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno);
 		        	Date dtDia = dataFormat.parse(strData);
 		        	
 		        	CriarPlanilha(intDia, intMes, intAno, dtDia, response, wb, userGerador, localRelat);
 		        	
 		        }
 		        
 	 	        {
 		 	        // Salvando o arquivo
 		 	        ServletOutputStream out = response.getOutputStream();
 		 	        wb.write(out);
 		 	        out.flush();
 		 	        out.close();
 		 	        wb.dispose();
 		 	        
 		 	        wb = null;
 		 	        out = null;
 		        }
 	        }
 	        
 	        localVigente = null;
 	        locais = null;
 	        response = null;

 	        return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}
	
	public void CriarPlanilha(Integer intDia, Integer intMes, Integer intAno, Date dtDia, HttpServletResponse response, SXSSFWorkbook wb,
							  String userGerador, LocalVigente localRelat
			) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
		
		try {
			
	        //Cria uma planilha Excel
            String strNomePlanilha = null;
			strNomePlanilha = "Dia " + String.format("%02d", intDia);

		    Sheet sheet = wb.createSheet(strNomePlanilha);  
            sheet.setVerticallyCenter(true);
            sheet.setHorizontallyCenter(true);
            
            String numeroContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("numero_contrato");
    		String anoContrato = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("ano_contrato");
    		String contrato = String.format("%03d", Integer.parseInt(numeroContrato)) + "/" + anoContrato;
    		
						
			//Estilos
			ColecaoEstilos ce = new ColecaoEstilos(wb);
						
			//Nome relatório
			Row cabecalhoTituloRelatorio2 = sheet.createRow(0);
			cabecalhoTituloRelatorio2.getCell(0).setCellValue("TABELA DE DISTRIBUIÇÃO POR FAIXA DE VELOCIDADE");
			cabecalhoTituloRelatorio2.getCell(0).setCellStyle(ce.estiloCabecalhoTituloRelatorio);
			sheet.addMergedRegion(new CellRangeAddress(cabecalhoTituloRelatorio2.getRowNum(), cabecalhoTituloRelatorio2.getRowNum(), 0, 13));

            //Filtros informados
            Row cabecalhoContrato = sheet.createRow(1);
            cabecalhoContrato.getCell(0).setCellValue("Nº DO CONTRATO:");
            cabecalhoContrato.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoContrato.getCell(1).setCellValue(contrato);
            cabecalhoContrato.getCell(1).setCellStyle(ce.estiloFiltro);
            
            Row cabecalhoLogradouro = sheet.createRow(2);
            cabecalhoLogradouro.getCell(0).setCellValue("LOCAL:");
            cabecalhoLogradouro.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoLogradouro.getCell(1).setCellValue(localRelat.getNome().trim());
            cabecalhoLogradouro.getCell(1).setCellStyle(ce.estiloFiltro);
            
            Row cabecalhoReferenciaNumEquip = sheet.createRow(3);
            cabecalhoReferenciaNumEquip.getCell(0).setCellValue("EQUIPAMENTO:");
            cabecalhoReferenciaNumEquip.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoReferenciaNumEquip.getCell(1).setCellValue(localRelat.getSerieEquipamento());
            cabecalhoReferenciaNumEquip.getCell(1).setCellStyle(ce.estiloFiltro);
            
            Row cabecalhoPistaSentido = sheet.createRow(4);
            cabecalhoPistaSentido.getCell(0).setCellValue("COORDENADA:");
            cabecalhoPistaSentido.getCell(0).setCellStyle(ce.estiloCabecalhoFiltro);
            cabecalhoPistaSentido.getCell(1).setCellValue(Funcoes.converterDecimalParaDMS(localRelat.getLatitude(), localRelat.getLongitude()));
            cabecalhoPistaSentido.getCell(1).setCellStyle(ce.estiloFiltro);
                        
            int intLinha = 6;
            
            intLinha = GerarRelatorio(intDia, intMes, intAno, intLinha, response, sheet, wb, userGerador, localRelat, null, ce);
			
            //Buscar as faixas para geração dos tabelas específicas
 			LocalVigente localVigente = new LocalVigente();
 			ArrayList<LocalVigente> faixas = localVigente.buscaListaLocalVigenteFaixaRelFluxoRJ(localRelat.getIdLocal());
             
 	        LocalVigente faixaRelat = new LocalVigente();
 	        for (Integer i = 0; i < faixas.size(); i++) {
 	        	
 	        	faixaRelat = faixas.get(i);
 	        	intLinha = GerarRelatorio(intDia, intMes, intAno, intLinha, response, sheet, wb, userGerador, localRelat, faixaRelat, ce);
 	        	
 	        }
            
			return; 
		} 
		catch (Exception e) {
			logger.error("Erro ao gerar o relatório em Excel.", e);
			new ServletException("Erro ao gerar o relatório: " + e.getMessage());
		}
	}

	
	public int GerarRelatorio(Integer intDia, Integer intMes, Integer intAno, int intLinha, HttpServletResponse response,
			   				  Sheet sheet, SXSSFWorkbook wb, String userGerador, LocalVigente localRelat, LocalVigente faixaRelat,
			   				  ColecaoEstilos ce
		) throws IOException, ConexaoException, SQLException, ModelException, ParseException{
	
        //Criando as colunas do corpo
        Row cabecalho1 = null;
    	cabecalho1 = sheet.createRow((short)intLinha);
    	cabecalho1.createCell(0).setCellValue((faixaRelat != null ? "Faixa " + faixaRelat.getFaixa().toString() : "Total das faixas"));
	    cabecalho1.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoEsquerdaSemBorda);
    	intLinha = (intLinha + 1);
        
		Row cabecalho2 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho3 = sheet.createRow((short)intLinha);
        intLinha++;
		Row cabecalho4 = sheet.createRow((short)intLinha);
        intLinha++;
		
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strData = String.format("%02d", intDia) + "/" + String.format("%02d", intMes) + "/" + String.format("%04d", intAno);
    	Date dtDia = dataFormat.parse(strData);
        
		cabecalho2.getCell(0).setCellValue(dataFormat.format(dtDia));
	    cabecalho2.getCell(0).setCellStyle(ce.estiloCabecalhoCorpo1);
		cabecalho3.getCell(0).setCellValue("VELOCIDADE");
	    cabecalho3.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(0).setCellValue("Horário (h)");
	    cabecalho4.getCell(0).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(0, 4600);
	    
	    cabecalho2.getCell(1).setCellValue("");
	    cabecalho2.getCell(1).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(1).setCellValue("Até 9 km/h");
	    cabecalho3.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(1).setCellValue("N. Veic.");
	    cabecalho4.getCell(1).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(1, 2800);
	    
	    
	    Integer celula = 2, velocidade_ini = 1, velocidade_fim = 9;
	    
	    for (int i = 2; i < 17; i++) {
	    	
	    	velocidade_ini = velocidade_ini + 10;
	    	velocidade_fim = velocidade_fim + 10;
	    	
	    	cabecalho2.getCell(celula).setCellValue("");
		    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
		    cabecalho3.getCell(celula).setCellValue(velocidade_ini.toString() + " a " + velocidade_fim.toString() + " km/h");
		    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    cabecalho4.getCell(celula).setCellValue("N. Veic.");
		    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
		    sheet.setColumnWidth(celula, celula < 20 ? 3480 : 3800);
		    celula++;
	    }
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo3);
	    cabecalho3.getCell(celula).setCellValue("Acima de 160 km/h");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("N. Veic.");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4200);
	    celula++;
	    
	    cabecalho2.getCell(celula).setCellValue("");
	    cabecalho2.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpo2);
	    cabecalho3.getCell(celula).setCellValue("Soma");
	    cabecalho3.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    cabecalho4.getCell(celula).setCellValue("N. Veic.");
	    cabecalho4.getCell(celula).setCellStyle(ce.estiloCabecalhoCorpoNegrito);
	    sheet.setColumnWidth(celula, 4200);
	    
        
	    Integer intIdPista = faixaRelat != null ? faixaRelat.getIdPista() : null;
	    
        //Buscando informações para popular planilhas
	    DadosRelatorios dadosRelatorios = new DadosRelatorios();
	    ArrayList<ItemRelatorios> dadosRelatorio = dadosRelatorios.RelDistribuicaoFaixaVelocidade(dtDia, localRelat.getIdLocal(), intIdPista);
	    
	    int intLinhaInicioDados = intLinha, intLinhaRetorno = intLinhaInicioDados;
        
        if (dadosRelatorio.size() == 0) {
        	Row linhaSemInfo = sheet.createRow((short)intLinha);
        	linhaSemInfo.getCell(0).setCellValue("Não há dados para os filtros informados!");
        } else {
        	for (int i = 0; i < dadosRelatorio.size(); i++) {
            	
            	Row linhaSheet = sheet.createRow((short)(i+intLinha));
            	linhaSheet.getCell(0).setCellValue(dadosRelatorio.get(i).getHoraDesc());
            	linhaSheet.getCell(0).setCellStyle(ce.estiloItensCentralizado);

            	Integer valor;
            	celula = 1;

            	for(int h = 0; h < 18; h++){
            		valor = dadosRelatorio.get(i).getCelulasInteger()[h];
        		   
            		if (valor != null && valor > 0) {
            			linhaSheet.getCell(celula).setCellValue(valor);
            		} 
            		linhaSheet.getCell(celula).setCellStyle(ce.estiloItensNumCentralizado);
            		celula++;
            	}
            }
        	
        	int intLinhaResumo = 0, intLinhaInicioSomaResumo = (intLinhaInicioDados + 1), intLinhaAcumulado = 0, intLinhaPorcAcumulada = 0;
        	
        	if (dadosRelatorio.size() > 0) {
        		
        		intLinhaResumo = dadosRelatorio.size() + (intLinha);
        		intLinhaAcumulado = intLinhaResumo + 1;
        		intLinhaPorcAcumulada = intLinhaAcumulado + 1;
        		intLinhaRetorno = intLinhaPorcAcumulada + 1;
        		
        		Row linhaResumo = sheet.createRow((short)(intLinhaResumo));
        		linhaResumo.getCell(0).setCellValue("Nº DE VEÍCULOS");
        		linhaResumo.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
        		
        		Row linhaAcumulado = sheet.createRow((short)(intLinhaAcumulado));
        		linhaAcumulado.getCell(0).setCellValue("ACUMULADO");
        		linhaAcumulado.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
        		
        		Row linhaPorcAcumulada = sheet.createRow((short)(intLinhaPorcAcumulada));
        		linhaPorcAcumulada.getCell(0).setCellValue("% ACUMULADA");
        		linhaPorcAcumulada.getCell(0).setCellStyle(ce.estiloItensCentralizadoCinza);
        		
        		celula = 1;
        		
        		String letraCelulaFluxo = null, letraCelulaFluxoAcumulado = null, letraCelulaFluxoAcumuladoTotal = null;
        		
        		for(int h = 0; h < 18; h++){

        			//FLUXO VEICULAR
        			letraCelulaFluxo = CellReference.convertNumToColString(celula);
        			linhaResumo.getCell(celula).setCellFormula("SUM("+letraCelulaFluxo+String.valueOf(intLinhaInicioSomaResumo)+":"+letraCelulaFluxo+String.valueOf(linhaResumo.getRowNum())+")");
        			linhaResumo.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
        			
        			if (h == 0) {
        				letraCelulaFluxo = CellReference.convertNumToColString(celula);
        				linhaAcumulado.getCell(celula).setCellFormula("=("+letraCelulaFluxo+String.valueOf((linhaResumo.getRowNum() + 1))+")");
        				linhaAcumulado.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
        			} else if (h == 17) {
        				letraCelulaFluxo = CellReference.convertNumToColString(celula);
        				linhaAcumulado.getCell(celula).setCellFormula("=("+letraCelulaFluxo+String.valueOf((linhaResumo.getRowNum() + 1))+")");
        				linhaAcumulado.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
        			} else {
        				letraCelulaFluxoAcumulado = CellReference.convertNumToColString(celula-1);
        				letraCelulaFluxo = CellReference.convertNumToColString(celula);
        				linhaAcumulado.getCell(celula).setCellFormula("=("+letraCelulaFluxoAcumulado+String.valueOf((linhaAcumulado.getRowNum() + 1))+" + "+letraCelulaFluxo+String.valueOf((linhaResumo.getRowNum() + 1))+")");
        				linhaAcumulado.getCell(celula).setCellStyle(ce.estiloItensNumCentralizadoCinza);
        			}
        			
        			letraCelulaFluxoAcumuladoTotal = CellReference.convertNumToColString(18);
    				linhaPorcAcumulada.getCell(celula).setCellFormula("=("+letraCelulaFluxo+String.valueOf((linhaAcumulado.getRowNum() + 1))+" / "+letraCelulaFluxoAcumuladoTotal+String.valueOf((linhaAcumulado.getRowNum() + 1))+")");
    				linhaPorcAcumulada.getCell(celula).setCellStyle(ce.estiloPorcentagemCinza);

        			celula++;
        		}
        	}
        }
        
        sheet = wb.getSheetAt(0);
		
		return intLinhaRetorno + 1;

	}
	
	/**
	 * Criar arquivo de saída .zip
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 15/05/2018
	 */
	private ZipOutputStream criaArquivoSaidaZip(String nomeArquivoZip, HttpServletResponse response) throws ServletException, IOException {
		
		ZipOutputStream arquivoZip;

		response.setContentType(TipoMime.ZIP.getTipo());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivoZip + "\"");
		response.setHeader("Refresh", "300");
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setDateHeader("Expires", 0); //prevents caching at the proxy server

		arquivoZip = new ZipOutputStream(response.getOutputStream());

		arquivoZip.setMethod(ZipOutputStream.DEFLATED);
		arquivoZip.setLevel(0);

		return arquivoZip;
	}

	
	/**
	 * Adicionar arquivo excel no arquivo .zip
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 15/05/2018
	 */
	private void adicionarArquivoNoZip(ZipOutputStream arquivoZip, String nomeArquivoExcel, byte[] conteudoArquivoExcel) throws IOException {
		
		arquivoZip.putNextEntry(new ZipEntry(nomeArquivoExcel));
		arquivoZip.write(conteudoArquivoExcel);
		arquivoZip.closeEntry();
		
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
