/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 21/09/2010

  Descrição: Servlet para envio do gráfico de perfil.

  Histórico:

    $Log$

 *********************************************************************************/
package com.consilux.servlet.ferramentas;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.consilux.infra.ExpValida;
import com.consilux.infra.GraficoXY;
import com.consilux.lib.exception.GraficoException;
import com.consilux.model.Acesso;
import com.consilux.model.Perfil;

/**
 * Servlet para envio do gráfico de perfil..
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/01/12 12:49:46 $ $Author: fos $
 */
public class PerfilVeiculo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = 8931202539704328853L;
	private static Logger logger = Logger.getLogger(PerfilVeiculo.class); 
	public static Integer MARGEM_RUIDO = 20;
	
	/**
	 * Constrói o objeto
	 */
	public PerfilVeiculo() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Acesso acesso = new Acesso(request, response, false); 
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");

		Date inicio = new Date();
		String sIdVeiculo = request.getParameter("id_veiculo");
		
		if (sIdVeiculo == null || !ExpValida.LONGO.validar(sIdVeiculo))
			throw new ServletException("Identificador do veículo enviado inválido!");

		Integer idVeiculo = Integer.valueOf(sIdVeiculo);

		try {
			List<Perfil> lPerfil = Perfil.buscaPerfisPorIdVeiculo(idVeiculo);
			GraficoXY grafico = new GraficoXY(request.getSession(), response.getWriter());
		    
			XYSeriesCollection seriesLinhas = new XYSeriesCollection();
			XYSeriesCollection seriesDisparos = new XYSeriesCollection();
			XYSeriesCollection seriesCentros = new XYSeriesCollection();
			
	        byte[] blobPerfil;
			
			for (Perfil perfil: lPerfil) {
				XYSeries serieLinha = new XYSeries("Sensor "+(perfil.getSensor()+1));
				XYSeries serieDisparo = new XYSeries("Disparo sensor "+(perfil.getSensor()+1));
				XYSeries serieCentro = new XYSeries("Centro sensor "+(perfil.getSensor()+1));
				Double tamanhoAmostra = perfil.getTamanhoAmostra();
				Integer inicioDisparo = (int)((double)perfil.getInicioDisparo()*tamanhoAmostra);
				Integer finalDisparo = (int)((double)perfil.getFinalDisparo()*tamanhoAmostra);

				long area = 0; 
				long areaQuad = 0;
				int contaAmostra = 0;
				try {
					blobPerfil = perfil.getPerfil();
					for (int i = 0; i < blobPerfil.length-1; i+=2) {
						int x = (int)((double)(i/2)*tamanhoAmostra);
						
						Integer y = ((blobPerfil[i] & 0xFF) << 8) | (blobPerfil[i+1] & 0xFF);
						serieLinha.add(x, y);
						
						if (x == inicioDisparo) 
							serieDisparo.add(x, y);
						if (x == finalDisparo) 
							serieDisparo.add(x, y);
						
						if (x >= inicioDisparo && x <= finalDisparo) {
							contaAmostra++;
							area += y*tamanhoAmostra;
							areaQuad += x*y;
						}
					}
					if (contaAmostra > 1) {
						serieCentro.add((areaQuad/area)*tamanhoAmostra, (area/(finalDisparo-inicioDisparo))/2);
					}
				}
				catch (Exception ex) {
					logger.warn("Erro ao plotar o perfil!", ex);
				}
				
		        seriesLinhas.addSeries(serieLinha);
		        seriesDisparos.addSeries(serieDisparo);
		        seriesCentros.addSeries(serieCentro);
			}

			AbstractXYItemRenderer rendererLinha = new StandardXYItemRenderer(
	                StandardXYItemRenderer.LINES,
	                grafico.getToolTipGenerator());

			XYPlot plotXY = new XYPlot(seriesLinhas, new NumberAxis("Tempo"), new NumberAxis("Amplitude"), rendererLinha);

	        AbstractXYItemRenderer rendererPonto = new StandardXYItemRenderer(
	                StandardXYItemRenderer.SHAPES,
	                grafico.getToolTipGenerator());

	        plotXY.setDataset(1, seriesDisparos);
	        plotXY.setRenderer(1, rendererPonto);
	        for (int i=0; i < seriesDisparos.getSeriesCount(); i++)
	        	rendererPonto.setSeriesPaint(i, Color.BLACK);

	        AbstractXYItemRenderer rendererPonto2 = new StandardXYItemRenderer(
	                StandardXYItemRenderer.SHAPES,
	                grafico.getToolTipGenerator());

	        plotXY.setDataset(2, seriesCentros);
	        plotXY.setRenderer(2, rendererPonto2);
	        for (int i=0; i < seriesCentros.getSeriesCount(); i++)
	        	rendererPonto2.setSeriesPaint(i, Color.CYAN);
	        
	        grafico.setPlot(plotXY);

	        grafico.setLargura(620);
			grafico.setAltura(460);
			
			String url = "";
			String mapId = "";
			StringBuffer map = new StringBuffer();
			
			try {
				grafico.gerar();
				mapId = grafico.getImageMap(map);
				url = "/graficos/MostraGrafico?filename="+grafico.getArquivo();
			}
			catch(GraficoException e) {
				url = "/images/grafico_vazio.png";
			}
			request.setAttribute("mapId", mapId);
			request.setAttribute("map", map);
			request.setAttribute("url", url);
			
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/templates/relatorio/perfil_veiculo.jsp");
			rd.include(request, response);

			logger.info("[TEMPO] Perfil: "+(new Date().getTime() - inicio.getTime()));
		}
		catch(Exception err) {
			logger.error("Erro ao mostar o gráfico.", err);
			throw new ServletException("Erro ao montar o gráfico: " + err.getMessage(), err);
		}

	}
}