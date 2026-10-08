package com.consilux.servlet.relatorio.rj;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class ColecaoEstilos {
	
	public CellStyle estiloCabecalhoFiltro;
	public CellStyle estiloCabecalhoCorpoNegrito;
	public CellStyle estiloCabecalhoCorpo;
	public CellStyle estiloCabecalhoCorpoEsquerda;
	public CellStyle estiloCabecalhoCorpoEsquerdaSemBorda;
	public CellStyle estiloCabecalhoCorpoDireita;
	public CellStyle estiloItensCentralizado;
	public CellStyle estiloItensCentralizadoCinza;
	public CellStyle estiloItensCentralizadoVerde;
	public CellStyle estiloItensCentralizadoCinzaTempo;
	public CellStyle estiloItensCentralizadoVerdeTempo;
	public CellStyle estiloItensCentralizadoTempo;
	public CellStyle estiloItensNumCentralizado;
	public CellStyle estiloItensNumCentralizadoVermelho;
	public CellStyle estiloItensNumCentralizadoAmarelo;
	public CellStyle estiloItensNumCentralizadoVerde;
	public CellStyle estiloItensNumCentralizadoCinza;
	public CellStyle estiloItensNumCentralizadoComZero;
	public CellStyle estiloItensNumCentralizadoVermelhoComZero;
	public CellStyle estiloItensNumCentralizadoAmareloComZero;
	public CellStyle estiloItensNumCentralizadoVerdeComZero;
	public CellStyle estiloItensNumCentralizadoCinzaComZero;
	public CellStyle estiloItensIM;
	public CellStyle estiloItensIMVerde;
	public CellStyle estiloItensIMCinza;
	public CellStyle estiloCabecalhoCorpo1;
	public CellStyle estiloCabecalhoCorpo2;
	public CellStyle estiloCabecalhoCorpo3;
	public CellStyle estiloCabecalhoCorpoSemBordaInferior;
	public CellStyle estiloCabecalhoCorpoSemBordaSuperior;
	public CellStyle estiloCabecalhoCorpoApenasBordaSuperior;
	public CellStyle estiloCabecalhoCorpoBordaSuperiorEsquerda;
	public CellStyle estiloCabecalhoCorpoBordaSuperiorDireita;
	public CellStyle estiloCabecalhoCorpoApenasBordaInferior;
	public CellStyle estiloCabecalhoCorpoBordaInferiorEsquerda;
	public CellStyle estiloCabecalhoCorpoBordaInferiorDireita;
	public CellStyle estiloCabecalhoCorpoApenasBordaEsquerda;
	public CellStyle estiloCabecalhoCorpoApenasBordaDireita;
	public CellStyle estiloCabecalhoCorpoSemBordaFundoBranco;
	public CellStyle estiloCabecalhoCorpoNegritoVerde;
	public CellStyle estiloCorpo;
	public CellStyle estiloPorcentagem;
	public CellStyle estiloPorcentagemCinza;
	public CellStyle estiloItensLinhaSemInfo;
	public CellStyle estiloCabecalhoNegrito;
	public CellStyle estiloItensEsquerda;
	public CellStyle estiloItensEsquerdaQuebraLinha;
	public CellStyle estiloCabecalhoTituloRelatorio;
	public CellStyle estiloCabecalhoTituloRelatorioComBorda;
	public CellStyle estiloFiltro;
	public CellStyle estiloCabelcalhoFiltroVM;
	public CellStyle estiloFiltroVM;
	public CellStyle estiloFiltroHoraVM;
	public CellStyle estiloCabelcalhoFiltroVMCinza;
	public CellStyle estiloCabecalhoCorpoCinza;
	public CellStyle estiloCabecalhoCorpoCinzaClaro;
	public CellStyle estiloItensCentralizadoCinzaPontilhado;
	public CellStyle estiloItensCentralizadoCinzaUltimaLinha;
	public CellStyle estiloItensEsquerdaUltimaLinha;
	public CellStyle estiloItensEsquerdaPontilhado;
	public CellStyle estiloItensCentralizadoVerdeUltimaLinha;
	public CellStyle estiloItensCentralizadoVerdePontilhado;
	public CellStyle estiloItensCentralizadoUltimaLinha;
	public CellStyle estiloItensCentralizadoPontilhado;
	public CellStyle estiloItensCentralizadoPlanAcomp;
	public CellStyle estiloItensEsquerdaPlanAcomp;
	public CellStyle estiloItensCentralizadoVerdePlanAcomp;
	public CellStyle estiloItensCentralizadoCinzaPlanAcomp;
	public CellStyle estiloItensCentralizadoResumo;
	public CellStyle estiloItensNumCentralizadoResumo;
	public CellStyle estiloPorcentagemResumo;

	public ColecaoEstilos(Workbook wb) {
		//Fontes
		Font f1 = wb.createFont();
        f1.setFontHeightInPoints((short) 14);
        f1.setColor(Font.COLOR_NORMAL);
        f1.setBold(true);
        f1.setFontName("Calibri");
        
		Font f2 = wb.createFont();
        f2.setFontHeightInPoints((short) 10);
        f2.setFontName("Calibri");
        f2.setColor(Font.COLOR_NORMAL);
        
        Font f3 = wb.createFont();
        f3.setFontHeightInPoints((short) 10);
        f3.setBold(true);
        f3.setFontName("Calibri");
        f3.setColor(Font.COLOR_NORMAL);
        
		Font f4 = wb.createFont();
        f4.setFontHeightInPoints((short) 10);
        f4.setBold(true);
        f4.setFontName("Calibri");
        f4.setColor(Font.COLOR_NORMAL);
        
        Font f5 = wb.createFont();
        f5.setFontHeightInPoints((short) 10);
        f5.setFontName("Calibri");
        f5.setColor(Font.COLOR_NORMAL);
        
        Font f6 = wb.createFont();
        f6.setFontHeightInPoints((short) 12);
        f6.setBold(true);
        f6.setFontName("Calibri");
        f6.setColor(Font.COLOR_NORMAL);
        
        estiloItensLinhaSemInfo = wb.createCellStyle();
        estiloItensLinhaSemInfo.setAlignment(HorizontalAlignment.LEFT);
        estiloItensLinhaSemInfo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensLinhaSemInfo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensLinhaSemInfo.setFont(f5);
        
        estiloCabecalhoFiltro = wb.createCellStyle();
        estiloCabecalhoFiltro.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoFiltro.setFont(f4);
        
        estiloCabecalhoNegrito = wb.createCellStyle();
        estiloCabecalhoNegrito.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoNegrito.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoNegrito.setWrapText(true);
        estiloCabecalhoNegrito.setFont(f4);
        
        estiloCabecalhoCorpoNegrito = wb.createCellStyle();
        estiloCabecalhoCorpoNegrito.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoNegrito.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoNegrito.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegrito.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegrito.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegrito.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegrito.setWrapText(true);
        estiloCabecalhoCorpoNegrito.setFont(f4);
        
        estiloCabecalhoCorpoNegritoVerde = wb.createCellStyle();
        estiloCabecalhoCorpoNegritoVerde.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoNegritoVerde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoNegritoVerde.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegritoVerde.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegritoVerde.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegritoVerde.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoNegritoVerde.setWrapText(true);
        estiloCabecalhoCorpoNegritoVerde.setFont(f4);
        estiloCabecalhoCorpoNegritoVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloCabecalhoCorpoNegritoVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        estiloFiltro = wb.createCellStyle();
        estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
        estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloFiltro.setFont(f2);
        
        estiloCabecalhoCorpo = wb.createCellStyle();
        estiloCabecalhoCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo.setWrapText(true);
        estiloCabecalhoCorpo.setFont(f5);
        
        estiloCorpo = wb.createCellStyle();
        estiloCorpo.setAlignment(HorizontalAlignment.CENTER);
        estiloCorpo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCorpo.setBorderBottom(BorderStyle.MEDIUM);
        estiloCorpo.setBorderTop(BorderStyle.MEDIUM);
        estiloCorpo.setBorderRight(BorderStyle.MEDIUM);
        estiloCorpo.setBorderLeft(BorderStyle.MEDIUM);
        estiloCorpo.setFont(f5);
        
        estiloCabecalhoCorpoEsquerda = wb.createCellStyle();
        estiloCabecalhoCorpoEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpoEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoEsquerda.setWrapText(true);
        estiloCabecalhoCorpoEsquerda.setFont(f4);
        
        estiloCabecalhoCorpoEsquerdaSemBorda = wb.createCellStyle();
        estiloCabecalhoCorpoEsquerdaSemBorda.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpoEsquerdaSemBorda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoEsquerdaSemBorda.setWrapText(true);
        estiloCabecalhoCorpoEsquerdaSemBorda.setFont(f4);
        
        estiloCabecalhoCorpoDireita = wb.createCellStyle();
        estiloCabecalhoCorpoDireita.setAlignment(HorizontalAlignment.RIGHT);
        estiloCabecalhoCorpoDireita.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoDireita.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoDireita.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoDireita.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoDireita.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoDireita.setWrapText(true);
        estiloCabecalhoCorpoDireita.setFont(f4);
        
        estiloItensCentralizado = wb.createCellStyle();
        estiloItensCentralizado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizado.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizado.setWrapText(true);
        estiloItensCentralizado.setFont(f5);
        
        estiloItensCentralizadoPlanAcomp = wb.createCellStyle();
        estiloItensCentralizadoPlanAcomp.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoPlanAcomp.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoPlanAcomp.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoPlanAcomp.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoPlanAcomp.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoPlanAcomp.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoPlanAcomp.setFont(f5);
        
        estiloItensEsquerdaPlanAcomp = wb.createCellStyle();
        estiloItensEsquerdaPlanAcomp.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerdaPlanAcomp.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensEsquerdaPlanAcomp.setBorderBottom(BorderStyle.DOTTED);
        estiloItensEsquerdaPlanAcomp.setBorderTop(BorderStyle.MEDIUM);
        estiloItensEsquerdaPlanAcomp.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerdaPlanAcomp.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerdaPlanAcomp.setFont(f5);
        
        estiloItensEsquerda = wb.createCellStyle();
        estiloItensEsquerda.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerda.setFont(f5);
        
        estiloItensEsquerdaQuebraLinha = wb.createCellStyle();
        estiloItensEsquerdaQuebraLinha.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerdaQuebraLinha.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensEsquerdaQuebraLinha.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensEsquerdaQuebraLinha.setBorderTop(BorderStyle.MEDIUM);
        estiloItensEsquerdaQuebraLinha.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerdaQuebraLinha.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerdaQuebraLinha.setWrapText(true);
        estiloItensEsquerdaQuebraLinha.setFont(f5);
        estiloItensCentralizadoCinza = wb.createCellStyle();
        estiloItensCentralizadoCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinza.setWrapText(true);
        estiloItensCentralizadoCinza.setFont(f5);
        estiloItensCentralizadoCinza.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoVerde = wb.createCellStyle();
        estiloItensCentralizadoVerde.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoVerde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoVerde.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerde.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerde.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerde.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerde.setWrapText(true);
        estiloItensCentralizadoVerde.setFont(f5);
        estiloItensCentralizadoVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensCentralizadoVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoTempo = wb.createCellStyle();
        estiloItensCentralizadoTempo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoTempo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoTempo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoTempo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoTempo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoTempo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoTempo.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
        estiloItensCentralizadoTempo.setFont(f5);
        
        estiloItensCentralizadoCinzaTempo = wb.createCellStyle();
        estiloItensCentralizadoCinzaTempo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoCinzaTempo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoCinzaTempo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaTempo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaTempo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaTempo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaTempo.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
        estiloItensCentralizadoCinzaTempo.setFont(f5);
        estiloItensCentralizadoCinzaTempo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoCinzaTempo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoVerdeTempo = wb.createCellStyle();
        estiloItensCentralizadoVerdeTempo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoVerdeTempo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoVerdeTempo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeTempo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeTempo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeTempo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeTempo.setDataFormat(wb.createDataFormat().getFormat("hh:mm:ss"));
        estiloItensCentralizadoVerdeTempo.setFont(f5);
        estiloItensCentralizadoVerdeTempo.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensCentralizadoVerdeTempo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizado = wb.createCellStyle();
        estiloItensNumCentralizado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizado.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizado.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizado.setWrapText(true);
        estiloItensNumCentralizado.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizado.setFont(f5);
        
        estiloItensNumCentralizadoVermelho = wb.createCellStyle();
        estiloItensNumCentralizadoVermelho.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoVermelho.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoVermelho.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelho.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelho.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelho.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelho.setWrapText(true);
        estiloItensNumCentralizadoVermelho.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizadoVermelho.setFont(f5);
        estiloItensNumCentralizadoVermelho.setFillForegroundColor(IndexedColors.RED.index);
        estiloItensNumCentralizadoVermelho.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoAmarelo = wb.createCellStyle();
        estiloItensNumCentralizadoAmarelo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoAmarelo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoAmarelo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmarelo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmarelo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmarelo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmarelo.setWrapText(true);
        estiloItensNumCentralizadoAmarelo.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizadoAmarelo.setFont(f5);
        estiloItensNumCentralizadoAmarelo.setFillForegroundColor(IndexedColors.YELLOW.index);
        estiloItensNumCentralizadoAmarelo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoVerde = wb.createCellStyle();
        estiloItensNumCentralizadoVerde.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoVerde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoVerde.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerde.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerde.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerde.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerde.setWrapText(true);
        estiloItensNumCentralizadoVerde.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizadoVerde.setFont(f5);
        estiloItensNumCentralizadoVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensNumCentralizadoVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoCinza = wb.createCellStyle();
        estiloItensNumCentralizadoCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinza.setWrapText(true);
        estiloItensNumCentralizadoCinza.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizadoCinza.setFont(f5);
        estiloItensNumCentralizadoCinza.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensNumCentralizadoCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        estiloItensNumCentralizadoComZero = wb.createCellStyle();
        estiloItensNumCentralizadoComZero.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoComZero.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoComZero.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoComZero.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoComZero.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoComZero.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoComZero.setWrapText(true);
        estiloItensNumCentralizadoComZero.setDataFormat(wb.createDataFormat().getFormat("#,#0"));
        estiloItensNumCentralizadoComZero.setFont(f5);
        
        estiloItensNumCentralizadoVermelhoComZero = wb.createCellStyle();
        estiloItensNumCentralizadoVermelhoComZero.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoVermelhoComZero.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoVermelhoComZero.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelhoComZero.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelhoComZero.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelhoComZero.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVermelhoComZero.setWrapText(true);
        estiloItensNumCentralizadoVermelhoComZero.setDataFormat(wb.createDataFormat().getFormat("#,#0"));
        estiloItensNumCentralizadoVermelhoComZero.setFont(f5);
        estiloItensNumCentralizadoVermelhoComZero.setFillForegroundColor(IndexedColors.RED.index);
        estiloItensNumCentralizadoVermelhoComZero.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoAmareloComZero = wb.createCellStyle();
        estiloItensNumCentralizadoAmareloComZero.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoAmareloComZero.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoAmareloComZero.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmareloComZero.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmareloComZero.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmareloComZero.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoAmareloComZero.setWrapText(true);
        estiloItensNumCentralizadoAmareloComZero.setDataFormat(wb.createDataFormat().getFormat("#,#"));
        estiloItensNumCentralizadoAmareloComZero.setFont(f5);
        estiloItensNumCentralizadoAmareloComZero.setFillForegroundColor(IndexedColors.YELLOW.index);
        estiloItensNumCentralizadoAmareloComZero.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoVerdeComZero = wb.createCellStyle();
        estiloItensNumCentralizadoVerdeComZero.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoVerdeComZero.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoVerdeComZero.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerdeComZero.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerdeComZero.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerdeComZero.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoVerdeComZero.setWrapText(true);
        estiloItensNumCentralizadoVerdeComZero.setDataFormat(wb.createDataFormat().getFormat("#,#"));
        estiloItensNumCentralizadoVerdeComZero.setFont(f5);
        estiloItensNumCentralizadoVerdeComZero.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensNumCentralizadoVerdeComZero.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoCinzaComZero = wb.createCellStyle();
        estiloItensNumCentralizadoCinzaComZero.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoCinzaComZero.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoCinzaComZero.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinzaComZero.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinzaComZero.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinzaComZero.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoCinzaComZero.setWrapText(true);
        estiloItensNumCentralizadoCinzaComZero.setDataFormat(wb.createDataFormat().getFormat("#,#"));
        estiloItensNumCentralizadoCinzaComZero.setFont(f5);
        estiloItensNumCentralizadoCinzaComZero.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensNumCentralizadoCinzaComZero.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensIM = wb.createCellStyle();
        estiloItensIM.setAlignment(HorizontalAlignment.CENTER);
        estiloItensIM.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensIM.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensIM.setBorderTop(BorderStyle.MEDIUM);
        estiloItensIM.setBorderRight(BorderStyle.MEDIUM);
        estiloItensIM.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensIM.setWrapText(true);
        estiloItensIM.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        estiloItensIM.setFont(f5);
        
        estiloItensIMVerde = wb.createCellStyle();
        estiloItensIMVerde.setAlignment(HorizontalAlignment.CENTER);
        estiloItensIMVerde.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensIMVerde.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensIMVerde.setBorderTop(BorderStyle.MEDIUM);
        estiloItensIMVerde.setBorderRight(BorderStyle.MEDIUM);
        estiloItensIMVerde.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensIMVerde.setWrapText(true);
        estiloItensIMVerde.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        estiloItensIMVerde.setFont(f5);
        estiloItensIMVerde.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensIMVerde.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensIMCinza = wb.createCellStyle();
        estiloItensIMCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloItensIMCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensIMCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensIMCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloItensIMCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloItensIMCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensIMCinza.setWrapText(true);
        estiloItensIMCinza.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        estiloItensIMCinza.setFont(f5);
        estiloItensIMCinza.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensIMCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpo1 = wb.createCellStyle();
        estiloCabecalhoCorpo1.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpo1.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo1.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo1.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo1.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo1.setFont(f4);
        estiloCabecalhoCorpo1.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpo1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpo2 = wb.createCellStyle();
        estiloCabecalhoCorpo2.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpo2.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo2.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo2.setFont(f4);
        estiloCabecalhoCorpo2.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpo2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpo3 = wb.createCellStyle();
        estiloCabecalhoCorpo3.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoCorpo3.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpo3.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpo3.setFont(f4);
        estiloCabecalhoCorpo3.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpo3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoSemBordaInferior = wb.createCellStyle();
        estiloCabecalhoCorpoSemBordaInferior.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaInferior.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaInferior.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaInferior.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaInferior.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaInferior.setFont(f1);
        estiloCabecalhoCorpoSemBordaInferior.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoSemBordaInferior.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoSemBordaSuperior = wb.createCellStyle();
        estiloCabecalhoCorpoSemBordaSuperior.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaSuperior.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaSuperior.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaSuperior.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaSuperior.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoSemBordaSuperior.setFont(f1);
        estiloCabecalhoCorpoSemBordaSuperior.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoSemBordaSuperior.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoApenasBordaSuperior = wb.createCellStyle();
        estiloCabecalhoCorpoApenasBordaSuperior.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaSuperior.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaSuperior.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoApenasBordaSuperior.setFont(f1);
        estiloCabecalhoCorpoApenasBordaSuperior.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoApenasBordaSuperior.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        estiloCabecalhoCorpoApenasBordaInferior = wb.createCellStyle();
        estiloCabecalhoCorpoApenasBordaInferior.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaInferior.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaInferior.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoApenasBordaInferior.setFont(f1);
        estiloCabecalhoCorpoApenasBordaInferior.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoApenasBordaInferior.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoBordaSuperiorEsquerda = wb.createCellStyle();
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setFont(f1);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoBordaSuperiorEsquerda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoBordaSuperiorDireita = wb.createCellStyle();
        estiloCabecalhoCorpoBordaSuperiorDireita.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoBordaSuperiorDireita.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoBordaSuperiorDireita.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaSuperiorDireita.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaSuperiorDireita.setFont(f1);
        estiloCabecalhoCorpoBordaSuperiorDireita.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoBordaSuperiorDireita.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoBordaInferiorEsquerda = wb.createCellStyle();
        estiloCabecalhoCorpoBordaInferiorEsquerda.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setFont(f1);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoBordaInferiorEsquerda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoBordaInferiorDireita = wb.createCellStyle();
        estiloCabecalhoCorpoBordaInferiorDireita.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoBordaInferiorDireita.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoBordaInferiorDireita.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaInferiorDireita.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoBordaInferiorDireita.setFont(f1);
        estiloCabecalhoCorpoBordaInferiorDireita.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoBordaInferiorDireita.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoApenasBordaDireita = wb.createCellStyle();
        estiloCabecalhoCorpoApenasBordaDireita.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaDireita.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaDireita.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoApenasBordaDireita.setFont(f1);
        estiloCabecalhoCorpoApenasBordaDireita.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoApenasBordaDireita.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoApenasBordaEsquerda = wb.createCellStyle();
        estiloCabecalhoCorpoApenasBordaEsquerda.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaEsquerda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoApenasBordaEsquerda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoApenasBordaEsquerda.setFont(f1);
        estiloCabecalhoCorpoApenasBordaEsquerda.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoApenasBordaEsquerda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoSemBordaFundoBranco = wb.createCellStyle();
        estiloCabecalhoCorpoSemBordaFundoBranco.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaFundoBranco.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoSemBordaFundoBranco.setWrapText(true);
        estiloCabecalhoCorpoSemBordaFundoBranco.setFont(f1);
        estiloCabecalhoCorpoSemBordaFundoBranco.setFillForegroundColor(IndexedColors.WHITE.index);
        estiloCabecalhoCorpoSemBordaFundoBranco.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloPorcentagem = wb.createCellStyle();
        estiloPorcentagem.setAlignment(HorizontalAlignment.CENTER);
        estiloPorcentagem.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloPorcentagem.setBorderBottom(BorderStyle.MEDIUM);
        estiloPorcentagem.setBorderTop(BorderStyle.MEDIUM);
        estiloPorcentagem.setBorderRight(BorderStyle.MEDIUM);
        estiloPorcentagem.setBorderLeft(BorderStyle.MEDIUM);
        estiloPorcentagem.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
        estiloPorcentagem.setFont(f5);
        
        estiloPorcentagemCinza = wb.createCellStyle();
        estiloPorcentagemCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloPorcentagemCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloPorcentagemCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloPorcentagemCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloPorcentagemCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloPorcentagemCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloPorcentagemCinza.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
        estiloPorcentagemCinza.setFont(f5);
        estiloPorcentagemCinza.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloPorcentagemCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoTituloRelatorio = wb.createCellStyle();
        estiloCabecalhoTituloRelatorio.setAlignment(HorizontalAlignment.LEFT);
        estiloCabecalhoTituloRelatorio.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoTituloRelatorio.setFont(f1);
        
        estiloCabecalhoTituloRelatorioComBorda = wb.createCellStyle();
        estiloCabecalhoTituloRelatorioComBorda.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoTituloRelatorioComBorda.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoTituloRelatorioComBorda.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoTituloRelatorioComBorda.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoTituloRelatorioComBorda.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoTituloRelatorioComBorda.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoTituloRelatorioComBorda.setFont(f1);
        
        estiloFiltro = wb.createCellStyle();
        estiloFiltro.setAlignment(HorizontalAlignment.LEFT);
        estiloFiltro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloFiltro.setFont(f2);
        
        estiloCabelcalhoFiltroVM = wb.createCellStyle();
        estiloCabelcalhoFiltroVM.setAlignment(HorizontalAlignment.LEFT);
        estiloCabelcalhoFiltroVM.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabelcalhoFiltroVM.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVM.setBorderTop(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVM.setBorderRight(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVM.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVM.setFont(f3);
        
        estiloFiltroVM = wb.createCellStyle();
        estiloFiltroVM.setAlignment(HorizontalAlignment.CENTER);
        estiloFiltroVM.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloFiltroVM.setBorderBottom(BorderStyle.MEDIUM);
        estiloFiltroVM.setBorderTop(BorderStyle.MEDIUM);
        estiloFiltroVM.setBorderRight(BorderStyle.MEDIUM);
        estiloFiltroVM.setBorderLeft(BorderStyle.MEDIUM);
        estiloFiltroVM.setFont(f2);
        
        estiloFiltroHoraVM = wb.createCellStyle();
        estiloFiltroHoraVM.setAlignment(HorizontalAlignment.CENTER);
        estiloFiltroHoraVM.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloFiltroHoraVM.setBorderBottom(BorderStyle.MEDIUM);
        estiloFiltroHoraVM.setBorderTop(BorderStyle.MEDIUM);
        estiloFiltroHoraVM.setBorderRight(BorderStyle.MEDIUM);
        estiloFiltroHoraVM.setBorderLeft(BorderStyle.MEDIUM);
        estiloFiltroHoraVM.setFont(f2);
        estiloFiltroHoraVM.setDataFormat(wb.createDataFormat().getFormat("HH:mm:ss"));
        
        estiloCabelcalhoFiltroVMCinza = wb.createCellStyle();
        estiloCabelcalhoFiltroVMCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloCabelcalhoFiltroVMCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabelcalhoFiltroVMCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVMCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVMCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVMCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabelcalhoFiltroVMCinza.setWrapText(true);
        estiloCabelcalhoFiltroVMCinza.setFont(f3);        
        estiloCabelcalhoFiltroVMCinza.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloCabelcalhoFiltroVMCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoCinza = wb.createCellStyle();
        estiloCabecalhoCorpoCinza.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoCinza.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoCinza.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinza.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinza.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinza.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinza.setWrapText(true);
        estiloCabecalhoCorpoCinza.setFont(f4);        
        estiloCabecalhoCorpoCinza.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        estiloCabecalhoCorpoCinza.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloCabecalhoCorpoCinzaClaro = wb.createCellStyle();
        estiloCabecalhoCorpoCinzaClaro.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalhoCorpoCinzaClaro.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalhoCorpoCinzaClaro.setBorderBottom(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinzaClaro.setBorderTop(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinzaClaro.setBorderRight(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinzaClaro.setBorderLeft(BorderStyle.MEDIUM);
        estiloCabecalhoCorpoCinzaClaro.setWrapText(true);
        estiloCabecalhoCorpoCinzaClaro.setFont(f6);        
        estiloCabecalhoCorpoCinzaClaro.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloCabecalhoCorpoCinzaClaro.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoCinzaPontilhado = wb.createCellStyle();
        estiloItensCentralizadoCinzaPontilhado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoCinzaPontilhado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoCinzaPontilhado.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoCinzaPontilhado.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoCinzaPontilhado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaPontilhado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaPontilhado.setFont(f5);
        estiloItensCentralizadoCinzaPontilhado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoCinzaPontilhado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoCinzaUltimaLinha = wb.createCellStyle();
        estiloItensCentralizadoCinzaUltimaLinha.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoCinzaUltimaLinha.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoCinzaUltimaLinha.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaUltimaLinha.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoCinzaUltimaLinha.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaUltimaLinha.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaUltimaLinha.setFont(f5);
        estiloItensCentralizadoCinzaUltimaLinha.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoCinzaUltimaLinha.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensEsquerdaUltimaLinha = wb.createCellStyle();
        estiloItensEsquerdaUltimaLinha.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerdaUltimaLinha.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensEsquerdaUltimaLinha.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensEsquerdaUltimaLinha.setBorderTop(BorderStyle.DOTTED);
        estiloItensEsquerdaUltimaLinha.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerdaUltimaLinha.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerdaUltimaLinha.setFont(f5);
        
        estiloItensEsquerdaPontilhado = wb.createCellStyle();
        estiloItensEsquerdaPontilhado.setAlignment(HorizontalAlignment.LEFT);
        estiloItensEsquerdaPontilhado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensEsquerdaPontilhado.setBorderBottom(BorderStyle.DOTTED);
        estiloItensEsquerdaPontilhado.setBorderTop(BorderStyle.DOTTED);
        estiloItensEsquerdaPontilhado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensEsquerdaPontilhado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensEsquerdaPontilhado.setFont(f5);
        
        estiloItensCentralizadoVerdeUltimaLinha = wb.createCellStyle();
        estiloItensCentralizadoVerdeUltimaLinha.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoVerdeUltimaLinha.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoVerdeUltimaLinha.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeUltimaLinha.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoVerdeUltimaLinha.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeUltimaLinha.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdeUltimaLinha.setFont(f5);
        estiloItensCentralizadoVerdeUltimaLinha.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensCentralizadoVerdeUltimaLinha.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoVerdePontilhado = wb.createCellStyle();
        estiloItensCentralizadoVerdePontilhado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoVerdePontilhado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoVerdePontilhado.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoVerdePontilhado.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoVerdePontilhado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdePontilhado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdePontilhado.setFont(f5);
        estiloItensCentralizadoVerdePontilhado.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensCentralizadoVerdePontilhado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoUltimaLinha = wb.createCellStyle();
        estiloItensCentralizadoUltimaLinha.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoUltimaLinha.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoUltimaLinha.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoUltimaLinha.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoUltimaLinha.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoUltimaLinha.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoUltimaLinha.setFont(f5);
        
        estiloItensCentralizadoPontilhado = wb.createCellStyle();
        estiloItensCentralizadoPontilhado.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoPontilhado.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoPontilhado.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoPontilhado.setBorderTop(BorderStyle.DOTTED);
        estiloItensCentralizadoPontilhado.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoPontilhado.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoPontilhado.setFont(f5);
        
        estiloItensCentralizadoVerdePlanAcomp = wb.createCellStyle();
        estiloItensCentralizadoVerdePlanAcomp.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoVerdePlanAcomp.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoVerdePlanAcomp.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoVerdePlanAcomp.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdePlanAcomp.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdePlanAcomp.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoVerdePlanAcomp.setFont(f5);
        estiloItensCentralizadoVerdePlanAcomp.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        estiloItensCentralizadoVerdePlanAcomp.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoCinzaPlanAcomp = wb.createCellStyle();
        estiloItensCentralizadoCinzaPlanAcomp.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoCinzaPlanAcomp.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoCinzaPlanAcomp.setBorderBottom(BorderStyle.DOTTED);
        estiloItensCentralizadoCinzaPlanAcomp.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaPlanAcomp.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaPlanAcomp.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoCinzaPlanAcomp.setFont(f5);
        estiloItensCentralizadoCinzaPlanAcomp.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoCinzaPlanAcomp.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensCentralizadoResumo = wb.createCellStyle();
        estiloItensCentralizadoResumo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensCentralizadoResumo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensCentralizadoResumo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensCentralizadoResumo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensCentralizadoResumo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensCentralizadoResumo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensCentralizadoResumo.setFont(f4);
        estiloItensCentralizadoResumo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensCentralizadoResumo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloItensNumCentralizadoResumo = wb.createCellStyle();
        estiloItensNumCentralizadoResumo.setAlignment(HorizontalAlignment.CENTER);
        estiloItensNumCentralizadoResumo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloItensNumCentralizadoResumo.setBorderBottom(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoResumo.setBorderTop(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoResumo.setBorderRight(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoResumo.setBorderLeft(BorderStyle.MEDIUM);
        estiloItensNumCentralizadoResumo.setDataFormat(wb.createDataFormat().getFormat("0,0"));
        estiloItensNumCentralizadoResumo.setFont(f4);
        estiloItensNumCentralizadoResumo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloItensNumCentralizadoResumo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        estiloPorcentagemResumo = wb.createCellStyle();
        estiloPorcentagemResumo.setAlignment(HorizontalAlignment.CENTER);
        estiloPorcentagemResumo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloPorcentagemResumo.setBorderBottom(BorderStyle.MEDIUM);
        estiloPorcentagemResumo.setBorderTop(BorderStyle.MEDIUM);
        estiloPorcentagemResumo.setBorderRight(BorderStyle.MEDIUM);
        estiloPorcentagemResumo.setBorderLeft(BorderStyle.MEDIUM);
        estiloPorcentagemResumo.setDataFormat(wb.createDataFormat().getFormat("0.000%"));
        estiloPorcentagemResumo.setFont(f4);
        estiloPorcentagemResumo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        estiloPorcentagemResumo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}
}
