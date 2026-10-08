/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2006

  Descricao: Classe pai para gráficos de linhas.

  Historico:

    $Log: GraficoXY.java,v $
    Revision 1.8  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.6  2008/08/14 19:33:25  fos
    Ajustada método ultrapassado.

    Revision 1.5  2008/08/13 18:57:09  fos
    Ajustado o nome da método para português.

    Revision 1.4  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.3  2007/07/06 13:02:25  fos
    Retirado efeito de sombra.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.io.Writer;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.consilux.lib.Grafico;
import com.consilux.lib.exception.GraficoException;

/**
 * Classe pai para gráficos de linhas.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/01/12 12:49:50 $ $Author: fos $
 */
public class GraficoXY extends Grafico {
	
	private static Logger logger = Logger.getLogger(GraficoXY.class);
	private StandardXYToolTipGenerator toolTipGenerator = null;
	private Boolean plotaLinhas = true;
	private Boolean plotaPontos = true;

	public GraficoXY(HttpSession session, Writer writer) {
		super(session, writer);
		this.toolTipGenerator = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                getNumberFormat(), getNumberFormat());
	}

	/* (non-Javadoc)
	 * @see com.consilux.lib.Grafico#gerar()
	 */
	public void gerar() throws GraficoException {
  		XYDataset xyDataset = (XYDataset)getDataset();
  		Plot plot = getPlot();
  		
    	try {
	        NumberAxis XAxis = new NumberAxis(getLegendaX());
	        NumberAxis YAxis = new NumberAxis(getLegendaY());
	        
//	        AbstractXYItemRenderer renderer = new XYLine3DRenderer();
	        
	        if (plot == null) { 
	        	if (xyDataset == null || xyDataset.getItemCount(0) == 0)
	        		throw new GraficoException();

		        AbstractXYItemRenderer renderer = new StandardXYItemRenderer(
		                (this.plotaLinhas ? StandardXYItemRenderer.LINES : 0) +
		                (this.plotaPontos ? StandardXYItemRenderer.SHAPES : 0),
		                toolTipGenerator);

		        plot = new XYPlot(xyDataset, XAxis, YAxis, renderer);
	        }
	        
	        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, (xyDataset == null || xyDataset.getSeriesCount() > 1));
	        chart.setBackgroundPaint(java.awt.Color.white);

	        setInfo(new ChartRenderingInfo(new StandardEntityCollection()));
	        setArquivo(ServletUtilities.saveChartAsPNG(chart, getLargura(), getAltura(), getInfo(), getSession()));

		}
    	catch (Exception e) {
			logger.error("Erro ao gerar gráfico.", e);
	        setArquivo("/images/grafico_vazio.png");
	    }
	}

	/**
	 * Adiciona uma série com os dados a serem exibidos no gráfico
	 * @param series Série com os dados
	 */
	public void addSeries(XYSeries series) {
		Dataset ds = super.getDataset(); 
		if (ds != null && ds instanceof XYSeriesCollection)
			((XYSeriesCollection)ds).addSeries(series);
		else
			super.setDataset(new XYSeriesCollection(series));
	}
	
	public void setPlot(XYPlot plot) {
		super.setPlot(plot);
	}

	/**
	 * @param plotaPontos
	 */
	public void setPlotaPontos(Boolean plotaPontos) {
		this.plotaPontos = plotaPontos;
	}

	public void setPlotaLinhas(Boolean plotaLinhas) {
		this.plotaLinhas = plotaLinhas;
	}
	
	public StandardXYToolTipGenerator getToolTipGenerator() {
		return toolTipGenerator;
	}

}
