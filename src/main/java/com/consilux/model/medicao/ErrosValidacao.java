/**********************************************************************************
  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 09/09/2014
*********************************************************************************/

package com.consilux.model.medicao;

import java.io.Serializable;

/**
 * Classe de negócio para busca de DADOS para os relatórios de medição - Relatório de Erros de Validação
 * @author Luiz Fernando Amaral - Consilux Tecnologia
 * Data: 09/09/2014
 */

public  class ErrosValidacao implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	Integer movimentoLote;
	String tipoApait;
	Integer qtdeErros;
	String tipoEquipamento;

	public ErrosValidacao(Integer movimentoLote,	
						  String tipoApait,
						  Integer qtdeErros){
		super();
		this.movimentoLote=movimentoLote;
		this.tipoApait=tipoApait;
		this.qtdeErros=qtdeErros;
	}
	
	
	public ErrosValidacao(String tipoEquipamento,
						  Integer qtdeErros){
			super();
			this.tipoEquipamento = tipoEquipamento;
			this.qtdeErros=qtdeErros;
	}

	public Integer getMovimentoLote() {
		return movimentoLote;
	}

	public void setMovimentoLote(Integer movimentoLote) {
		this.movimentoLote = movimentoLote;
	}

	public String getTipoApait() {
		return tipoApait;
	}

	public void setTipoApait(String tipoApait) {
		this.tipoApait = tipoApait;
	}

	public Integer getQtdeErros() {
		return qtdeErros;
	}

	public void setQtdeErros(Integer qtdeErros) {
		this.qtdeErros = qtdeErros;
	}
	
	public String getTipoEquipamento() {
		return tipoEquipamento;
	}

	public void setTipoEquipamento(String tipoEquipamento) {
		this.tipoEquipamento = tipoEquipamento;
	}
	
}
