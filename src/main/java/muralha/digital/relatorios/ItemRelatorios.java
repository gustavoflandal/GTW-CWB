/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

*********************************************************************************/
package muralha.digital.relatorios;

import java.io.Serializable;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 10/01/2021
 */
public  class ItemRelatorios implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	Integer hora;
	String horaDesc;
	Integer idLocal;
	
	Integer[] celulasInteger = new Integer[18];
	
	Integer fluxoVeicular;
	Integer fluxoVeicularMoto;
	Integer fluxoVeicularPequeno;
	Integer fluxoVeicularMedio;
	Integer fluxoVeicularGrande;
	Integer fluxoVeicularSemId;
	
	Integer fluxoVeicularPasseio;
	Integer fluxoVeicularUtilitario;
	Integer fluxoVeicularCaminhao;
	Integer fluxoVeicularOnibus;
	
	Integer[] celulasFluxo = new Integer[31];
	Integer[] celulasFluxoMoto = new Integer[31];
	Integer[] celulasFluxoPequeno = new Integer[31];
	Integer[] celulasFluxoMedio = new Integer[31];
	Integer[] celulasFluxoGrande = new Integer[31];
	Integer[] celulasFluxoSemId = new Integer[31];
	
	Integer[] celulasFluxoPasseio = new Integer[31];
	Integer[] celulasFluxoUtilitario = new Integer[31];
	Integer[] celulasFluxoCaminhao = new Integer[31];
	Integer[] celulasFluxoOnibus = new Integer[31];

	public ItemRelatorios()
	{
	};
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 10/01/2021
	 * Objetivo: Construtor para o relatório de distribuição por faixa de velocidade
	 */
	public ItemRelatorios(Integer hora,
							 String horaDesc,
							 Integer[] celulasInteger)
	{
		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasInteger = celulasInteger;
	}

	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 10/01/2021
	 * Objetivo: Construtor para o relatório de velocidades do 85º percentil.
	 */
	public ItemRelatorios(Integer idLocal,
							 Integer[] celulasInteger)
	{
		super();
		this.idLocal = idLocal;
		this.celulasInteger = celulasInteger;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/01/2021
	 * Objetivo: Construtor para o relatório de distribuição por porte veicular
	 */
	public ItemRelatorios(Integer hora,
							 String horaDesc,
							 Integer[] celulasFluxoMoto,
							 Integer[] celulasFluxoPequeno,
							 Integer[] celulasFluxoMedio,
							 Integer[] celulasFluxoGrande,
							 Integer[] celulasFluxo,
							 Integer fluxoVeicular,
							 Integer fluxoVeicularMoto,
							 Integer fluxoVeicularPequeno,
							 Integer fluxoVeicularMedio,
							 Integer fluxoVeicularGrande){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxoMoto = celulasFluxoMoto;
		this.celulasFluxoPequeno = celulasFluxoPequeno;
		this.celulasFluxoMedio = celulasFluxoMedio;
		this.celulasFluxoGrande = celulasFluxoGrande;
		this.celulasFluxo = celulasFluxo;
		this.fluxoVeicular = fluxoVeicular;
		this.fluxoVeicularMoto = fluxoVeicularMoto;
		this.fluxoVeicularPequeno = fluxoVeicularPequeno;
		this.fluxoVeicularMedio = fluxoVeicularMedio;
		this.fluxoVeicularGrande = fluxoVeicularGrande;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * @since 11/01/2021
	 * Objetivo: Construtor para o relatório de Fluxo mensal por Classificação
	 */
	public ItemRelatorios(Integer hora,
							 String horaDesc,
							 Integer[] celulasFluxoMoto,
							 Integer[] celulasFluxoPasseio,
							 Integer[] celulasFluxoUtilitario,
							 Integer[] celulasFluxoCaminhao,
							 Integer[] celulasFluxoOnibus,
							 Integer[] celulasFluxo,
							 Integer fluxoVeicular,
							 Integer fluxoVeicularMoto,
							 Integer fluxoVeicularPasseio,
							 Integer fluxoVeicularUtilitario,
							 Integer fluxoVeicularCaminhao,
							 Integer fluxoVeicularOnibus){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxoMoto = celulasFluxoMoto;
		this.celulasFluxoPasseio = celulasFluxoPasseio;
		this.celulasFluxoUtilitario = celulasFluxoUtilitario;
		this.celulasFluxoCaminhao = celulasFluxoCaminhao;
		this.celulasFluxoOnibus = celulasFluxoOnibus;
		this.celulasFluxo = celulasFluxo;
		this.fluxoVeicular = fluxoVeicular;
		this.fluxoVeicularMoto = fluxoVeicularMoto;
		this.fluxoVeicularPasseio = fluxoVeicularPasseio;
		this.fluxoVeicularUtilitario = fluxoVeicularUtilitario;
		this.fluxoVeicularCaminhao = fluxoVeicularCaminhao;
		this.fluxoVeicularOnibus = fluxoVeicularOnibus;
	}
	
	public ItemRelatorios(Integer hora,
			 	 String horaDesc,
			 	 Integer[] celulasFluxo,
			 	 Integer fluxoVeicular){
	
		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxo = celulasFluxo;
		this.fluxoVeicular = fluxoVeicular;
	}
	

	public Integer getHora() { return hora; }
	public void setHora(Integer hora) { this.hora = hora; }

	public String getHoraDesc() { return horaDesc; }
	public void setHoraDesc(String horaDesc) { this.horaDesc = horaDesc; }
	
	public Integer getIdLocal() { return idLocal; }
	public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }

	public Integer[] getCelulasInteger() { return celulasInteger; }
	public void setCelulasInteger(Integer[] celulasInteger) { this.celulasInteger = celulasInteger; }

	public Integer getFluxoVeicular() { return fluxoVeicular; }
	public void setFluxoVeicular(Integer fluxoVeicular) { this.fluxoVeicular = fluxoVeicular; }

	public Integer getFluxoVeicularMoto() { return fluxoVeicularMoto; }
	public void setFluxoVeicularMoto(Integer fluxoVeicularMoto) { this.fluxoVeicularMoto = fluxoVeicularMoto; }

	public Integer getFluxoVeicularPequeno() { return fluxoVeicularPequeno; }
	public void setFluxoVeicularPequeno(Integer fluxoVeicularPequeno) { this.fluxoVeicularPequeno = fluxoVeicularPequeno; }

	public Integer getFluxoVeicularMedio() { return fluxoVeicularMedio; }
	public void setFluxoVeicularMedio(Integer fluxoVeicularMedio) { this.fluxoVeicularMedio = fluxoVeicularMedio; }

	public Integer getFluxoVeicularGrande() { return fluxoVeicularGrande; }
	public void setFluxoVeicularGrande(Integer fluxoVeicularGrande) { this.fluxoVeicularGrande = fluxoVeicularGrande; }

	public Integer getFluxoVeicularSemId() { return fluxoVeicularSemId; }
	public void setFluxoVeicularSemId(Integer fluxoVeicularSemId) { this.fluxoVeicularSemId = fluxoVeicularSemId; }

	public Integer[] getCelulasFluxo() { return celulasFluxo; }
	public void setCelulasFluxo(Integer[] celulasFluxo) { this.celulasFluxo = celulasFluxo; }

	public Integer[] getCelulasFluxoMoto() { return celulasFluxoMoto; }
	public void setCelulasFluxoMoto(Integer[] celulasFluxoMoto) { this.celulasFluxoMoto = celulasFluxoMoto; }

	public Integer[] getCelulasFluxoPequeno() { return celulasFluxoPequeno; }
	public void setCelulasFluxoPequeno(Integer[] celulasFluxoPequeno) { this.celulasFluxoPequeno = celulasFluxoPequeno; }

	public Integer[] getCelulasFluxoMedio() { return celulasFluxoMedio; }
	public void setCelulasFluxoMedio(Integer[] celulasFluxoMedio) { this.celulasFluxoMedio = celulasFluxoMedio; }

	public Integer[] getCelulasFluxoGrande() { return celulasFluxoGrande; }
	public void setCelulasFluxoGrande(Integer[] celulasFluxoGrande) { this.celulasFluxoGrande = celulasFluxoGrande; }

	public Integer[] getCelulasFluxoSemId() { return celulasFluxoSemId; }
	public void setCelulasFluxoSemId(Integer[] celulasFluxoSemId) { this.celulasFluxoSemId = celulasFluxoSemId; }
	
	public Integer getFluxoVeicularPasseio() { return fluxoVeicularPasseio;	}
	public void setFluxoVeicularPasseio(Integer fluxoVeicularPasseio) { this.fluxoVeicularPasseio = fluxoVeicularPasseio; }

	public Integer getFluxoVeicularUtilitario() { return fluxoVeicularUtilitario; }
	public void setFluxoVeicularUtilitario(Integer fluxoVeicularUtilitario) { this.fluxoVeicularUtilitario = fluxoVeicularUtilitario; }

	public Integer getFluxoVeicularCaminhao() { return fluxoVeicularCaminhao; }
	public void setFluxoVeicularCaminhao(Integer fluxoVeicularCaminhao) { this.fluxoVeicularCaminhao = fluxoVeicularCaminhao; }

	public Integer getFluxoVeicularOnibus() { return fluxoVeicularOnibus; }
	public void setFluxoVeicularOnibus(Integer fluxoVeicularOnibus) { this.fluxoVeicularOnibus = fluxoVeicularOnibus; }

	public Integer[] getCelulasFluxoPasseio() { return celulasFluxoPasseio; }
	public void setCelulasFluxoPasseio(Integer[] celulasFluxoPasseio) { this.celulasFluxoPasseio = celulasFluxoPasseio; }

	public Integer[] getCelulasFluxoUtilitario() { return celulasFluxoUtilitario; }
	public void setCelulasFluxoUtilitario(Integer[] celulasFluxoUtilitario) {this.celulasFluxoUtilitario = celulasFluxoUtilitario; }

	public Integer[] getCelulasFluxoCaminhao() { return celulasFluxoCaminhao; }
	public void setCelulasFluxoCaminhao(Integer[] celulasFluxoCaminhao) { this.celulasFluxoCaminhao = celulasFluxoCaminhao; }

	public Integer[] getCelulasFluxoOnibus() { return celulasFluxoOnibus; }
	public void setCelulasFluxoOnibus(Integer[] celulasFluxoOnibus) { this.celulasFluxoOnibus = celulasFluxoOnibus; }

}
