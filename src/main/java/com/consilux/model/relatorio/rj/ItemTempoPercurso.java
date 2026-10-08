/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 09/11/2016

*********************************************************************************/
package com.consilux.model.relatorio.rj;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 09/11/2016
 */
public  class ItemTempoPercurso implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	String data;
	Integer diaSemana;
	String diaSemanaDesc;
	Integer dia;
	Integer hora;
	String horaDesc;
	Integer periodo;
	String descPeriodo;
	Integer mes;
	Integer ano;
	Date dtData;
	
	Integer idPercurso;
	String nomePercurso;
	Integer idTipoPercurso;
	String descTipoPercurso;
	Integer idLocalMontante;
	Integer idLocalJusante;
	
	Integer tempoMedio;
	Double indiceMobilidade;
	Integer velocidadePercurso;
	Integer volumeVeicular;
	
	Integer tempoMedioDomingo;
	Integer tempoMedioSegunda;
	Integer tempoMedioTerca;
	Integer tempoMedioQuarta;
	Integer tempoMedioQuinta;
	Integer tempoMedioSexta;
	Integer tempoMedioSabado;
	Integer tempoMedioDiasUteis;
	
	Time tempoMedioDomingoTime;
	Time tempoMedioSegundaTime;
	Time tempoMedioTercaTime;
	Time tempoMedioQuartaTime;
	Time tempoMedioQuintaTime;
	Time tempoMedioSextaTime;
	Time tempoMedioSabadoTime;
	Time tempoMedioDiasUteisTime;
	
	Integer[] celulasTempoMedio = new Integer[31];
	Double[] celulasIndiceMobilidade = new Double[31];
	Integer[] celulasVelocidadePercurso = new Integer[31];
	Integer[] celulasVolumeVeicular = new Integer[31];
	
	
	public ItemTempoPercurso() {
	
	}
	
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 09/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 01 - Tempo de percurso, volume, velocidade e mobilidade de trechos por hora e data.
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 02 - Tempo de percurso, volume, velocidade e mobilidade de corredores por hora e data.
	 */
	public ItemTempoPercurso(Integer idPercurso,
							 String nomePercurso,
							 Integer hora,
							 String horaDesc,
							 Integer[] celulasTempoMedio,
							 Double[] celulasIndiceMobilidade,
							 Integer[] celulasVelocidadePercurso,
							 Integer[] celulasVolumeVeicular,
							 Integer tempoMedio,
							 Double indiceMobilidade,
							 Integer velocidadePercurso,
							 Integer volumeVeicular){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasTempoMedio = celulasTempoMedio;
		this.celulasIndiceMobilidade = celulasIndiceMobilidade;
		this.celulasVelocidadePercurso = celulasVelocidadePercurso;
		this.celulasVolumeVeicular = celulasVolumeVeicular;
		this.tempoMedio = tempoMedio;
		this.indiceMobilidade = indiceMobilidade;
		this.velocidadePercurso = velocidadePercurso;
		this.volumeVeicular = volumeVeicular;
	}
	

	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 03 e 04 - Média horária de tempo de percurso de trechos/corredores por dia da semana.
	 */
	public ItemTempoPercurso(Integer idPercurso,
			 				 String nomePercurso,
			 				 Integer hora,
			 				 String horaDesc,
			 				 Time tempoMedioDomingoTime,
			 				 Time tempoMedioSegundaTime,
			 				 Time tempoMedioTercaTime,
			 				 Time tempoMedioQuartaTime,
			 				 Time tempoMedioQuintaTime,
			 				 Time tempoMedioSextaTime,
			 				 Time tempoMedioSabadoTime,
			 				 Time tempoMedioDiasUteisTime){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.tempoMedioDomingoTime = tempoMedioDomingoTime;
		this.tempoMedioSegundaTime = tempoMedioSegundaTime;
		this.tempoMedioTercaTime = tempoMedioTercaTime;
		this.tempoMedioQuartaTime = tempoMedioQuartaTime;
		this.tempoMedioQuintaTime = tempoMedioQuintaTime;
		this.tempoMedioSextaTime = tempoMedioSextaTime;
		this.tempoMedioSabadoTime = tempoMedioSabadoTime;
		this.tempoMedioDiasUteisTime = tempoMedioDiasUteisTime;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 22/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 03 e 04 - Média horária de tempo de percurso de trechos/corredores por dia da semana.
	 */
	public ItemTempoPercurso(Integer idPercurso,
			 				 String nomePercurso,
			 				 Integer hora,
			 				 String horaDesc,
			 				 Integer tempoMedioDomingo,
			 				 Integer tempoMedioSegunda,
			 				 Integer tempoMedioTerca,
			 				 Integer tempoMedioQuarta,
			 				 Integer tempoMedioQuinta,
			 				 Integer tempoMedioSexta,
			 				 Integer tempoMedioSabado,
			 				 Integer tempoMedioDiasUteis){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.tempoMedioDomingo = tempoMedioDomingo;
		this.tempoMedioSegunda = tempoMedioSegunda;
		this.tempoMedioTerca = tempoMedioTerca;
		this.tempoMedioQuarta = tempoMedioQuarta;
		this.tempoMedioQuinta = tempoMedioQuinta;
		this.tempoMedioSexta = tempoMedioSexta;
		this.tempoMedioSabado = tempoMedioSabado;
		this.tempoMedioDiasUteis = tempoMedioDiasUteis;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 05 e 06 - Média horária do tempo de percurso e volume médio de trechos/corredores em dias úteis por mês.
	 */
	public ItemTempoPercurso(Integer idPercurso,
			 				 String nomePercurso,
			 				 Integer hora,
			 				 String horaDesc,
			 				 Integer tempoMedio,
			 				 Double indiceMobilidade,
			 				 Integer velocidadePercurso,
			 				 Integer volumeVeicular){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.tempoMedio = tempoMedio;
		this.indiceMobilidade = indiceMobilidade;
		this.velocidadePercurso = velocidadePercurso;
		this.volumeVeicular = volumeVeicular;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 23/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 07 e 08 - Média diária do tempo de percurso e volume médio de trechos/corredores mensal por data.
	 */
	public ItemTempoPercurso(Integer idPercurso,
							 String nomePercurso,
							 Date dtData,
							 Integer dia,
							 Integer diaSemana,
							 String diaSemanaDesc,
							 Integer tempoMedio,
							 Double indiceMobilidade,
							 Integer velocidadePercurso,
							 Integer volumeVeicular){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.dtData = dtData;
		this.dia = dia;
		this.diaSemana = diaSemana;
		this.diaSemanaDesc = diaSemanaDesc;
		this.tempoMedio = tempoMedio;
		this.indiceMobilidade = indiceMobilidade;
		this.velocidadePercurso = velocidadePercurso;
		this.volumeVeicular = volumeVeicular;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 25/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 09 e 10 - Tempo de percurso, volume, velocidade e mobilidade de trechos/corredores por período de pico e data.
	 */
	public ItemTempoPercurso(Integer idPercurso,
							 String nomePercurso,
							 Integer periodo,
							 String descPeriodo,
							 Integer[] celulasTempoMedio,
							 Double[] celulasIndiceMobilidade,
							 Integer[] celulasVelocidadePercurso,
							 Integer[] celulasVolumeVeicular,
							 Integer tempoMedio,
							 Double indiceMobilidade,
							 Integer velocidadePercurso,
							 Integer volumeVeicular,
							 String strRelatorio){

		super();
		this.idPercurso = idPercurso;
		this.nomePercurso = nomePercurso;
		this.periodo = periodo;
		this.descPeriodo = descPeriodo;
		this.celulasTempoMedio = celulasTempoMedio;
		this.celulasIndiceMobilidade = celulasIndiceMobilidade;
		this.celulasVelocidadePercurso = celulasVelocidadePercurso;
		this.celulasVolumeVeicular = celulasVolumeVeicular;
		this.tempoMedio = tempoMedio;
		this.indiceMobilidade = indiceMobilidade;
		this.velocidadePercurso = velocidadePercurso;
		this.volumeVeicular = volumeVeicular;
	}
	

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}


	public Integer getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}


	public String getDiaSemanaDesc() {
		return diaSemanaDesc;
	}
	public void setDiaSemanaDesc(String diaSemanaDesc) {
		this.diaSemanaDesc = diaSemanaDesc;
	}
	
	
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	
	
	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}


	public String getHoraDesc() {
		return horaDesc;
	}
	public void setHoraDesc(String horaDesc) {
		this.horaDesc = horaDesc;
	}
	
	
	public Integer getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}


	public String getDescPeriodo() {
		return descPeriodo;
	}
	public void setDescPeriodo(String descPeriodo) {
		this.descPeriodo = descPeriodo;
	}


	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}


	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	
	public Date getDtData() {
		return dtData;
	}
	public void setDtData(Date dtData) {
		this.dtData = dtData;
	}


	public Integer getIdPercurso() {
		return idPercurso;
	}
	public void setIdPercurso(Integer idPercurso) {
		this.idPercurso = idPercurso;
	}


	public String getNomePercurso() {
		return nomePercurso;
	}
	public void setNomePercurso(String nomePercurso) {
		this.nomePercurso = nomePercurso;
	}


	public Integer getIdTipoPercurso() {
		return idTipoPercurso;
	}
	public void setIdTipoPercurso(Integer idTipoPercurso) {
		this.idTipoPercurso = idTipoPercurso;
	}


	public String getDescTipoPercurso() {
		return descTipoPercurso;
	}
	public void setDescTipoPercurso(String descTipoPercurso) {
		this.descTipoPercurso = descTipoPercurso;
	}
	
	
	public Integer getIdLocalMontante() {
		return idLocalMontante;
	}
	public void setIdLocalMontante(Integer idLocalMontante) {
		this.idLocalMontante = idLocalMontante;
	}
	
	
	public Integer getIdLocalJusante() {
		return idLocalJusante;
	}
	public void setIdLocalJusante(Integer idLocalJusante) {
		this.idLocalJusante = idLocalJusante;
	}


	public Integer getTempoMedio() {
		return tempoMedio;
	}
	public void setTempoMedio(Integer tempoMedio) {
		this.tempoMedio = tempoMedio;
	}


	public Double getIndiceMobilidade() {
		return indiceMobilidade;
	}
	public void setIndiceMobilidade(Double indiceMobilidade) {
		this.indiceMobilidade = indiceMobilidade;
	}


	public Integer getVelocidadePercurso() {
		return velocidadePercurso;
	}
	public void setVelocidadePercurso(Integer velocidadePercurso) {
		this.velocidadePercurso = velocidadePercurso;
	}


	public Integer getVolumeVeicular() {
		return volumeVeicular;
	}
	public void setVolumeVeicular(Integer volumeVeicular) {
		this.volumeVeicular = volumeVeicular;
	}
	
	
	public Integer getTempoMedioDomingo() {
		return tempoMedioDomingo;
	}
	public void setTempoMedioDomingo(Integer tempoMedioDomingo) {
		this.tempoMedioDomingo = tempoMedioDomingo;
	}
	
	
	public Integer getTempoMedioSegunda() {
		return tempoMedioSegunda;
	}
	public void setTempoMedioSegunda(Integer tempoMedioSegunda) {
		this.tempoMedioSegunda = tempoMedioSegunda;
	}


	public Integer getTempoMedioTerca() {
		return tempoMedioTerca;
	}
	public void setTempoMedioTerca(Integer tempoMedioTerca) {
		this.tempoMedioTerca = tempoMedioTerca;
	}


	public Integer getTempoMedioQuarta() {
		return tempoMedioQuarta;
	}
	public void setTempoMedioQuarta(Integer tempoMedioQuarta) {
		this.tempoMedioQuarta = tempoMedioQuarta;
	}


	public Integer getTempoMedioQuinta() {
		return tempoMedioQuinta;
	}
	public void setTempoMedioQuinta(Integer tempoMedioQuinta) {
		this.tempoMedioQuinta = tempoMedioQuinta;
	}


	public Integer getTempoMedioSexta() {
		return tempoMedioSexta;
	}
	public void setTempoMedioSexta(Integer tempoMedioSexta) {
		this.tempoMedioSexta = tempoMedioSexta;
	}


	public Integer getTempoMedioSabado() {
		return tempoMedioSabado;
	}
	public void setTempoMedioSabado(Integer tempoMedioSabado) {
		this.tempoMedioSabado = tempoMedioSabado;
	}


	public Integer getTempoMedioDiasUteis() {
		return tempoMedioDiasUteis;
	}
	public void setTempoMedioDiasUteis(Integer tempoMedioDiasUteis) {
		this.tempoMedioDiasUteis = tempoMedioDiasUteis;
	}
	
	

	public Time getTempoMedioDomingoTime() {
		return tempoMedioDomingoTime;
	}
	public void setTempoMedioDomingoTime(Time tempoMedioDomingoTime) {
		this.tempoMedioDomingoTime = tempoMedioDomingoTime;
	}


	public Time getTempoMedioSegundaTime() {
		return tempoMedioSegundaTime;
	}
	public void setTempoMedioSegundaTime(Time tempoMedioSegundaTime) {
		this.tempoMedioSegundaTime = tempoMedioSegundaTime;
	}


	public Time getTempoMedioTercaTime() {
		return tempoMedioTercaTime;
	}
	public void setTempoMedioTercaTime(Time tempoMedioTercaTime) {
		this.tempoMedioTercaTime = tempoMedioTercaTime;
	}


	public Time getTempoMedioQuartaTime() {
		return tempoMedioQuartaTime;
	}
	public void setTempoMedioQuartaTime(Time tempoMedioQuartaTime) {
		this.tempoMedioQuartaTime = tempoMedioQuartaTime;
	}


	public Time getTempoMedioQuintaTime() {
		return tempoMedioQuintaTime;
	}
	public void setTempoMedioQuintaTime(Time tempoMedioQuintaTime) {
		this.tempoMedioQuintaTime = tempoMedioQuintaTime;
	}


	public Time getTempoMedioSextaTime() {
		return tempoMedioSextaTime;
	}
	public void setTempoMedioSextaTime(Time tempoMedioSextaTime) {
		this.tempoMedioSextaTime = tempoMedioSextaTime;
	}


	public Time getTempoMedioSabadoTime() {
		return tempoMedioSabadoTime;
	}
	public void setTempoMedioSabadoTime(Time tempoMedioSabadoTime) {
		this.tempoMedioSabadoTime = tempoMedioSabadoTime;
	}


	public Time getTempoMedioDiasUteisTime() {
		return tempoMedioDiasUteisTime;
	}
	public void setTempoMedioDiasUteisTime(Time tempoMedioDiasUteisTime) {
		this.tempoMedioDiasUteisTime = tempoMedioDiasUteisTime;
	}


	public Integer[] getCelulasTempoMedio() {
		return celulasTempoMedio;
	}
	public void setCelulasTempoMedio(Integer[] celulasTempoMedio) {
		this.celulasTempoMedio = celulasTempoMedio;
	}


	public Double[] getCelulasIndiceMobilidade() {
		return celulasIndiceMobilidade;
	}
	public void setCelulasIndiceMobilidade(Double[] celulasIndiceMobilidade) {
		this.celulasIndiceMobilidade = celulasIndiceMobilidade;
	}


	public Integer[] getCelulasVelocidadePercurso() {
		return celulasVelocidadePercurso;
	}
	public void setCelulasVelocidadePercurso(Integer[] celulasVelocidadePercurso) {
		this.celulasVelocidadePercurso = celulasVelocidadePercurso;
	}


	public Integer[] getCelulasVolumeVeicular() {
		return celulasVolumeVeicular;
	}
	public void setCelulasVolumeVeicular(Integer[] celulasVolumeVeicular) {
		this.celulasVolumeVeicular = celulasVolumeVeicular;
	};	
}