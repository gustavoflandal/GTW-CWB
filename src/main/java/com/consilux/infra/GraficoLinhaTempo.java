/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2006

  Descricao: Classe pai para gráficos de linhas com base no tempo.

  Historico:

    $Log: GraficoLinhaTempo.java,v $
    Revision 1.9  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.7  2008/08/14 19:33:25  fos
    Ajustada método ultrapassado.

    Revision 1.6  2008/08/13 18:56:57  fos
    Ajustado o nome da método para português.

    Revision 1.5  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.4  2007/07/06 13:02:14  fos
    Retirado efeito de sombra.

    Revision 1.3  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.io.Writer;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.consilux.lib.Grafico;
import com.consilux.lib.exception.GraficoException;

/**
 * Classe pai para gráficos de linhas com base no tempo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.9 $ $Date: 2009/01/12 12:49:50 $ $Author: fos $
 */
public class GraficoLinhaTempo extends Grafico {

	private static Logger logger = Logger.getLogger(GraficoLinhaTempo.class); 
	
	/**
	 * Inicia objetos necessário para montagem do grafico.
	 * @param session Sessão onde será armazenada o identificador do gráfico
	 * @param writer Saída dos dados posicionais do gráfico
	 */
	public GraficoLinhaTempo(HttpSession session, Writer writer) {
		super(session, writer);
	}

	/* (non-Javadoc)
	 * @see com.consilux.lib.Grafico#gerar()
	 */
	public void gerar() throws GraficoException {
  		XYDataset xyDataset = (XYDataset)getDataset();
    	if (xyDataset == null || xyDataset.getItemCount(0) == 0)
    		throw new GraficoException();

    	try {
	        StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                getDateFormat(), getNumberFormat());

	        DateAxis timeAxis = new DateAxis(getLegendaX());
	        if (getUnidade() != null)
	        	timeAxis.setTickUnit((DateTickUnit)getUnidade());
	        
	        timeAxis.setDateFormatOverride(getDateFormat());
	        
	        NumberAxis valueAxis = new NumberAxis(getLegendaY());
	        if (getValorMaximo() != null) {
	        	valueAxis.setRange(0, getValorMaximo());
	        }
	        
	        AbstractXYItemRenderer renderer = new StandardXYItemRenderer(
	                StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES,
	                ttg);

	        //AbstractXYItemRenderer renderer = new XYLine3DRenderer();
	        renderer.setBaseToolTipGenerator(ttg);
	        
	        XYPlot plot = null;
	        
	        if (getListaDataset().size() > 1) {
	        	plot = new CombinedDomainXYPlot(timeAxis);
	        	Integer i = 0;
	        	for (AbstractDataset dataset : getListaDataset()) {
        			
	        		valueAxis = new NumberAxis(getListaLegendaY().get(i));
	        		
	        		if (getListaValorMaximo().size() > i) {
	    	        	valueAxis.setRange(0, getListaValorMaximo().get(i));
	    	        }
	        		
					XYPlot subplot = new XYPlot((XYDataset)dataset, timeAxis, valueAxis, renderer);
	        		((CombinedDomainXYPlot)plot).add(subplot);
		        	i++;
				}
	        }
	        else {
	        	plot = new XYPlot(xyDataset, timeAxis, valueAxis, renderer);
	        }
	        
	        plot.setFixedLegendItems(removeDuplicados(plot.getLegendItems()));
	        
	        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, (xyDataset.getSeriesCount() > 1));
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
	public void criaNovoDataSet() {
		super.addDataset(new XYSeriesCollection());
	}
	/**
	 * Ajusta o nível de detalhamento da linha do tempo.
	 * @param unidade Unidade para a linha do tempo
	 */
	public void setUnidade(DateTickUnit unidade) {
      	super.setUnidade(unidade);
	}
}
