package com.consilux.ui.tabela;
/**********************************************************************************



  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 19/05/2010

  Descricao: Sub classe que encapsula os dados de uma linha da TabelaImagensEmProcessamento.


 *********************************************************************************/

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.consilux.model.IndicadoresProcessamento;


	/**
	 * Classe interna que encapsula os dados de uma linha na tabela.
	 * @author fos
	 * @version $Revision$ $Date$ $Author$
	 */

public class LinhaImagensEmProcessamento {

	private Integer idProcesso;
	private String nomeProcesso;
	private Date data;
	private Integer total = 0;
	private Map<Integer, Integer> totalEnquadramento = new TreeMap<Integer, Integer>();
	private Boolean espera;

	
	/**
	* Constrói o objeto DataImagensEmProcessamento a partir dos parâmetros dados.
	* @param idProcesso
	* @param idEnquadramento
	* @param data
	* @param total
	* @param espera
	*/
	protected LinhaImagensEmProcessamento(Integer idProcesso, String nomeProcesso, Date data, Boolean espera) {
		super();
		this.idProcesso = idProcesso;
		this.nomeProcesso = nomeProcesso;
		this.data = data;
		this.espera = espera;
	}
	/**
	 * Retorna o valor do campo 'idProcesso' atual.
	 * @return the idProcesso
	 */
	public Integer getIdProcesso() {
		return this.idProcesso;
	}
	/**
	 * Retorna o valor do campo 'nomeProcesso' atual.
	 * @return the nomeProcesso
	 */
	public String getNomeProcesso() {
		return this.nomeProcesso;
	}
	/**
	 * Retorna o valor do campo 'data' atual.
	 * @return the data
	 */
	public Date getData() {
		return this.data;
	}
	/**
	 * Retorna o valor do campo 'total' atual.
	 * @return the total
	 */
	public Integer getTotal() {
		return this.total;
	}
	/**
	 * Retorna o valor do campo 'totalEnquadramento' atual.
	 * @return the totaisEnquadramento
	 */
	public Map<Integer, Integer> getTotalEnquadramento() {
		return this.totalEnquadramento;
	}
	/**
	 * Retorna o valor do campo 'espera' atual.
	 * @return the espera
	 */
	public Boolean getEspera() {
		return this.espera;
	}

	public Boolean getAtrasado() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1 * IndicadoresProcessamento.NUMERO_DIAS_ATRASO);
		return cal.getTime().after(data);
	}
	
	protected void addTotalEnquadramento(Integer enquadramento, Integer total) {
		this.totalEnquadramento.put(enquadramento, total);
		this.total += total;
	}
}