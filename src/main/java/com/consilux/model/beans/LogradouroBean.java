/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 12/03/2009

  Descricao: Bean de informações do formulário de cadastro de logradouro.

  Historico:

    $Log: LogradouroBean.java,v $
    Revision 1.1  2009/03/18 17:18:53  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model.beans;


/**
 * Bean de informações do formulário de cadastro de logradouro.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1.2.1 $ $Date: 2009/06/12 18:15:09 $ $Author: charles.maske $
 */
public class LogradouroBean {
	private Integer id = 0;
	private String descricao = "";
	private String latitude = "";
	private String longitude = "";
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
