package com.consilux.conf;

import java.io.Serializable;

/**
 * Classe que representa a configuração do processamento das infrações.
 * Utilizado para customizar o comportamento do sistema durante as
 * etapas do processamento.
 * @author raoni
 */
public class ConfiguracaoProcessamento implements Serializable {

	private static final long serialVersionUID = 9193401790277648641L;

	private boolean comSugestaoPlaca;
	private boolean comObliteracao;
	private boolean comAjustaImagem;
	private boolean comMarcaProcesso; 
	private boolean comEspecieProcesso;
	private boolean comUfValidacao;
	private boolean confirmaObliteracao;
	private int alertaVelocidade;
	private boolean alertaIsento;
	private int tamanhoTarja;
	private boolean comValidacaoConsistentesAgendamento;
	
	public ConfiguracaoProcessamento() {
		// Construtor padrão sem parâmetros, padrão JavaBeans
	}

	public boolean isComSugestaoPlaca() {
		return comSugestaoPlaca;
	}

	public boolean isComObliteracao() {
		return comObliteracao;
	}

	public void setComObliteracao(boolean comObliteracao) {
		this.comObliteracao = comObliteracao;
	}

	public void setComSugestaoPlaca(boolean comSugestaoPlaca) {
		this.comSugestaoPlaca = comSugestaoPlaca;
	}

	public boolean isComMarcaProcesso() {
		return comMarcaProcesso;
	}

	public void setComMarcaProcesso(boolean comMarcaProcesso) {
		this.comMarcaProcesso = comMarcaProcesso;
	}

	public boolean isComEspecieProcesso() {
		return comEspecieProcesso;
	}

	public void setComEspecieProcesso(boolean comEspecieProcesso) {
		this.comEspecieProcesso = comEspecieProcesso;
	}

	/**
	 * Retorna o valor do campo 'comUfValidacao' atual.
	 * @return the comUfValidacao
	 */
	public boolean isComUfValidacao() {
		return this.comUfValidacao;
	}

	/**
	 * Ajusta o valor do campo 'comUfValidacao' no objeto.
	 * @param comUfValidacao the comUfValidacao to set
	 */
	public void setComUfValidacao(boolean comUfValidacao) {
		this.comUfValidacao = comUfValidacao;
	}

	public boolean isConfirmaObliteracao() {
		return confirmaObliteracao;
	}

	public void setConfirmaObliteracao(boolean confirmaObliteracao) {
		this.confirmaObliteracao = confirmaObliteracao;
	}

	public int getAlertaVelocidade() {
		return alertaVelocidade;
	}

	public boolean getAlertaIsento() {
		return alertaIsento;
	}

	public void setAlertaVelocidade(int alertaVelocidade) {
		this.alertaVelocidade = alertaVelocidade;
	}

	public void setAlertaIsento(boolean alertaIsento) {
		this.alertaIsento = alertaIsento;
	}

	public int getTamanhoTarja() {
		return tamanhoTarja;
	}

	public void setTamanhoTarja(int tamanhoTarja) {
		this.tamanhoTarja = tamanhoTarja;
	}

	/**
	 * Valor que define se na validação (3) será permitido processar com agendamento
	 * (antigo processa-direto) para as infrações consistentes.
	 * @return boolean
	 */
	public boolean isComValidacaoConsistentesAgendamento() {
		return comValidacaoConsistentesAgendamento;
	}

	public void setComValidacaoConsistentesAgendamento(
			boolean comValidacaoConsistentesAgendamento) {
		this.comValidacaoConsistentesAgendamento = comValidacaoConsistentesAgendamento;
	}

	public void setComAjustaImagem(boolean comAjustaImagem) {
		this.comAjustaImagem = comAjustaImagem;
	}

	public boolean isComAjustaImagem() {
		return comAjustaImagem;
	}
	
}
