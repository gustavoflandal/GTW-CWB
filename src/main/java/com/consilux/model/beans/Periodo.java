package com.consilux.model.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean que representa um período (um intervalo de datas)
 * @author raoni
 *
 */
public class Periodo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Date dataInicio;
	Date dataFim;
	
	public Date getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public Date getDataFim() {
		return dataFim;
	}
	
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public Periodo(Date dataInicio, Date dataFim) {
		super();
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}
	
}
