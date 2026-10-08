/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 29/04/2008

  Descricao: Classe abstrata para busca de informações do cadastro de veículos.

  Historico:

    $Log: Cadastro.java,v $
    Revision 1.5  2009/05/08 18:51:21  fos
    Agora busca a espécie selecionada durante o processamento também.

    Revision 1.4  2009/04/03 15:44:19  fos
    Unificada a busca de marca, marca_processo e modelo.

    Revision 1.3  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.1  2008/05/08 21:25:30  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.util.Date;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/05/08 18:51:21 $ $Author: fos $
 */
public abstract class Cadastro {
	private String placa;
	private Integer idMarca;
	private String marca;
	private Integer idMarcaCliente;
	private String marcaCliente;
	private Integer idMarcaProcesso;
	private String marcaProcesso;
	private Integer idEspecieProcesso;
	private String especieProcesso;
	private String ufProcesso;
	private String cor;
	private Integer ano;
	private Integer idEspecie;
	private String especie;
	private String uf;
	private String tipo;
	private String categoria;
	private String situacao;
	private String localidade;
	private Date dataAtualizacao;
	private String classificacaoCad;

	/**
	 * Constrói o objeto Cadastro com os seus respectivos atributos.
	 * @param placa Placa do veículo.
	 * @param marca Marca do veículo segundo o cadastro
	 * @param marca Marca Cliente do veículo segundo o cadastro
	 * @param idMarcaProcesso Identificador da marca do veículo segundo o cadastro gerado pelos digitadores.
	 * @param idEspecieProcesso Identificador da espécie do veículo segundo o cadastro gerado pelos digitadores.
	 * @param ufProcesso Identificador da UF do veículo segundo o cadastro gerado pelos digitadores.
	 * @param cor Cor do veículo segundo o cadastro
	 * @param ano Ano do veículo segundo o cadastro
	 * @param especie Espécie qual se encaixa o veículo segundo o cadastro
	 * @param especie UF qual se encaixa o veículo segundo o cadastro
	 * @param tipo Tipo do veículo segundo o cadastro
	 * @param categoria Categoria qual se encaixa o veículo segundo o cadastro
	 * @param situacao Situação atual do veículo no cadastro
	 * @param localidade Localidade do veículo segundo o cadastro
	 * @param dataAtualizacao Data da última atualização do veículo no cadastro
	 */
	public Cadastro(String placa, Integer idMarca, String marca, Integer idMarcaCliente, String marcaCliente,
			Integer idMarcaProcesso, String marcaProcesso,
			Integer idEspecieProcesso, String ufProcesso, String especieProcesso,
			String cor, Integer ano, Integer idEspecie, String especie, String uf,
			String tipo, String categoria, String situacao, String localidade,
			Date dataAtualizacao, String classificacaoCad) {
		super();
		this.placa = placa;
		this.idMarca = idMarca;
		this.marca = marca;
		this.idMarcaCliente = idMarcaCliente;
		this.marcaCliente = marcaCliente;
		this.idMarcaProcesso = idMarcaProcesso;
		this.marcaProcesso = marcaProcesso;
		this.idEspecieProcesso = idEspecieProcesso;
		this.ufProcesso = ufProcesso;
		this.especieProcesso = especieProcesso;
		this.cor = cor;
		this.ano = ano;
		this.idEspecie = idEspecie;
		this.especie = especie;
		this.uf = uf;
		this.tipo = tipo;
		this.categoria = categoria;
		this.situacao = situacao;
		this.localidade = localidade;
		this.dataAtualizacao = dataAtualizacao;
		this.classificacaoCad = classificacaoCad;
	}

	/**
	 * @return Retorna o valor de placa atual.
	 */
	public String getPlaca() {
		return placa;
	}
 
	public String getMarca() {
		return marca;
	}

	/**
	 * @return Retorna o valor de cor atual.
	 */
	public String getCor() {
		return cor;
	}

	/**
	 * @return Retorna o valor de ano atual.
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * @return Retorna o valor de especie atual.
	 */
	public String getEspecie() {
		return especie;
	}
	
	/**
	 * Retorna o valor do campo 'uf' atual.
	 * @return the uf
	 */
	public String getUf() {
		return this.uf;
	}

	/**
	 * @return Retorna o valor de tipo atual.
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @return Retorna o valor de categoria atual.
	 */
	public String getCategoria() {
		return categoria;
	}

	/**
	 * @return Retorna o valor de situacao atual.
	 */
	public String getSituacao() {
		return situacao;
	}

	/**
	 * @return Retorna o valor de localidade atual.
	 */
	public String getLocalidade() {
		return localidade;
	}

	/**
	 * @return Retorna o valor de dataAtualizacao atual.
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	
	public String getMarcaDisponivel() {
		return marca == null ? (marcaCliente == null ? marcaProcesso : marcaCliente) : marca;
	}
	
	public String getMarcaCliente() {
		return marcaCliente;
	}
	
	public String getEspecieDisponivel() {
		return especie == null ? especieProcesso : especie;
	}
	
	public String getUfDisponivel() {
		return uf == null ? ufProcesso : uf;
	}
	 
	public Integer getIdMarcaDisponivel() {
		return idMarcaProcesso != null ? idMarcaProcesso : (idMarcaCliente != null ? idMarcaCliente : idMarca);
	}
	
	public Integer getIdMarcaCliente() {
		return idMarcaCliente;
	}
	
	public Integer getIdEspecieDisponivel() {
		return idEspecieProcesso != null ? idEspecieProcesso : idEspecie;
	}
	
	public String getClassificacaoCad() {
		return classificacaoCad;
	}
	
}
