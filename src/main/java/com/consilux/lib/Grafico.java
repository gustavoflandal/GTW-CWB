/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 15/01/2007

  Descricao: Classe pai para construção de graficos

  Historico:

    $Log: Grafico.java,v $
    Revision 1.6  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.4  2008/08/13 18:57:28  fos
    Ajustado o nome da método para português.

    Revision 1.3  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.lib;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.AbstractDataset;

import com.consilux.lib.exception.GraficoException;

/**
 * Classe pai para construção de graficos
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/01/12 12:49:46 $ $Author: fos $
 */
public abstract class Grafico {
	private HttpSession session;
	private Writer writer;
	private List<AbstractDataset> listaDataset = new ArrayList<AbstractDataset>();
	private Plot plot = null;
	private String arquivo;
	private ChartRenderingInfo info;
	private int largura = 500;
	private int altura = 300;
	private DateFormat dateFormat = DateFormat.getInstance();
	private TickUnit unidade = null;
	private String legendaX = "";
	private List<String> listaLegendaY = new ArrayList<String>();
	private List<Double> listaValorMaximo = new ArrayList<Double>();
	private NumberFormat numberFormat = NumberFormat.getInstance();
	/**
	 * Inicia objetos necessário para montagem do grafico.
	 * @param session Sessão onde será armazenada o identificador do gráfico
	 * @param writer Saída dos dados posicionais do gráfico
	 */
	public Grafico(HttpSession session, Writer writer) {
		this.session = session;
		this.writer = writer;
	}
	/**
	 * Envia as informações de posicionamento ao browser.
	 * @return O nome do arquivo
	 * @throws IOException
	 */
	public String dumpImageMap() throws IOException {
		ChartUtilities.writeImageMap(new PrintWriter(writer), arquivo, info, false);
    	writer.flush();
    	return arquivo;
	}
	
	/**
	 * Armazena informações de posicionamento em um StringBuffer.
	 * @return O nome do arquivo
	 * @throws IOException
	 */
	public String getImageMap(StringBuffer sb) throws IOException {
		StringWriter sw = new StringWriter();
		ChartUtilities.writeImageMap(new PrintWriter(sw), arquivo, info, false);
    	sb.append(sw.getBuffer());
    	
    	return arquivo;
	}
	
