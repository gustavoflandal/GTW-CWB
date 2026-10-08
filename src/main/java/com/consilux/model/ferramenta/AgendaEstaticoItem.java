package com.consilux.model.ferramenta;

/**********************************************************************************
Projeto: GTW
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Luiz Amaral
Data: 02/10/2014
*********************************************************************************/

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
* Classe de negócio para popula objeto de AgendaEstatico Itens
* @author Luiz Fernando Amaral - Consilux Tecnologia
* Data: 02/10/2014
*/
public  class AgendaEstaticoItem implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Date dtOperacao;
	Long idAgendaEstaticoItem;
	Long idAgendaEstatico;
	Time hraInicio;
	Time hraFim;
	int status;

	public AgendaEstaticoItem(Date dtOp, Long idAgendaEstatico, 
							  Long idAgendaEstaticoItem, Time hraIni, Time hraF,int status){
		this.dtOperacao = dtOp;
		this.idAgendaEstatico = idAgendaEstatico;
		this.idAgendaEstaticoItem = idAgendaEstaticoItem;
		this.hraInicio = hraIni;
		this.hraFim = hraF;
		this.status = status;
	}
	
	public AgendaEstaticoItem(){
	}

	public Date getDtOperacao() {
		return dtOperacao;
	}

	public void setDtOperacao(Date dtOperacao) {
		this.dtOperacao = dtOperacao;
	}

	public Long getIdAgendaEstaticoItem() {
		return idAgendaEstaticoItem;
	}

	public void setIdAgendaEstaticoItem(Long idAgendaEstaticoItem) {
		this.idAgendaEstaticoItem = idAgendaEstaticoItem;
	}

	public Long getIdAgendaEstatico() {
		return idAgendaEstatico;
	}

	public void setIdAgendaEstatico(Long idAgendaEstatico) {
		this.idAgendaEstatico = idAgendaEstatico;
	}

	public Time getHraInicio() {
		return hraInicio;
	}

	public void setHraInicio(Time hraInicio) {
		this.hraInicio = hraInicio;
	}

	public Time getHraFim() {
		return hraFim;
	}

	public void setHraFim(Time hraFim) {
		this.hraFim = hraFim;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusDesc() {
		
		if(status == 0){
			return "Desativado";
		}else{
			return "Ativo";
		}
	}

}