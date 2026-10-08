package com.consilux.model.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean que representa o processamento de um usuário do GTW.
 * @author raoni
 *
 */
public class TempoProcessamentoBean implements Serializable {

	public enum Classificacao {
		
		GERAL(0),
		DETALHE1(1),
		DETALHE2(2);
		
		private Classificacao(int codigo) {
			this.codigo = codigo;
		}
		
		private int codigo;
		
		public int getCodigo() {
			return this.codigo;
		}
		
		public static Classificacao valueOfCodigo(int codigo) {
			for (Classificacao cla : values()) {
				if (cla.getCodigo() == codigo)
					return cla;
			}
			return null;
		}		
		
	}
	
	private static final long serialVersionUID = 1L;

	private Integer idUsuario;
	private Date dataInicioCliente;
	private Integer tempoGasto;
	private Classificacao classificacao;
	private Integer identificador;
	private String subIdentificador;
	
	/**
	 * Recupera o id do usuário que realizou ou processamento.
	 * @return
	 */
	public Integer getIdUsuario() {
		return idUsuario;
	}
	
	
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	/**
	 * Recupera a data de início do processamento no cliente (BROWSER).
	 * @return
	 */
	public Date getDataInicioCliente() {
		return dataInicioCliente;
	}
	
	public void setDataInicioCliente(Date dataInicioCliente) {
		this.dataInicioCliente = dataInicioCliente;
	}
	
	/**
	 * Recupera o tempo gasto na operação pelo cliente.
	 * @return
	 */
	public Integer getTempoGasto() {
		return tempoGasto;
	}
	
	public void setTempoGasto(Integer tempoGasto) {
		this.tempoGasto = tempoGasto;
	}
	
	/**
	 * Recupera a classificação (o tipo) da operação de processamento,
	 * @return
	 */
	public Classificacao getClassificacao() {
		return classificacao;
	}
	
	public void setClassificacao(Classificacao classificacao) {
		this.classificacao = classificacao;
	}
	
	/**
	 * Recupera o identificador da operação. O identificador é um int genérico, pode
	 * significar um enquadramento, uma EtapaProcesso, etc. 
	 * @return
	 */
	public Integer getIdentificador() {
		return identificador;
	}
	
	public void setIdentificador(Integer identificador) {
		this.identificador = identificador;
	}

	public void setSubIdentificador(String subIdentificador) {
		this.subIdentificador = subIdentificador;
	}
	
	public String getSubIdentificador() {
		return this.subIdentificador;
	}
	
}
