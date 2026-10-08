
package com.consilux.exportalista;

import java.util.List;

/**
 * Classe que controla o grafico de contagem x tempo.
 * 
 * @author Edson Jan F Lopes - Consilux Tecnologia
 * @version $Revision: 1.0 $ $Date: 2009/08/21 $ $Author: fos $
 */
public class FluxoTempoBean extends ExportaLista{
	
	
	private Integer ano;
	private Integer mes;
	private Integer dia;
	private Integer hora;
	private Integer conta;
	private Byte pista;
	private String local;
	
	
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Byte getPista() {
		return pista;
	}
	public void setPista(Byte pista) {
		this.pista = pista;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}
	public Integer getConta() {
		return conta;
	}
	public void setConta(Integer conta) {
		this.conta = conta;
	}
	
	
	public static List<FluxoTempoBean> getLista(){
		
		List<FluxoTempoBean> l = (List<FluxoTempoBean>) getListaParaRelatorio();
		
		return l;
	}

}
