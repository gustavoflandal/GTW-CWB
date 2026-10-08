/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2007

  Descricao: Bean de informações do formulário de identificação do condutor infrator

  Historico:

    $Log: NAIBean.java,v $
    Revision 1.4  2009/01/12 12:49:45  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.model.beans;

public class NAIBean {
	/**
	 * Identificação da infração
	 */
	private String infracao = "";
	/**
	 * Placa do veículo
	 */
	private String placa = "";
	/**
	 * Data da infração
	 */
	private String dataInfracao = "";
	/**
	 * Nome do infrator
	 */
	private String nome = "";
	/**
	 * Endereço do infrator
	 */
	private String endereco = "";
	/**
	 * Cidade do infrator
	 */
	private String cidade = "";
	/**
	 * UF do infrator
	 */
	private String UF = "";
	/**
	 * CEP do infrator
	 */
	private String CEP = "";
	/**
	 * CPF do infrator
	 */
	private String CPF = "";
	/**
	 * RG do infrator
	 */
	private String RG = "";
	/**
	 * Telefone do infrator
	 */
	private String telefone = "";
	/**
	 * Documento da CNH
	 */
	private String docCNH = "";
	/**
	 * Registro da CNH
	 */
	private String regCNH = "";
	/**
	 * UF da CNH
	 */
	private String UFCNH = "";
	/**
	 * Data de entrada do formulário de identificação
	 */
	private String dataEntrada = "";
	
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getCPF() {
		return CPF;
	}
	public void setCPF(String cpf) {
		CPF = cpf;
	}
	public String getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(String dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public String getDataInfracao() {
		return dataInfracao;
	}
	public void setDataInfracao(String dataInfracao) {
		this.dataInfracao = dataInfracao;
	}
	public String getDocCNH() {
		return docCNH;
	}
	public void setDocCNH(String docCNH) {
		this.docCNH = docCNH;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getInfracao() {
		return infracao;
	}
	public void setInfracao(String infracao) {
		this.infracao = infracao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public String getRegCNH() {
		return regCNH;
	}
	public void setRegCNH(String regCNH) {
		this.regCNH = regCNH;
	}
	public String getRG() {
		return RG;
	}
	public void setRG(String rg) {
		RG = rg;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getUFCNH() {
		return UFCNH;
	}
	public void setUFCNH(String ufcnh) {
		UFCNH = ufcnh;
	}
	public String getUF() {
		return UF;
	}
	public void setUF(String uf) {
		UF = uf;
	}
	public String getCEP() {
		return CEP;
	}
	public void setCEP(String cep) {
		CEP = cep;
	}
}
