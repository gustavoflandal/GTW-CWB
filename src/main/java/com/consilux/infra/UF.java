/**********************************************************************************

  Projeto: GTW-1.4.1
  Nome do Modulo: com.consilux.infra

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 14/04/2010

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.infra;


/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public enum UF {
	AC("Acre"),
	AL("Alagoas"),
	AM("Amazonas"),
	AP("Amapá"),
	BA("Bahia"),
	CE("Ceará"),
	DF("Distrito Federal"),
	ES("Espírito Santo"),
	GO("Goiás"),
	MA("Maranhão"),
	MG("Minas Gerais"),
	MS("Mato Grosso do Sul"),
	MT("Mato Grosso"),
	PA("Pará"),
	PB("Paraíba"),
	PE("Pernambuco"),
	PI("Piauí"),
	PR("Paraná"),
	RJ("Rio de Janeiro"),
	RN("Rio Grande do Norte"),
	RO("Rondônia"),
	RR("Roraima"),
	RS("Rio Grande do Sul"),
	SC("Santa Catarina"),
	SE("Sergipe"),
	SP("São Paulo"),
	TO("Tocantins");

	private String nomeCompleto;
	UF(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}
	public String getNomeCompleto() {
		return nomeCompleto;
	}
	public static UF valueOfId(String nomeCompleto) {
		for (UF uf: values()) {
			if (uf.getNomeCompleto().equals(nomeCompleto))
				return uf;
		}
		return null;
	}
}