	/**
	 * Será implementado o busca de dados e montagem do grafico.
	 * @throws GraficoException
	 */
	public abstract void gerar() throws GraficoException;
	/**
	 * Ajuste de dados através de Dataset.
	 * @param dataset
	 */
	protected void setDataset(AbstractDataset dataset) {
		if (this.listaDataset.size() > 0)
			this.listaDataset.set(0, dataset);
		else
			addDataset(dataset);
	}
	/**
	 * Ajuste de dados através de Dataset.
	 * @param dataset
	 */
	protected void addDataset(AbstractDataset dataset) {
		this.listaDataset.add(dataset);
	}
	/**
	 * Ajusta o nome do arquivo que será gerado.
	 * @param arquivo
	 */
	protected void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
	/**
	 * Ajuste de dados através de objeto nativo da biblioteca.
	 * @param info
	 */
	protected void setInfo(ChartRenderingInfo info) {
		this.info = info;
	}
	/**
	 * Ajusta o objeto de sessão que é necessário para montar o grafico.
	 * @param session
	 */
	protected void setSession(HttpSession session) {
		this.session = session;
	}
	/**
	 * Ajusta o objeto de saída de dados.
	 * @param writer
	 */
	protected void setWriter(Writer writer) {
		this.writer = writer;
	}
	public Plot getPlot() {
		return plot;
	}
	protected void setPlot(Plot plot) {
		this.plot = plot;
	}
	/**
	 * Nome do arquivo para imagem do gráfico.
	 * @return Nome do grafico
	 */
	public String getArquivo() {
		return this.arquivo;
	}
	/**
	 * Dataset com os dados do gráfico
	 * @return Dataset
	 */
	public AbstractDataset getDataset() {
		return this.listaDataset.size() > 0 ? this.listaDataset.get(this.listaDataset.size()-1) : null;
	}
	/**
	 * Dataset com os dados do gráfico
	 * @return Dataset
	 */
	public List<AbstractDataset> getListaDataset() {
		return this.listaDataset;
	}
	/**
	 * Objeto nativo da biblioteca com os dados do gráfico
	 * @return Objeto
	 */
	protected ChartRenderingInfo getInfo() {
		return this.info;
	}
	/**
	 * Retorna o objeto de sessão utilizado na classe.
	 * @return Sessão
	 */
	protected HttpSession getSession() {
		return this.session;
	}
	/**
	 * Retorna a altura da imagem gerada.
	 * @return Altura
	 */
	public int getAltura() {
		return altura;
	}
	/**
	 * Ajusta a altura da imagem gerada.
	 * @param altura
	 */
	public void setAltura(int altura) {
		this.altura = altura;
	}
	/**
	 * Retorna a largura da imagem gerada.
	 * @return Largura
	 */
	public int getLargura() {
		return largura;
	}
	/**
	 * Ajusta a largura da imagem gerada.
	 * @param largura
	 */
	public void setLargura(int largura) {
		this.largura = largura;
	}
	/**
	 * Retorna o formato de data no gráfico.
	 * @return Formato
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	/**
	 * Ajusta o formato de data no gráfico.
	 * @param dateFormat
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * Retorna a legenda horizontal
	 * @return Legenda
	 */
	public String getLegendaX() {
		return legendaX;
	}
	/**
	 * Ajusta a legenda horizontal.
	 * @param legendaX
	 */
	public void setLegendaX(String legendaX) {
		this.legendaX = legendaX;
	}
	/**
	 * Retorna a legenda vertical.
	 * @return Legenda
	 */
	public String getLegendaY() {
		return this.listaLegendaY.size() > 0 ? this.listaLegendaY.get(this.listaLegendaY.size()-1) : null;
	}
	/**
	 * Retorna o valor máximo.
	 * @return Legenda
	 */
	public Double getValorMaximo() {
		return this.listaValorMaximo.size() > 0 ? this.listaValorMaximo.get(this.listaValorMaximo.size()-1) : null;
	}
	/**
	 * Retorna a lista legenda vertical.
	 * @return Legenda
	 */
	public List<String> getListaLegendaY() {
		return this.listaLegendaY;
	}
	/**
	 * @return the listaValorMaximo
	 */
	public List<Double> getListaValorMaximo() {
		return listaValorMaximo;
	}
	/**
	 * Ajusta a legenda vertical.
	 * @param legendaY
	 */
	public void setLegendaY(String legendaY) {
		if (listaLegendaY.size() > 0)
			this.listaLegendaY.set(listaLegendaY.size()-1, legendaY);
		else
			addLegendaY(legendaY);
	}
	/**
	 * Ajusta o valor máximo.
	 * @param legendaY
	 */
	public void setValorMaximo(Double valorMaximo) {
		if (listaValorMaximo.size() > 0)
			this.listaValorMaximo.set(listaValorMaximo.size()-1, valorMaximo);
		else
			addValorMaximo(valorMaximo);
	}
	/**
	 * Ajusta a legenda vertical.
	 * @param legendaY
	 */
	public void addLegendaY(String legendaY) {
		listaLegendaY.add(legendaY);
	}
	/**
	 * Ajusta o valor máximo.
	 * @param valorMaximo
	 */
	public void addValorMaximo(Double valorMaximo) {
		listaValorMaximo.add(valorMaximo);
	}
	/**
	 * Retorna a definição dos dados no gráfico.
	 * @return Unidade
	 */
	public TickUnit getUnidade() {
		return unidade;
	}
	/**
	 * Ajusta a definição dos dados no gráfico.
	 * @param unidade
	 */
	protected void setUnidade(TickUnit unidade) {
		this.unidade = unidade;
	}
	
	/**
	 * @return the numberFormat
	 */
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}
	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}
	public LegendItemCollection removeDuplicados(LegendItemCollection lc) {
		LegendItemCollection lcRet = new LegendItemCollection();
		List<String> items = new ArrayList<String>();
		Iterator it = lc.iterator();
		while(it.hasNext()) {
			LegendItem li = (LegendItem)it.next();
			if (!items.contains(li.getDescription())) {
				items.add(li.getDescription());
				lcRet.add(li);
			}
		}
		return lcRet;
	}
}
