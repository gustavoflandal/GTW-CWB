/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2006

  Descrição: Classe pai para gráficos de barras.

  Histórico:

    $Log: GraficoBarra.java,v $
    Revision 1.7  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.5  2008/08/14 19:33:25  fos
    Ajustada método ultrapassado.

    Revision 1.4  2008/08/13 18:56:44  fos
    Ajustado o nome da método para português.

    Revision 1.3  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.io.Writer;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.consilux.lib.Grafico;
import com.consilux.lib.exception.GraficoException;

/**
 * Classe pai para gráficos de barras.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/01/12 12:49:50 $ $Author: fos $
 */
public class GraficoBarra extends Grafico {

	private static Logger logger = Logger.getLogger(GraficoBarra.class); 
	
	/**
	 * Inicia objetos necessário para montagem do gráfico.
	 * @param session Sessão onde será armazenada o identificador do gráfico
	 * @param writer Saída dos dados posicionais do gráfico
	 */
	public GraficoBarra(HttpSession session, Writer writer) {
		super(session, writer);
	}

	/* (non-Javadoc)
	 * @see com.consilux.lib.Grafico#gerar()
	 */
	public void gerar() throws GraficoException {
  		CategoryDataset catDataset = (CategoryDataset)getDataset();
    	if (catDataset == null || catDataset.getColumnCount() == 0)
    		throw new GraficoException();

    	try {
	    	
    		StandardCategoryToolTipGenerator ttg = new StandardCategoryToolTipGenerator(
	                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
	                getNumberFormat());

    		CategoryAxis categoryAxis = new CategoryAxis(getLegendaX());
    		ValueAxis valueAxis = new NumberAxis(getLegendaY());
	        
	        BarRenderer renderer = new BarRenderer3D();
	        renderer.setBaseToolTipGenerator(ttg);

	        CategoryPlot plot = new CategoryPlot((CategoryDataset)getDataset(), categoryAxis, valueAxis, renderer);
	        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
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
	 * Ajusta os dados a serem mostrados no gráfico.
	 * @param dataset Dataset com os dados
	 */
	public void setDataset(DefaultCategoryDataset dataset) {
		super.setDataset(dataset);
	}
}
