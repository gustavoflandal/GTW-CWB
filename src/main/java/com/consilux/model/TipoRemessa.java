package com.consilux.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.consilux.conf.ConfiguracaoProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Classe que define os tipos de remessa do sistema.
 * @author raoni
 *
 */
public class TipoRemessa {

	private String codigo;
	private String descricao;
	private Integer idProcesso;
	private Integer autoInicial;
	private Integer autoFinal;
	private String serieInicial;
	private String serieFinal;
	private String modeloAIT;
	private String siglaCliente;


	/**
	* Constrói o objeto TipoRemessa a partir dos parâmetros dados.
	* @param codigo
	* @param descricao
	* @param idProcesso
	* @param autoInicial
	* @param autoFinal
	* @param serieInicial
	* @param serieFinal
	* @param modeloAIT
	* @param siglaCliente
	*/
	public TipoRemessa(String codigo, String descricao, Integer idProcesso,
			Integer autoInicial, Integer autoFinal, String serieInicial,
			String serieFinal, String modeloAIT, String siglaCliente) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
		this.idProcesso = idProcesso;
		this.autoInicial = autoInicial;
		this.autoFinal = autoFinal;
		this.serieInicial = serieInicial;
		this.serieFinal = serieFinal;
		this.modeloAIT = modeloAIT;
		this.siglaCliente = siglaCliente;
	}

	/**
	 * Retorna o valor do campo 'codigo' atual.
	 * @return the codigo
	 */
	public String getCodigo() {
		return this.codigo;
	}

	/**
	 * Retorna o valor do campo 'descricao' atual.
	 * @return the descricao
	 */
	public String getDescricao() {
		return this.descricao;
	}

	/**
	 * Retorna o valor do campo 'idProcesso' atual.
	 * @return the idProcesso
	 */
	public Integer getIdProcesso() {
		return this.idProcesso;
	}

	/**
	 * Retorna o valor do campo 'autoInicial' atual.
	 * @return the autoInicial
	 */
	public Integer getAutoInicial() {
		return this.autoInicial;
	}

	/**
	 * Retorna o valor do campo 'autoFinal' atual.
	 * @return the autoFinal
	 */
	public Integer getAutoFinal() {
		return this.autoFinal;
	}

	/**
	 * Retorna o valor do campo 'serieInicial' atual.
	 * @return the serieInicial
	 */
	public String getSerieInicial() {
		return this.serieInicial;
	}

	/**
	 * Retorna o valor do campo 'serieFinal' atual.
	 * @return the serieFinal
	 */
	public String getSerieFinal() {
		return this.serieFinal;
	}

	/**
	 * Retorna o valor do campo 'modeloAIT' atual.
	 * @return the modeloAIT
	 */
	public String getModeloAIT() {
		return this.modeloAIT;
	}

	/**
	 * Retorna o valor do campo 'siglaCliente' atual.
	 * @return the siglaCliente
	 */
	public String getSiglaCliente() {
		return this.siglaCliente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoRemessa other = (TipoRemessa) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return descricao;
	}

	public static Collection<TipoRemessa> buscarTiposRemessa() {
		
		ArrayList<TipoRemessa> lRet = Lists.newArrayList((ConfiguracaoProvider.getInstance()
			.getConfiguracaoRemessa().getMapaTipos().values()));
		Collections.sort(lRet, Ordering.usingToString());
		
		return lRet;
	}
}
