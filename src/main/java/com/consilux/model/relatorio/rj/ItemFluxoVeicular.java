/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 21/08/2014

*********************************************************************************/
package com.consilux.model.relatorio.rj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Thiago Surgik - Consilux Tecnologia
 * Data: 28/09/2016
 */
public  class ItemFluxoVeicular implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	
	String data;
	Integer diaSemana;
	String diaSemanaDesc;
	Integer hora;
	String horaDesc;
	Date dtData;
	
	Long serieEquipamento;
	Integer idLocal;
	Integer faixa;
	String enderecoPistaSentido;
	Integer gstPistaSentido;
	String enderecoPistaSentidoFaixa;
	Integer gstPistaSentidoFaixa;
	Double latitude;
	Double longitude;
	String codigoEquipamentoDER;
	String codigosEquipamentosDER;
	Integer codigoGrinDER;
	
	Integer fluxoVeicular;
	Integer registroOCR;
	Integer autosDetectados;
	Integer autosValidos;
	Integer autosInvalidos;
	Integer autosInvalidosMotivoTecnico;
	Integer autosInvalidosMotivoNaoTecnico;
	Integer velocidadeMedia;
	Integer velocidadeMaxima;
	Integer velocidadePermitida;
	String strVelocidadesPermitidas;

	
	Integer autosDetectadosEnq56732; 
	Integer autosDetectadosEnq60503; 
	Integer autosDetectadosEnq74550; 
	Integer autosDetectadosEnq74630; 
	Integer autosDetectadosEnq74710; 
	Integer autosValidosEnq56732;
	Integer autosValidosEnq60503;
	Integer autosValidosEnq74550;
	Integer autosValidosEnq74630;
	Integer autosValidosEnq74710;
	
	Integer[] celulasAutosDetectadosEnq56732;
	Integer[] celulasAutosDetectadosEnq60503;
	Integer[] celulasAutosDetectadosEnq74550;
	Integer[] celulasAutosDetectadosEnq74630;
	Integer[] celulasAutosDetectadosEnq74710;
	Integer[] celulasAutosValidosEnq56732;
	Integer[] celulasAutosValidosEnq60503;
	Integer[] celulasAutosValidosEnq74550;
	Integer[] celulasAutosValidosEnq74630;
	Integer[] celulasAutosValidosEnq74710;
	
	Integer fluxoVeicularMoto;
	Integer fluxoVeicularPequeno;
	Integer fluxoVeicularMedio;
	Integer fluxoVeicularGrande;
	Integer fluxoVeicularOutros;
	Integer fluxoVeicularSemId;
	Integer fluxoVeicularTotal;
	
	Double[] celulasDouble = new Double[31];
	Integer[] celulasInteger = new Integer[42];
	
	Integer[] celulasFluxo = new Integer[31];
	Integer[] celulasRegistroOCR = new Integer[31];
	Integer[] celulasVelMedia = new Integer[31];
	Integer[] celulasVelMax = new Integer[31];
	Integer[] celulasAutosDetectados = new Integer[31];
	Integer[] celulasAutosDetectadosAvanco = new Integer[31];
	Integer[] celulasAutosDetectadosParada = new Integer[31];
	Integer[] celulasAutosDetectadosVelocidade = new Integer[31];
	Integer[] celulasAutosValidos = new Integer[31];
	Integer[] celulasAutosValidosAvanco = new Integer[31];
	Integer[] celulasAutosValidosParada = new Integer[31];
	Integer[] celulasAutosValidosVelocidade = new Integer[31];
	Integer[] celulasAutosDetectadosTVeAte2 = new Integer[31];
	Integer[] celulasAutosDetectadosTVeEntre2e5 = new Integer[31];
	Integer[] celulasAutosDetectadosTVeEntre5e10 = new Integer[31];
	Integer[] celulasAutosDetectadosTVeAcima10 = new Integer[31];
	Integer[] celulasAutosValidosTVeAte2 = new Integer[31];
	Integer[] celulasAutosValidosTVeEntre2e5 = new Integer[31];
	Integer[] celulasAutosValidosTVeEntre5e10 = new Integer[31];
	Integer[] celulasAutosValidosTVeAcima10 = new Integer[31];
	Integer[] celulasAutosDetectadosInvasao = new Integer[31];
	Integer[] celulasAutosValidosInvasao = new Integer[31];
	Integer[] celulasAutosDetectadosConversao = new Integer[31];
	Integer[] celulasAutosValidosConversao = new Integer[31];
	Integer[] celulasAutosDetectadosRetorno = new Integer[31];
	Integer[] celulasAutosValidosRetorno = new Integer[31];
	
	Integer[] celulasFluxoMoto = new Integer[31];
	Integer[] celulasFluxoPequeno = new Integer[31];
	Integer[] celulasFluxoMedio = new Integer[31];
	Integer[] celulasFluxoGrande = new Integer[31];
	Integer[] celulasFluxoSemId = new Integer[31];
	
	String[] celulasColunasRelatorio = new String[200];
	Integer[] celulasValorColuna = new Integer[200];
	Integer qtdeColunas;
	
	Integer fluxoVeicularSegunda;
	Integer fluxoVeicularTerca;
	Integer fluxoVeicularQuarta;
	Integer fluxoVeicularQuinta;
	Integer fluxoVeicularSexta;
	Integer fluxoVeicularSabado;
	Integer fluxoVeicularDomingo;
	

	public ItemFluxoVeicular(){
	};
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 03/01/2019
	 * Objetivo: Construtor para o relatório fluxo veicular por hora.
	 */
	public ItemFluxoVeicular(Integer hora,
						 	 String horaDesc,
						 	 Integer[] celulasFluxo,
						 	 Integer fluxoVeicular){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxo = celulasFluxo;
		this.fluxoVeicular = fluxoVeicular;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 06/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 01 - Cada endereço mensal por data e horário - fluxo, velocidade e autos.
	 */
	public ItemFluxoVeicular(Integer hora,
							 String horaDesc,
							 Integer[] celulasFluxo,
							 Integer[] celulasVelMedia,
							 Integer[] celulasVelMax,
							 Integer[] celulasAutosDetectados,
							 Integer[] celulasAutosValidos,
							 Integer fluxoVeicular,
							 Integer velocidadeMedia,
							 Integer velocidadeMaxima,
							 Integer autosDetectados,
							 Integer autosValidos){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxo = celulasFluxo;
		this.celulasVelMedia = celulasVelMedia;
		this.celulasVelMax = celulasVelMax;
		this.celulasAutosDetectados = celulasAutosDetectados;
		this.celulasAutosValidos = celulasAutosValidos;
		this.fluxoVeicular = fluxoVeicular;
		this.velocidadeMedia = velocidadeMedia;
		this.velocidadeMaxima = velocidadeMaxima;
		this.autosDetectados = autosDetectados;
		this.autosValidos = autosValidos;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 02 - Cada endereço mensal por data - fluxo, velocidade e autos.
	 */
	public ItemFluxoVeicular(String data,
							 Integer diaSemana,
							 String diaSemanaDesc,
							 Integer fluxoVeicular,
							 Integer velocidadeMedia,
							 Integer velocidadeMaxima,
							 Integer autosDetectados,
							 Integer autosValidos){

		super();
		this.data = data;
		this.diaSemana = diaSemana;
		this.diaSemanaDesc = diaSemanaDesc;
		this.fluxoVeicular = fluxoVeicular;
		this.velocidadeMedia = velocidadeMedia;
		this.velocidadeMaxima = velocidadeMaxima;
		this.autosDetectados = autosDetectados;
		this.autosValidos = autosValidos;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 03 e 07.
	 */
	public ItemFluxoVeicular(String data, 
									Integer diaSemana,						
									String diaSemanaDesc,
									String[] celulasColunasRelatorio,
									Integer[] celulasValorColuna,
									Integer qtdeColunas){

		super();
		this.data = data;
		this.diaSemana = diaSemana;
		this.diaSemanaDesc = diaSemanaDesc;
		this.celulasColunasRelatorio = celulasColunasRelatorio;
		this.celulasValorColuna = celulasValorColuna;
		this.qtdeColunas = qtdeColunas;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 01/11/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 04 - volume veicular por porte veicular.
	 */
	public ItemFluxoVeicular(Integer hora,
							 String horaDesc,
							 Integer[] celulasFluxoMoto,
							 Integer[] celulasFluxoPequeno,
							 Integer[] celulasFluxoMedio,
							 Integer[] celulasFluxoGrande,
							 Integer[] celulasFluxoSemId,
							 Integer[] celulasFluxo,
							 Integer fluxoVeicular,
							 Integer fluxoVeicularMoto,
							 Integer fluxoVeicularPequeno,
							 Integer fluxoVeicularMedio,
							 Integer fluxoVeicularGrande,
							 Integer fluxoVeicularSemId){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxoMoto = celulasFluxoMoto;
		this.celulasFluxoPequeno = celulasFluxoPequeno;
		this.celulasFluxoMedio = celulasFluxoMedio;
		this.celulasFluxoGrande = celulasFluxoGrande;
		this.celulasFluxoSemId = celulasFluxoSemId;
		this.celulasFluxo = celulasFluxo;
		this.fluxoVeicular = fluxoVeicular;
		this.fluxoVeicularMoto = fluxoVeicularMoto;
		this.fluxoVeicularPequeno = fluxoVeicularPequeno;
		this.fluxoVeicularMedio = fluxoVeicularMedio;
		this.fluxoVeicularGrande = fluxoVeicularGrande;
		this.fluxoVeicularSemId = fluxoVeicularSemId;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 05, 06.
	 */
	public ItemFluxoVeicular(Integer hora, 
							 String horaDesc,
							 Double[] celulasDouble){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasDouble = celulasDouble;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 10/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 07 - Cada endereço (por pista-sentido) mensal por data e horário - volume veicular e autos (avanço, parada e velocidade).
	 */
	public ItemFluxoVeicular(Integer hora,
					 		 String horaDesc,
					 		 Integer[] celulasFluxo,
					 		 Integer[] celulasRegistroOCR,
					 		 Integer[] celulasAutosDetectadosEnq56732,
					 		 Integer[] celulasAutosDetectadosEnq60503,
					 		 Integer[] celulasAutosDetectadosEnq74550,
					 		 Integer[] celulasAutosDetectadosEnq74630,
					 		 Integer[] celulasAutosDetectadosEnq74710,
					 		 Integer fluxoVeicular, Integer registroOCR,
					 		 Integer autosDetectadosEnq56732, Integer autosDetectadosEnq60503, 
					 		 Integer autosDetectadosEnq74550, Integer autosDetectadosEnq74630, Integer autosDetectadosEnq74710){
		
		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxo = celulasFluxo;
		this.celulasRegistroOCR = celulasRegistroOCR;
		this.celulasAutosDetectadosEnq56732 = celulasAutosDetectadosEnq56732;
		this.celulasAutosDetectadosEnq60503 = celulasAutosDetectadosEnq60503;
		this.celulasAutosDetectadosEnq74550 = celulasAutosDetectadosEnq74550;
		this.celulasAutosDetectadosEnq74630 = celulasAutosDetectadosEnq74630;
		this.celulasAutosDetectadosEnq74710 = celulasAutosDetectadosEnq74710;
		this.fluxoVeicular = fluxoVeicular;
		this.registroOCR = registroOCR;
		this.autosDetectadosEnq56732 = autosDetectadosEnq56732;
		this.autosDetectadosEnq60503 = autosDetectadosEnq60503;
		this.autosDetectadosEnq74550 = autosDetectadosEnq74550;
		this.autosDetectadosEnq74630 = autosDetectadosEnq74630;
		this.autosDetectadosEnq74710 = autosDetectadosEnq74710;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 10 - Todos os endereços mensal por datas - fluxo, velocidade e autos.
	 */
	public ItemFluxoVeicular(Integer idLocal,
							 Long serieEquipamento,
							 String enderecoPistaSentido,
							 Double latitude,
							 Double longitude,
							 String codigosEquipamentosDER,
							 String strVelocidadesPermitidas,
							 Integer[] celulasFluxo,
							 Integer[] celulasVelMedia,
							 Integer[] celulasVelMax,
							 Integer[] celulasAutosDetectados,
							 Integer[] celulasAutosValidos,
							 Integer fluxoVeicular,
							 Integer velocidadeMedia,
							 Integer velocidadeMaxima,
							 Integer autosDetectados,
							 Integer autosValidos,
							 Integer autosInvalidosMotivoTecnico,
							 Integer autosInvalidosMotivoNaoTecnico){

		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.enderecoPistaSentido = enderecoPistaSentido;
		this.latitude = latitude;
		this.longitude = longitude;
		this.codigosEquipamentosDER = codigosEquipamentosDER;
		this.strVelocidadesPermitidas = strVelocidadesPermitidas;
		this.celulasFluxo = celulasFluxo;
		this.celulasVelMedia = celulasVelMedia;
		this.celulasVelMax = celulasVelMax;
		this.celulasAutosDetectados = celulasAutosDetectados;
		this.celulasAutosValidos = celulasAutosValidos;
		this.fluxoVeicular = fluxoVeicular;
		this.velocidadeMedia = velocidadeMedia;
		this.velocidadeMaxima = velocidadeMaxima;
		this.autosDetectados = autosDetectados;
		this.autosValidos = autosValidos;
		this.autosInvalidosMotivoTecnico = autosInvalidosMotivoTecnico;
		this.autosInvalidosMotivoNaoTecnico = autosInvalidosMotivoNaoTecnico;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 07/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 10 - Todos os endereços mensal por datas - fluxo, velocidade e autos.
	 */
	public ItemFluxoVeicular(Integer idLocal,
							 Long serieEquipamento,
							 String enderecoPistaSentidoFaixa,
							 Double latitude,
							 Double longitude,
							 String codigoEquipamentoDER,
							 Integer velocidadePermitida,
							 Integer[] celulasFluxo,
							 Integer[] celulasVelMedia,
							 Integer[] celulasVelMax,
							 Integer[] celulasAutosDetectados,
							 Integer[] celulasAutosValidos,
							 Integer fluxoVeicular,
							 Integer velocidadeMedia,
							 Integer velocidadeMaxima,
							 Integer autosDetectados,
							 Integer autosValidos,
							 Integer autosInvalidosMotivoTecnico,
							 Integer autosInvalidosMotivoNaoTecnico){

		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.enderecoPistaSentidoFaixa = enderecoPistaSentidoFaixa;
		this.latitude = latitude;
		this.longitude = longitude;
		this.codigoEquipamentoDER = codigoEquipamentoDER;
		this.velocidadePermitida = velocidadePermitida;
		this.celulasFluxo = celulasFluxo;
		this.celulasVelMedia = celulasVelMedia;
		this.celulasVelMax = celulasVelMax;
		this.celulasAutosDetectados = celulasAutosDetectados;
		this.celulasAutosValidos = celulasAutosValidos;
		this.fluxoVeicular = fluxoVeicular;
		this.velocidadeMedia = velocidadeMedia;
		this.velocidadeMaxima = velocidadeMaxima;
		this.autosDetectados = autosDetectados;
		this.autosValidos = autosValidos;
		this.autosInvalidosMotivoTecnico = autosInvalidosMotivoTecnico;
		this.autosInvalidosMotivoNaoTecnico = autosInvalidosMotivoNaoTecnico;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 06/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 5, 6 e 9.
	 */
	public ItemFluxoVeicular(Integer hora,
							 String horaDesc,
							 Integer[] celulasInteger){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasInteger = celulasInteger;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 30/09/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 8 - Cada endereço mensal por hora - volume veicular por dia da semana.
	 */
	public ItemFluxoVeicular(Integer hora,
							 String horaDesc,
							 Integer fluxoVeicularSegunda,
							 Integer fluxoVeicularTerca,
							 Integer fluxoVeicularQuarta,
							 Integer fluxoVeicularQuinta,
							 Integer fluxoVeicularSexta,
							 Integer fluxoVeicularSabado,
							 Integer fluxoVeicularDomingo){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.fluxoVeicularSegunda = fluxoVeicularSegunda;
		this.fluxoVeicularTerca = fluxoVeicularTerca;
		this.fluxoVeicularQuarta = fluxoVeicularQuarta;
		this.fluxoVeicularQuinta = fluxoVeicularQuinta;
		this.fluxoVeicularSexta = fluxoVeicularSexta;
		this.fluxoVeicularSabado = fluxoVeicularSabado;
		this.fluxoVeicularDomingo = fluxoVeicularDomingo;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 11 - Cada endereço diário por hora - registros válidos por enquadramento.
	 */
	public ItemFluxoVeicular(Integer hora,
			 				 String horaDesc,
							 Integer[] celulasAutosValidosEnq56732,
							 Integer[] celulasAutosValidosEnq60503,
							 Integer[] celulasAutosValidosEnq74550,
							 Integer[] celulasAutosValidosEnq74630,
							 Integer[] celulasAutosValidosEnq74710,
							 Integer autosValidosEnq56732,Integer autosValidosEnq60503,
							 Integer autosValidosEnq74550,Integer autosValidosEnq74630,Integer autosValidosEnq74710, boolean x){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasAutosValidosEnq56732 = celulasAutosValidosEnq56732;
		this.celulasAutosValidosEnq60503 = celulasAutosValidosEnq60503;
		this.celulasAutosValidosEnq74550 = celulasAutosValidosEnq74550;
		this.celulasAutosValidosEnq74630 = celulasAutosValidosEnq74630;
		this.celulasAutosValidosEnq74710 = celulasAutosValidosEnq74710;
		this.autosValidosEnq56732 = autosValidosEnq56732;
		this.autosValidosEnq60503 = autosValidosEnq60503;
		this.autosValidosEnq74550 = autosValidosEnq74550;
		this.autosValidosEnq74630 = autosValidosEnq74630;
		this.autosValidosEnq74710 = autosValidosEnq74710;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 12 - Cada endereço mensal por data - registros detectados e válidos por enquadramento.
	 */
	public ItemFluxoVeicular(String data,
							 Integer diaSemana,
							 String diaSemanaDesc,
							 Integer fluxoVeicular,
							 Integer registroOCR,
							 Integer autosDetectados,
							 Integer autosDetectadosEnq56732,Integer autosDetectadosEnq60503,
							 Integer autosDetectadosEnq74550,Integer autosDetectadosEnq74630,Integer autosDetectadosEnq74710,
							 Integer autosValidos,
							 Integer autosValidosEnq56732,Integer autosValidosEnq60503,
							 Integer autosValidosEnq74550,Integer autosValidosEnq74630,Integer autosValidosEnq74710){

		super();
		this.data = data;
		this.diaSemana = diaSemana;
		this.diaSemanaDesc = diaSemanaDesc;
		this.fluxoVeicular = fluxoVeicular;
		this.registroOCR = registroOCR;
		this.autosDetectados = autosDetectados;
		this.autosDetectadosEnq56732 = autosDetectadosEnq56732;
		this.autosDetectadosEnq60503 = autosDetectadosEnq60503;
		this.autosDetectadosEnq74550 = autosDetectadosEnq74550;
		this.autosDetectadosEnq74630 = autosDetectadosEnq74630;
		this.autosDetectadosEnq74710 = autosDetectadosEnq74710;
		this.autosValidos = autosValidos;
		this.autosValidosEnq56732 = autosValidosEnq56732;
		this.autosValidosEnq60503 = autosValidosEnq60503;
		this.autosValidosEnq74550 = autosValidosEnq74550;
		this.autosValidosEnq74630 = autosValidosEnq74630;
		this.autosValidosEnq74710 = autosValidosEnq74710;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - registros detectados e válidos por enquadramento.
	 */
	public ItemFluxoVeicular(Integer idLocal,
							 Long serieEquipamento,
							 String enderecoPistaSentido,
							 Double latitude,
							 Double longitude,
							 String codigosEquipamentosDER,
							 Integer fluxoVeicular,
							 Integer autosDetectados,
							 Integer autosDetectadosEnq56732,Integer autosDetectadosEnq60503,
							 Integer autosDetectadosEnq74550,Integer autosDetectadosEnq74630,Integer autosDetectadosEnq74710,
							 Integer autosValidos,
							 Integer autosValidosEnq56732,Integer autosValidosEnq60503,
							 Integer autosValidosEnq74550,Integer autosValidosEnq74630,Integer autosValidosEnq74710){

		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.enderecoPistaSentido = enderecoPistaSentido;
		this.latitude = latitude;
		this.longitude = longitude;
		this.codigosEquipamentosDER = codigosEquipamentosDER;
		this.fluxoVeicular = fluxoVeicular;
		this.autosDetectados = autosDetectados;
		this.autosDetectadosEnq56732 = autosDetectadosEnq56732;
		this.autosDetectadosEnq60503 = autosDetectadosEnq60503;
		this.autosDetectadosEnq74550 = autosDetectadosEnq74550;
		this.autosDetectadosEnq74630 = autosDetectadosEnq74630;
		this.autosDetectadosEnq74710 = autosDetectadosEnq74710;
		this.autosValidos = autosValidos;
		this.autosValidosEnq56732 = autosValidosEnq56732;
		this.autosValidosEnq60503 = autosValidosEnq60503;
		this.autosValidosEnq74550 = autosValidosEnq74550;
		this.autosValidosEnq74630 = autosValidosEnq74630;
		this.autosValidosEnq74710 = autosValidosEnq74710;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 05/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 13 - Todos os endeços (por pista-sentido) mensal - registros detectados e válidos por enquadramento.
	 */
	public ItemFluxoVeicular(Integer idLocal,
							 Long serieEquipamento,
							 String enderecoPistaSentidoFaixa,
							 Integer faixa,
							 Double latitude,
							 Double longitude,
							 String codigoEquipamentoDER,
							 Integer fluxoVeicular,
							 Integer autosDetectados,
							 Integer autosDetectadosEnq56732,Integer autosDetectadosEnq60503,
							 Integer autosDetectadosEnq74550,Integer autosDetectadosEnq74630,Integer autosDetectadosEnq74710,
							 Integer autosValidos,
							 Integer autosValidosEnq56732,Integer autosValidosEnq60503,
							 Integer autosValidosEnq74550,Integer autosValidosEnq74630,Integer autosValidosEnq74710){

		super();
		this.idLocal = idLocal;
		this.serieEquipamento = serieEquipamento;
		this.enderecoPistaSentidoFaixa = enderecoPistaSentidoFaixa;
		this.faixa = faixa;
		this.latitude = latitude;
		this.longitude = longitude;
		this.codigoEquipamentoDER = codigoEquipamentoDER;
		this.fluxoVeicular = fluxoVeicular;
		this.autosDetectados = autosDetectados;
		this.autosDetectadosEnq56732 = autosDetectadosEnq56732;
		this.autosDetectadosEnq60503 = autosDetectadosEnq60503;
		this.autosDetectadosEnq74550 = autosDetectadosEnq74550;
		this.autosDetectadosEnq74630 = autosDetectadosEnq74630;
		this.autosDetectadosEnq74710 = autosDetectadosEnq74710;
		this.autosValidos = autosValidos;
		this.autosValidosEnq56732 = autosValidosEnq56732;
		this.autosValidosEnq60503 = autosValidosEnq60503;
		this.autosValidosEnq74550 = autosValidosEnq74550;
		this.autosValidosEnq74630 = autosValidosEnq74630;
		this.autosValidosEnq74710 = autosValidosEnq74710;
	}
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 11/10/2016
	 * Objetivo: Construtor para o relatório de de medição de fluxo veicular - Relatório 14 - Cada endereço (por pista-sentido) mensal por hora - volume veicular e autos (avanço e tempo de vermelho).
	 */
	public ItemFluxoVeicular(Integer hora,
							 String horaDesc,
							 Integer[] celulasFluxo,
							 Integer[] celulasAutosDetectadosAvanco,
							 Integer[] celulasAutosDetectadosTVeAte2,
							 Integer[] celulasAutosDetectadosTVeEntre2e5,
							 Integer[] celulasAutosDetectadosTVeEntre5e10,
							 Integer[] celulasAutosDetectadosTVeAcima10,
							 Integer[] celulasAutosValidosAvanco,
							 Integer[] celulasAutosValidosTVeAte2,
							 Integer[] celulasAutosValidosTVeEntre2e5,
							 Integer[] celulasAutosValidosTVeEntre5e10,
							 Integer[] celulasAutosValidosTVeAcima10){

		super();
		this.hora = hora;
		this.horaDesc = horaDesc;
		this.celulasFluxo = celulasFluxo;
		this.celulasAutosDetectadosAvanco = celulasAutosDetectadosAvanco;
		this.celulasAutosDetectadosTVeAte2 = celulasAutosDetectadosTVeAte2;
		this.celulasAutosDetectadosTVeEntre2e5 = celulasAutosDetectadosTVeEntre2e5;
		this.celulasAutosDetectadosTVeEntre5e10 = celulasAutosDetectadosTVeEntre5e10;
		this.celulasAutosDetectadosTVeAcima10 = celulasAutosDetectadosTVeAcima10;
		this.celulasAutosValidosAvanco = celulasAutosValidosAvanco;
		this.celulasAutosValidosTVeAte2 = celulasAutosValidosTVeAte2;
		this.celulasAutosValidosTVeEntre2e5 = celulasAutosValidosTVeEntre2e5;
		this.celulasAutosValidosTVeEntre5e10 = celulasAutosValidosTVeEntre5e10;
		this.celulasAutosValidosTVeAcima10 = celulasAutosValidosTVeAcima10;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 26/04/2022
	 * Objetivo: Construtor para o relatório de fluxo mensal e diário por classificação do DER-MG
	 */
	public ItemFluxoVeicular(Integer idLocal,
						 	 Integer faixa,
							 String endereco,
							 Integer codigoGrinDER,
							 Date dtData,
							 Integer fluxoVeicularMoto,
							 Integer fluxoVeicularPequeno,
							 Integer fluxoVeicularMedio,
							 Integer fluxoVeicularGrande,
							 Integer fluxoVeicularOutros,
							 Integer fluxoVeicularTotal)
	{
		super();
		this.idLocal = idLocal;
		this.faixa = faixa;
		this.enderecoPistaSentidoFaixa = endereco;
		this.codigoGrinDER = codigoGrinDER;
		this.dtData = dtData;
		this.fluxoVeicularMoto = fluxoVeicularMoto;
		this.fluxoVeicularPequeno = fluxoVeicularPequeno;
		this.fluxoVeicularMedio = fluxoVeicularMedio;
		this.fluxoVeicularGrande = fluxoVeicularGrande;
		this.fluxoVeicularOutros = fluxoVeicularOutros;
		this.fluxoVeicularTotal = fluxoVeicularTotal;
	}
	
	
	/**
	 * @author Thiago Surgik - Consilux Tecnologia
	 * Data: 12/07/2018
	 * Objetivo: Construtor para o relatório de velocidades do 85º percentil.
	 */
	public ItemFluxoVeicular(Integer idLocal,
							 Integer[] celulasInteger){

		super();
		this.idLocal = idLocal;
		this.celulasInteger = celulasInteger;
	}
	

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
	public Date getDtData() {
		return dtData;
	}
	public void setDtData(Date dtData) {
		this.dtData = dtData;
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
	
	
	public Long getSerieEquipamento() {
		return serieEquipamento;
	}
	public void setSerieEquipamento(Long serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	
	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}
	

	public String getEnderecoPistaSentido() {
		return enderecoPistaSentido;
	}
	public void setEnderecoPistaSentido(String enderecoPistaSentido) {
		this.enderecoPistaSentido = enderecoPistaSentido;
	}
	
	
	public Integer getGstPistaSentido() {
		return gstPistaSentido;
	}
	public void setGstPistaSentido(Integer gstPistaSentido) {
		this.gstPistaSentido = gstPistaSentido;
	}
	
	
	public String getEnderecoPistaSentidoFaixa() {
		return enderecoPistaSentidoFaixa;
	}
	public void setEnderecoPistaSentidoFaixa(String enderecoPistaSentidoFaixa) {
		this.enderecoPistaSentidoFaixa = enderecoPistaSentidoFaixa;
	}
	
	
	public Integer getGstPistaSentidoFaixa() {
		return gstPistaSentidoFaixa;
	}
	public void setGstPistaSentidoFaixa(Integer gstPistaSentidoFaixa) {
		this.gstPistaSentidoFaixa = gstPistaSentidoFaixa;
	}

	
	public Integer getFaixa() {
		return faixa;
	}
	public void setFaixa(Integer faixa) {
		this.faixa = faixa;
	}
	
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
	public String getCodigoEquipamentoDER() {
		return codigoEquipamentoDER;
	}
	public void setCodigoEquipamentoDER(String codigoEquipamentoDER) {
		this.codigoEquipamentoDER = codigoEquipamentoDER;
	}
	
	
	public String getCodigosEquipamentosDER() {
		return codigosEquipamentosDER;
	}
	public void setCodigosEquipamentosDER(String codigosEquipamentosDER) {
		this.codigosEquipamentosDER = codigosEquipamentosDER;
	}

	
	public Integer getCodigoGrinDER() {
		return codigoGrinDER;
	}
	public void setCodigoGrinDER(Integer codigoGrinDER) {
		this.codigoGrinDER = codigoGrinDER;
	}

	
	public Integer getFluxoVeicular() {
		return fluxoVeicular;
	}
	public void setFluxoVeicular(Integer fluxoVeicular) {
		this.fluxoVeicular = fluxoVeicular;
	}

	
	public Integer getRegistroOCR() {
		return registroOCR;
	}
	public void setRegistroOCR(Integer registroOCR) {
		this.registroOCR = registroOCR;
	}

	
	public Integer getAutosDetectados() {
		return autosDetectados;
	}
	public void setAutosDetectados(Integer autosDetectados) {
		this.autosDetectados = autosDetectados;
	}

	
	public Integer getAutosValidos() {
		return autosValidos;
	}
	public void setAutosValidos(Integer autosValidos) {
		this.autosValidos = autosValidos;
	}

	
	public Integer getVelocidadeMedia() {
		return velocidadeMedia;
	}
	public void setVelocidadeMedia(Integer velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}
	
	
	public Integer getVelocidadeMaxima() {
		return velocidadeMaxima;
	}
	public void setVelocidadeMaxima(Integer velocidadeMaxima) {
		this.velocidadeMaxima = velocidadeMaxima;
	}
	
	
	public Integer getVelocidadePermitida() {
		return velocidadePermitida;
	}
	public void setVelocidadePermitida(Integer velocidadePermitida) {
		this.velocidadePermitida = velocidadePermitida;
	}
	
		
	public String getStrVelocidadesPermitidas() {
		return strVelocidadesPermitidas;
	}
	public void setStrVelocidadesPermitidas(String strVelocidadesPermitidas) {
		this.strVelocidadesPermitidas = strVelocidadesPermitidas;
	}

	
	public Integer getAutosDetectadosEnq74550() {
		return autosDetectadosEnq74550;
	}
	public void setAutosDetectadosEnq74550(Integer autosDetectadosEnq74550) {
		this.autosDetectadosEnq74550 = autosDetectadosEnq74550;
	}

	
	public Integer getAutosDetectadosEnq74630() {
		return autosDetectadosEnq74630;
	}
	public void setAutosDetectadosEnq74630(Integer autosDetectadosEnq74630) {
		this.autosDetectadosEnq74630 = autosDetectadosEnq74630;
	}

	
	public Integer getAutosDetectadosEnq74710() {
		return autosDetectadosEnq74710;
	}
	public void setAutosDetectadosEnq74710(Integer autosDetectadosEnq74710) {
		this.autosDetectadosEnq74710 = autosDetectadosEnq74710;
	}

	
	public Integer getAutosValidosEnq74550() {
		return autosValidosEnq74550;
	}
	public void setAutosValidosEnq74550(Integer autosValidosEnq74550) {
		this.autosValidosEnq74550 = autosValidosEnq74550;
	}

	
	public Integer getAutosValidosEnq74630() {
		return autosValidosEnq74630;
	}
	public void setAutosValidosEnq74630(Integer autosValidosEnq74630) {
		this.autosValidosEnq74630 = autosValidosEnq74630;
	}

	
	public Integer getAutosValidosEnq74710() {
		return autosValidosEnq74710;
	}
	public void setAutosValidosEnq74710(Integer autosValidosEnq74710) {
		this.autosValidosEnq74710 = autosValidosEnq74710;
	}
	
	public Integer[] getCelulasAutosDetectadosEnq74550() {
		return celulasAutosDetectadosEnq74550;
	}
	public void setCelulasAutosDetectadosEnq74550(Integer[] celulasAutosDetectadosEnq74550) {
		this.celulasAutosDetectadosEnq74550 = celulasAutosDetectadosEnq74550;
	}

	
	public Integer[] getCelulasAutosDetectadosEnq74630() {
		return celulasAutosDetectadosEnq74630;
	}
	public void setCelulasAutosDetectadosEnq74630(Integer[] celulasAutosDetectadosEnq74630) {
		this.celulasAutosDetectadosEnq74630 = celulasAutosDetectadosEnq74630;
	}

	
	public Integer[] getCelulasAutosDetectadosEnq74710() {
		return celulasAutosDetectadosEnq74710;
	}
	public void setCelulasAutosDetectadosEnq74710(Integer[] celulasAutosDetectadosEnq74710) {
		this.celulasAutosDetectadosEnq74710 = celulasAutosDetectadosEnq74710;
	}

	
	
	public Integer[] getCelulasAutosValidosEnq74550() {
		return celulasAutosValidosEnq74550;
	}
	public void setCelulasAutosValidosEnq74550(Integer[] celulasAutosValidosEnq74550) {
		this.celulasAutosValidosEnq74550 = celulasAutosValidosEnq74550;
	}

	
	public Integer[] getCelulasAutosValidosEnq74630() {
		return celulasAutosValidosEnq74630;
	}
	public void setCelulasAutosValidosEnq74630(Integer[] celulasAutosValidosEnq74630) {
		this.celulasAutosValidosEnq74630 = celulasAutosValidosEnq74630;
	}

	
	public Integer[] getCelulasAutosValidosEnq74710() {
		return celulasAutosValidosEnq74710;
	}
	public void setCelulasAutosValidosEnq74710(Integer[] celulasAutosValidosEnq74710) {
		this.celulasAutosValidosEnq74710 = celulasAutosValidosEnq74710;
	}

		
	public Integer getFluxoVeicularMoto() {
		return fluxoVeicularMoto;
	}
	public void setFluxoVeicularMoto(Integer fluxoVeicularMoto) {
		this.fluxoVeicularMoto = fluxoVeicularMoto;
	}
		
	
	public Integer getFluxoVeicularPequeno() {
		return fluxoVeicularPequeno;
	}
	public void setFluxoVeicularPequeno(Integer fluxoVeicularPequeno) {
		this.fluxoVeicularPequeno = fluxoVeicularPequeno;
	}
	
	
	public Integer getFluxoVeicularMedio() {
		return fluxoVeicularMedio;
	}
	public void setFluxoVeicularMedio(Integer fluxoVeicularMedio) {
		this.fluxoVeicularMedio = fluxoVeicularMedio;
	}
	
	
	public Integer getFluxoVeicularGrande() {
		return fluxoVeicularGrande;
	}
	public void setFluxoVeicularGrande(Integer fluxoVeicularGrande) {
		this.fluxoVeicularGrande = fluxoVeicularGrande;
	}
	

	public Integer getFluxoVeicularSemId() {
		return fluxoVeicularSemId;
	}
	public void setFluxoVeicularSemId(Integer fluxoVeicularSemId) {
		this.fluxoVeicularSemId = fluxoVeicularSemId;
	}
	

	public Integer getFluxoVeicularOutros() {
		return fluxoVeicularOutros;
	}
	public void setFluxoVeicularOutros(Integer fluxoVeicularOutros) {
		this.fluxoVeicularOutros = fluxoVeicularOutros;
	}

	public Integer getFluxoVeicularTotal() {
		return fluxoVeicularTotal;
	}
	public void setFluxoVeicularTotal(Integer fluxoVeicularTotal) {
		this.fluxoVeicularTotal = fluxoVeicularTotal;
	}
	

	public Double[] getCelulasDouble() {
		return celulasDouble;
	}
	public void setCelulasDouble(Double[] celulasDouble) {
		this.celulasDouble = celulasDouble;
	}
	
	
	public Integer[] getCelulasInteger() {
		return celulasInteger;
	}
	public void setCelulasInteger(Integer[] celulasInteger) {
		this.celulasInteger = celulasInteger;
	}


	public Integer[] getCelulasFluxo() {
		return celulasFluxo;
	}
	public void setCelulasFluxo(Integer[] celulasFluxo) {
		this.celulasFluxo = celulasFluxo;
	}

	
	public Integer[] getCelulasRegistroOCR() {
		return celulasRegistroOCR;
	}
	public void setCelulasRegistroOCR(Integer[] celulasRegistroOCR) {
		this.celulasRegistroOCR = celulasRegistroOCR;
	}

	
	public Integer[] getCelulasVelMedia() {
		return celulasVelMedia;
	}
	public void setCelulasVelMedia(Integer[] celulasVelMedia) {
		this.celulasVelMedia = celulasVelMedia;
	}


	public Integer[] getCelulasVelMax() {
		return celulasVelMax;
	}
	public void setCelulasVelMax(Integer[] celulasVelMax) {
		this.celulasVelMax = celulasVelMax;
	}

	
	public Integer[] getCelulasAutosDetectados() {
		return celulasAutosDetectados;
	}
	public void setCelulasAutosDetectados(Integer[] celulasAutosDetectados) {
		this.celulasAutosDetectados = celulasAutosDetectados;
	}


	public Integer[] getCelulasAutosDetectadosAvanco() {
		return celulasAutosDetectadosAvanco;
	}
	public void setCelulasAutosDetectadosAvanco(
			Integer[] celulasAutosDetectadosAvanco) {
		this.celulasAutosDetectadosAvanco = celulasAutosDetectadosAvanco;
	}

	
	public Integer[] getCelulasAutosDetectadosParada() {
		return celulasAutosDetectadosParada;
	}
	public void setCelulasAutosDetectadosParada(Integer[] celulasAutosDetectadosParada) {
		this.celulasAutosDetectadosParada = celulasAutosDetectadosParada;
	}

	
	public Integer[] getCelulasAutosDetectadosVelocidade() {
		return celulasAutosDetectadosVelocidade;
	}
	public void setCelulasAutosDetectadosVelocidade(Integer[] celulasAutosDetectadosVelocidade) {
		this.celulasAutosDetectadosVelocidade = celulasAutosDetectadosVelocidade;
	}


	public Integer[] getCelulasAutosValidos() {
		return celulasAutosValidos;
	}
	public void setCelulasAutosValidos(Integer[] celulasAutosValidos) {
		this.celulasAutosValidos = celulasAutosValidos;
	}

	
	public Integer[] getCelulasAutosValidosAvanco() {
		return celulasAutosValidosAvanco;
	}
	public void setCelulasAutosValidosAvanco(Integer[] celulasAutosValidosAvanco) {
		this.celulasAutosValidosAvanco = celulasAutosValidosAvanco;
	}


	public Integer[] getCelulasAutosValidosParada() {
		return celulasAutosValidosParada;
	}
	public void setCelulasAutosValidosParada(Integer[] celulasAutosValidosParada) {
		this.celulasAutosValidosParada = celulasAutosValidosParada;
	}


	public Integer[] getCelulasAutosValidosVelocidade() {
		return celulasAutosValidosVelocidade;
	}
	public void setCelulasAutosValidosVelocidade(Integer[] celulasAutosValidosVelocidade) {
		this.celulasAutosValidosVelocidade = celulasAutosValidosVelocidade;
	}

	
	public Integer[] getCelulasAutosDetectadosTVeAte2() {
		return celulasAutosDetectadosTVeAte2;
	}
	public void setCelulasAutosDetectadosTVeAte2(Integer[] celulasAutosDetectadosTVeAte2) {
		this.celulasAutosDetectadosTVeAte2 = celulasAutosDetectadosTVeAte2;
	}

	
	public Integer[] getCelulasAutosDetectadosTVeEntre2e5() {
		return celulasAutosDetectadosTVeEntre2e5;
	}
	public void setCelulasAutosDetectadosTVeEntre2e5(Integer[] celulasAutosDetectadosTVeEntre2e5) {
		this.celulasAutosDetectadosTVeEntre2e5 = celulasAutosDetectadosTVeEntre2e5;
	}

	
	public Integer[] getCelulasAutosDetectadosTVeEntre5e10() {
		return celulasAutosDetectadosTVeEntre5e10;
	}
	public void setCelulasAutosDetectadosTVeEntre5e10(Integer[] celulasAutosDetectadosTVeEntre5e10) {
		this.celulasAutosDetectadosTVeEntre5e10 = celulasAutosDetectadosTVeEntre5e10;
	}

	
	public Integer[] getCelulasAutosDetectadosTVeAcima10() {
		return celulasAutosDetectadosTVeAcima10;
	}
	public void setCelulasAutosDetectadosTVeAcima10(Integer[] celulasAutosDetectadosTVeAcima10) {
		this.celulasAutosDetectadosTVeAcima10 = celulasAutosDetectadosTVeAcima10;
	}

	
	public Integer[] getCelulasAutosValidosTVeAte2() {
		return celulasAutosValidosTVeAte2;
	}
	public void setCelulasAutosValidosTVeAte2(Integer[] celulasAutosValidosTVeAte2) {
		this.celulasAutosValidosTVeAte2 = celulasAutosValidosTVeAte2;
	}

	
	public Integer[] getCelulasAutosValidosTVeEntre2e5() {
		return celulasAutosValidosTVeEntre2e5;
	}
	public void setCelulasAutosValidosTVeEntre2e5(Integer[] celulasAutosValidosTVeEntre2e5) {
		this.celulasAutosValidosTVeEntre2e5 = celulasAutosValidosTVeEntre2e5;
	}

	
	public Integer[] getCelulasAutosValidosTVeEntre5e10() {
		return celulasAutosValidosTVeEntre5e10;
	}
	public void setCelulasAutosValidosTVeEntre5e10(Integer[] celulasAutosValidosTVeEntre5e10) {
		this.celulasAutosValidosTVeEntre5e10 = celulasAutosValidosTVeEntre5e10;
	}

	
	public Integer[] getCelulasAutosValidosTVeAcima10() {
		return celulasAutosValidosTVeAcima10;
	}
	public void setCelulasAutosValidosTVeAcima10(Integer[] celulasAutosValidosTVeAcima10) {
		this.celulasAutosValidosTVeAcima10 = celulasAutosValidosTVeAcima10;
	}

	
	public Integer[] getCelulasAutosDetectadosInvasao() {
		return celulasAutosDetectadosInvasao;
	}
	public void setCelulasAutosDetectadosInvasao(Integer[] celulasAutosDetectadosInvasao) {
		this.celulasAutosDetectadosInvasao = celulasAutosDetectadosInvasao;
	}
	

	public Integer[] getCelulasAutosValidosInvasao() {
		return celulasAutosValidosInvasao;
	}
	public void setCelulasAutosValidosInvasao(Integer[] celulasAutosValidosInvasao) {
		this.celulasAutosValidosInvasao = celulasAutosValidosInvasao;
	}

	
	public Integer[] getCelulasAutosDetectadosConversao() {
		return celulasAutosDetectadosConversao;
	}
	public void setCelulasAutosDetectadosConversao(Integer[] celulasAutosDetectadosConversao) {
		this.celulasAutosDetectadosConversao = celulasAutosDetectadosConversao;
	}
	
	
	public Integer[] getCelulasAutosValidosConversao() {
		return celulasAutosValidosConversao;
	}
	public void setCelulasAutosValidosConversao(Integer[] celulasAutosValidosConversao) {
		this.celulasAutosValidosConversao = celulasAutosValidosConversao;
	}
	
	
	public Integer[] getCelulasAutosDetectadosRetorno() {
		return celulasAutosDetectadosRetorno;
	}
	public void setCelulasAutosDetectadosRetorno(Integer[] celulasAutosDetectadosRetorno) {
		this.celulasAutosDetectadosRetorno = celulasAutosDetectadosRetorno;
	}
	
	
	public Integer[] getCelulasAutosValidosRetorno() {
		return celulasAutosValidosRetorno;
	}
	public void setCelulasAutosValidosRetorno(Integer[] celulasAutosValidosRetorno) {
		this.celulasAutosValidosRetorno = celulasAutosValidosRetorno;
	}

	
	public Integer[] getCelulasFluxoMoto() {
		return celulasFluxoMoto;
	}
	public void setCelulasFluxoMoto(Integer[] celulasFluxoMoto) {
		this.celulasFluxoMoto = celulasFluxoMoto;
	}

	
	public Integer[] getCelulasFluxoPequeno() {
		return celulasFluxoPequeno;
	}
	public void setCelulasFluxoPequeno(Integer[] celulasFluxoPequeno) {
		this.celulasFluxoPequeno = celulasFluxoPequeno;
	}

	
	public Integer[] getCelulasFluxoMedio() {
		return celulasFluxoMedio;
	}
	public void setCelulasFluxoMedio(Integer[] celulasFluxoMedio) {
		this.celulasFluxoMedio = celulasFluxoMedio;
	}

	
	public Integer[] getCelulasFluxoGrande() {
		return celulasFluxoGrande;
	}
	public void setCelulasFluxoGrande(Integer[] celulasFluxoGrande) {
		this.celulasFluxoGrande = celulasFluxoGrande;
	}

	
	public Integer[] getCelulasFluxoSemId() {
		return celulasFluxoSemId;
	}
	public void setCelulasFluxoSemId(Integer[] celulasFluxoSemId) {
		this.celulasFluxoSemId = celulasFluxoSemId;
	}

	
	public String[] getCelulasColunasRelatorio() {
		return celulasColunasRelatorio;
	}
	public void setCelulasColunasRelatorio(String[] celulasColunasRelatorio) {
		this.celulasColunasRelatorio = celulasColunasRelatorio;
	}

	
	public Integer[] getCelulasValorColuna() {
		return celulasValorColuna;
	}
	public void setCelulasValorColuna(Integer[] celulasValorColuna) {
		this.celulasValorColuna = celulasValorColuna;
	}

	
	public Integer getQtdeColunas() {
		return qtdeColunas;
	}
	public void setQtdeColunas(Integer qtdeColunas) {
		this.qtdeColunas = qtdeColunas;
	}

	
	public Integer getFluxoVeicularSegunda() {
		return fluxoVeicularSegunda;
	}
	public void setFluxoVeicularSegunda(Integer fluxoVeicularSegunda) {
		this.fluxoVeicularSegunda = fluxoVeicularSegunda;
	}


	public Integer getFluxoVeicularTerca() {
		return fluxoVeicularTerca;
	}
	public void setFluxoVeicularTerca(Integer fluxoVeicularTerca) {
		this.fluxoVeicularTerca = fluxoVeicularTerca;
	}


	public Integer getFluxoVeicularQuarta() {
		return fluxoVeicularQuarta;
	}
	public void setFluxoVeicularQuarta(Integer fluxoVeicularQuarta) {
		this.fluxoVeicularQuarta = fluxoVeicularQuarta;
	}


	public Integer getFluxoVeicularQuinta() {
		return fluxoVeicularQuinta;
	}
	public void setFluxoVeicularQuinta(Integer fluxoVeicularQuinta) {
		this.fluxoVeicularQuinta = fluxoVeicularQuinta;
	}


	public Integer getFluxoVeicularSexta() {
		return fluxoVeicularSexta;
	}
	public void setFluxoVeicularSexta(Integer fluxoVeicularSexta) {
		this.fluxoVeicularSexta = fluxoVeicularSexta;
	}


	public Integer getFluxoVeicularSabado() {
		return fluxoVeicularSabado;
	}
	public void setFluxoVeicularSabado(Integer fluxoVeicularSabado) {
		this.fluxoVeicularSabado = fluxoVeicularSabado;
	}


	public Integer getFluxoVeicularDomingo() {
		return fluxoVeicularDomingo;
	}
	public void setFluxoVeicularDomingo(Integer fluxoVeicularDomingo) {
		this.fluxoVeicularDomingo = fluxoVeicularDomingo;
	}


	public Integer getAutosDetectadosEnq56732() {
		return autosDetectadosEnq56732;
	}


	public void setAutosDetectadosEnq56732(Integer autosDetectadosEnq56732) {
		this.autosDetectadosEnq56732 = autosDetectadosEnq56732;
	}


	public Integer getAutosDetectadosEnq60503() {
		return autosDetectadosEnq60503;
	}


	public void setAutosDetectadosEnq60503(Integer autosDetectadosEnq60503) {
		this.autosDetectadosEnq60503 = autosDetectadosEnq60503;
	}


	public Integer[] getCelulasAutosDetectadosEnq56732() {
		return celulasAutosDetectadosEnq56732;
	}


	public void setCelulasAutosDetectadosEnq56732(
			Integer[] celulasAutosDetectadosEnq56732) {
		this.celulasAutosDetectadosEnq56732 = celulasAutosDetectadosEnq56732;
	}


	public Integer[] getCelulasAutosDetectadosEnq60503() {
		return celulasAutosDetectadosEnq60503;
	}


	public void setCelulasAutosDetectadosEnq60503(
			Integer[] celulasAutosDetectadosEnq60503) {
		this.celulasAutosDetectadosEnq60503 = celulasAutosDetectadosEnq60503;
	}


	public Integer getAutosValidosEnq56732() {
		return autosValidosEnq56732;
	}


	public void setAutosValidosEnq56732(Integer autosValidosEnq56732) {
		this.autosValidosEnq56732 = autosValidosEnq56732;
	}


	public Integer getAutosValidosEnq60503() {
		return autosValidosEnq60503;
	}


	public void setAutosValidosEnq60503(Integer autosValidosEnq60503) {
		this.autosValidosEnq60503 = autosValidosEnq60503;
	}


	public Integer[] getCelulasAutosValidosEnq56732() {
		return celulasAutosValidosEnq56732;
	}


	public void setCelulasAutosValidosEnq56732(Integer[] celulasAutosValidosEnq56732) {
		this.celulasAutosValidosEnq56732 = celulasAutosValidosEnq56732;
	}


	public Integer[] getCelulasAutosValidosEnq60503() {
		return celulasAutosValidosEnq60503;
	}


	public void setCelulasAutosValidosEnq60503(Integer[] celulasAutosValidosEnq60503) {
		this.celulasAutosValidosEnq60503 = celulasAutosValidosEnq60503;
	}
	
	

	public Integer getAutosInvalidos() {
		return autosInvalidos;
	}


	public void setAutosInvalidos(Integer autosInvalidos) {
		this.autosInvalidos = autosInvalidos;
	}


	public Integer getAutosInvalidosMotivoTecnico() {
		return autosInvalidosMotivoTecnico;
	}


	public void setAutosInvalidosMotivoTecnico(Integer autosInvalidosMotivoTecnico) {
		this.autosInvalidosMotivoTecnico = autosInvalidosMotivoTecnico;
	}


	public Integer getAutosInvalidosMotivoNaoTecnico() {
		return autosInvalidosMotivoNaoTecnico;
	}


	public void setAutosInvalidosMotivoNaoTecnico(
			Integer autosInvalidosMotivoNaoTecnico) {
		this.autosInvalidosMotivoNaoTecnico = autosInvalidosMotivoNaoTecnico;
	}
}
