package com.consilux.infra;

import javax.servlet.http.HttpSession;

public class Progresso implements SessaoFinaliza {
	private ProgressoProvider progressoProvider = null;
	private Integer id = null;
	private Double progresso = 0d;
	private String status = "";
	private Boolean terminado = false;
	/**
	 * @param progresso
	 * @param status
	 */
	protected Progresso(HttpSession se, ProgressoProvider pv, Integer id) {
		super();
		this.progressoProvider = pv;
		this.id = id;
		SessaoFinalizaManager.adicSessaoFinaliza(se, this);		
	}
	/**
	 * @return the progresso
	 */
	public Double getProgresso() {
		return progresso;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * @param progresso the progresso to set
	 */
	public void setProgresso(Double progresso) {
		this.progresso = progresso;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the terminado
	 */
	public Boolean getTerminado() {
		return terminado;
	}
	/**
	 * @param terminado the terminado to set
	 */
	public void terminado() {
		this.terminado = true;
	}
	@Override
	public void doFinaliza(HttpSession sessao) throws Exception {
		if (progressoProvider != null)
			progressoProvider.removeProgresso(this.id);
	}
}
