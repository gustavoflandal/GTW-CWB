package com.consilux.model.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean que representa um agendamento para processamento direto
 * @author raoni
 */
public class AgendamentoProcessamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idInfracao;
	private int idProcesso;
	private Date dataRequisicao;
	private int idUsuario;
	private int statusAgendamento;
	private Integer idInconsistencia;
	private String msgErro;
	
	public AgendamentoProcessamentoBean() {
		//ctor padrão java-beans
	}
	
	public int getIdInfracao() {
		return idInfracao;
	}
	
	public void setIdInfracao(int idInfracao) {
		this.idInfracao = idInfracao;
	}
	
	public int getIdProcesso() {
		return idProcesso;
	}
	
	public void setIdProcesso(int idProcesso) {
		this.idProcesso = idProcesso;
	}
	
	public Date getDataRequisicao() {
		return dataRequisicao;
	}
	
	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}
	
	public int getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public int getStatusAgendamento() {
		return statusAgendamento;
	}
	
	public void setStatusAgendamento(int statusAgendamento) {
		this.statusAgendamento = statusAgendamento;
	}
	
	public Integer getIdInconsistencia() {
		return idInconsistencia;
	}
	
	public void setIdInconsistencia(Integer idInconsistencia) {
		this.idInconsistencia = idInconsistencia;
	}
	
	public String getMsgErro() {
		return msgErro;
	}
	
	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}
	
}
