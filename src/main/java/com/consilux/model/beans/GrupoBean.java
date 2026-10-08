/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 14/01/2008

  Descricao: Bean de informações do formulário de cadastro de grupo.

  Historico:

    $Log: GrupoBean.java,v $
    Revision 1.4  2009/04/17 20:43:44  raoni
    Adicionado construtor.

    Revision 1.3  2009/01/12 12:49:45  fos
    Recuperação de repositório.

    Revision 1.1  2008/01/15 14:21:17  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model.beans;

/**
 * Bean de informações do formulário de cadastro de grupo.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/04/17 20:43:44 $ $Author: raoni $
 */
public class GrupoBean {
	Integer id = 0;
	String descricao = "";
	
	public GrupoBean(Integer id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id Novo valor para o atributo id.
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return Retorna o valor de descricao atual.
	 */
	public String getDescricao() {
		return descricao;
	}
	/**
	 * @param descricao Novo valor para o atributo descricao.
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
