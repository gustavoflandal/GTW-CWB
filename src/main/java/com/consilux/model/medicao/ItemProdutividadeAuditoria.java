/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 11/08/2016

*********************************************************************************/
package com.consilux.model.medicao;
import java.io.Serializable;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 11/08/2016
 */
public  class ItemProdutividadeAuditoria implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;

	String dataProcessamento;
	String login;
	String auditor;
	String horarioInicioProcessamento;
	Integer valor0;
	Integer valor1;
	Integer valor2;
	Integer valor3;
	Integer valor4;
	Integer valor5;
	Integer valor6;
	Integer valor7;
	Integer valor8;
	Integer valor9;
	Integer valor10;
	Integer valor11;
	Integer valor12;
	Integer valor13;
	Integer valor14;
	Integer valor15;
	Integer valor16;
	Integer valor17;
	Integer valor18;
	Integer valor19;
	Integer valor20;
	Integer valor21;
	Integer valor22;
	Integer valor23;
	Integer total;
	

	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/08/2016
	 * @return Construtor para o relatório de Produtividade Auditoria
	 */
	public ItemProdutividadeAuditoria(String dataProcessamento, String login, String auditor, String horarioInicioProcessamento,
									  Integer valor0, Integer valor1, Integer valor2, Integer valor3, Integer valor4, Integer valor5, Integer valor6,
									  Integer valor7, Integer valor8, Integer valor9, Integer valor10, Integer valor11, Integer valor12, Integer valor13,
									  Integer valor14, Integer valor15, Integer valor16, Integer valor17, Integer valor18, Integer valor19, Integer valor20,
									  Integer valor21, Integer valor22, Integer valor23, Integer total) {

		super();
		this.dataProcessamento = dataProcessamento;
		this.login = login;
		this.auditor = auditor;
		this.horarioInicioProcessamento = horarioInicioProcessamento;
		this.valor0 = valor0;
		this.valor1 = valor1;
		this.valor2 = valor2;
		this.valor3 = valor3;
		this.valor4 = valor4;
		this.valor5 = valor5;
		this.valor6 = valor6;
		this.valor7 = valor7;
		this.valor8 = valor8;
		this.valor9 = valor9;
		this.valor10 = valor10;
		this.valor11 = valor11;
		this.valor12 = valor12;
		this.valor13 = valor13;
		this.valor14 = valor14;
		this.valor15 = valor15;
		this.valor16 = valor16;
		this.valor17 = valor17;
		this.valor18 = valor18;
		this.valor19 = valor19;
		this.valor20 = valor20;
		this.valor21 = valor21;
		this.valor22 = valor22;
		this.valor23 = valor23;
		this.total = total;
	}


	
	public String getDataProcessamento() {
		return dataProcessamento;
	}
	public void setDataProcessamento(String dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getHorarioInicioProcessamento() {
		return horarioInicioProcessamento;
	}
	public void setHorarioInicioProcessamento(String horarioInicioProcessamento) {
		this.horarioInicioProcessamento = horarioInicioProcessamento;
	}

	public Integer getValor0() {
		return valor0;
	}
	public void setValor0(Integer valor0) {
		this.valor0 = valor0;
	}

	public Integer getValor1() {
		return valor1;
	}
	public void setValor1(Integer valor1) {
		this.valor1 = valor1;
	}

	public Integer getValor2() {
		return valor2;
	}
	public void setValor2(Integer valor2) {
		this.valor2 = valor2;
	}

	public Integer getValor3() {
		return valor3;
	}
	public void setValor3(Integer valor3) {
		this.valor3 = valor3;
	}

	public Integer getValor4() {
		return valor4;
	}
	public void setValor4(Integer valor4) {
		this.valor4 = valor4;
	}

	public Integer getValor5() {
		return valor5;
	}
	public void setValor5(Integer valor5) {
		this.valor5 = valor5;
	}

	public Integer getValor6() {
		return valor6;
	}
	public void setValor6(Integer valor6) {
		this.valor6 = valor6;
	}

	public Integer getValor7() {
		return valor7;
	}
	public void setValor7(Integer valor7) {
		this.valor7 = valor7;
	}

	public Integer getValor8() {
		return valor8;
	}
	public void setValor8(Integer valor8) {
		this.valor8 = valor8;
	}

	public Integer getValor9() {
		return valor9;
	}
	public void setValor9(Integer valor9) {
		this.valor9 = valor9;
	}

	public Integer getValor10() {
		return valor10;
	}
	public void setValor10(Integer valor10) {
		this.valor10 = valor10;
	}

	public Integer getValor11() {
		return valor11;
	}
	public void setValor11(Integer valor11) {
		this.valor11 = valor11;
	}

	public Integer getValor12() {
		return valor12;
	}
	public void setValor12(Integer valor12) {
		this.valor12 = valor12;
	}

	public Integer getValor13() {
		return valor13;
	}
	public void setValor13(Integer valor13) {
		this.valor13 = valor13;
	}

	public Integer getValor14() {
		return valor14;
	}
	public void setValor14(Integer valor14) {
		this.valor14 = valor14;
	}

	public Integer getValor15() {
		return valor15;
	}
	public void setValor15(Integer valor15) {
		this.valor15 = valor15;
	}

	public Integer getValor16() {
		return valor16;
	}
	public void setValor16(Integer valor16) {
		this.valor16 = valor16;
	}

	public Integer getValor17() {
		return valor17;
	}
	public void setValor17(Integer valor17) {
		this.valor17 = valor17;
	}

	public Integer getValor18() {
		return valor18;
	}
	public void setValor18(Integer valor18) {
		this.valor18 = valor18;
	}

	public Integer getValor19() {
		return valor19;
	}
	public void setValor19(Integer valor19) {
		this.valor19 = valor19;
	}

	public Integer getValor20() {
		return valor20;
	}
	public void setValor20(Integer valor20) {
		this.valor20 = valor20;
	}

	public Integer getValor21() {
		return valor21;
	}
	public void setValor21(Integer valor21) {
		this.valor21 = valor21;
	}

	public Integer getValor22() {
		return valor22;
	}
	public void setValor22(Integer valor22) {
		this.valor22 = valor22;
	}

	public Integer getValor23() {
		return valor23;
	}
	public void setValor23(Integer valor23) {
		this.valor23 = valor23;
	}

	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
}
