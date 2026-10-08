package com.consilux.model.beans;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.consilux.infra.Funcoes;
import com.consilux.model.AmostraImagem;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.AmostraVeiculoGwtBean;

public class AmostraVeiculoBean implements Serializable {

	private static final long serialVersionUID = 350618020887537660L;
	
	private Calendar data;
	private int idLocal;
	private byte idPista;
	private boolean fixada;
	private boolean metrologica;
	private boolean escolhidaManualmente;
	private boolean aplicavel = true;
	
	private String tipo; 
	private Long idVeiculo;
	private Integer idInfracao;
	private Integer score;

	Map<Integer, Boolean> mapaImagens = new TreeMap<Integer, Boolean>();	
	
	private int serieEquipamento;
	private String nomePista;
	private int codPistaAlternativo;
	private int codPistaProdam;
	private int codPista;
	private boolean pistaAtiva;

	public AmostraVeiculoBean() {
		super();
		// Construtor sem parâmetros, padrão Javabeans para
		// atender o Serializable.		
	}

	public AmostraVeiculoBean(Calendar data, int idLocal, byte idPista,
			boolean metrologica, boolean fixada, boolean escolhidaManualmente, int serieEquipamento,
			String nomePista, int codPistaAlternativo,
			int codPistaProdam, int codPista, boolean aplicavel) {
		super();
		this.data = data;
		this.idLocal = idLocal;
		this.idPista = idPista;
		this.metrologica = metrologica;
		this.fixada = fixada;
		this.escolhidaManualmente = escolhidaManualmente;
		this.serieEquipamento = serieEquipamento;
		this.nomePista = nomePista;
		this.codPistaAlternativo = codPistaAlternativo;
		this.codPistaProdam = codPistaProdam;
		this.codPista = codPista;
		this.aplicavel = aplicavel;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public int getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(int idLocal) {
		this.idLocal = idLocal;
	}

	public byte getIdPista() {
		return idPista;
	}

	public void setIdPista(byte idPista) {
		this.idPista = idPista;
	}

	public boolean isMetrologica() {
		return metrologica;
	}

	public boolean isFixada() {
		return fixada;
	}

	public void setMetrologica(boolean metrologica) {
		this.metrologica = metrologica;
	}

	public void setFixada(boolean fixada) {
		this.fixada = fixada;
	}

	public boolean isEscolhidaManualmente() {
		return escolhidaManualmente;
	}

	public void setEscolhidaManualmente(boolean escolhidaManualmente) {
		this.escolhidaManualmente = escolhidaManualmente;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getIdVeiculo() {
		return idVeiculo;
	}

	public void setIdVeiculo(Long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	public Integer getIdInfracao() {
		return idInfracao;
	}

	public void setIdInfracao(Integer idInfracao) {
		this.idInfracao = idInfracao;
	}

	public Map<Integer, Boolean> getMapaImagens() {
		return mapaImagens;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public int getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(int serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public String getNomePista() {
		return nomePista;
	}

	public void setNomeLocal(String nomePista) {
		this.nomePista = nomePista;
	}

	public int getCodPistaAlternativo() {
		return codPistaAlternativo;
	}

	public int getCodPistaAlternativoDisponivel() {
		return codPistaAlternativo > 0 ? codPistaAlternativo : idPista;
	}

	public void setCodPistaAlternativo(int codPistaAlternativo) {
		this.codPistaAlternativo = codPistaAlternativo;
	}

	public int getCodPistaProdam() {
		return codPistaProdam;
	}

	public void setCodPistaProdam(int codPistaProdam) {
		this.codPistaProdam = codPistaProdam;
	}

	public int getCodPista() {
		return codPista;
	}

	public void setCodPista(int codPista) {
		this.codPista = codPista;
	}

	public boolean isPistaAtiva() {
		return pistaAtiva;
	}
	
	public Color getProporcaoRGB() {
		return Funcoes.propVermelhorAmareloVerde(AmostraImagem.PONTO_MAX, AmostraImagem.PONTO_MIN, score);
	}

	public Color getCorFixada() {
		return Color.BLACK;
	}

	public void setPistaAtiva(boolean pistaAtiva) {
		this.pistaAtiva = pistaAtiva;
	}
	
	public boolean isAplicavel() {
		return aplicavel;
	}

	public void setAplicavel(boolean aplicavel) {
		this.aplicavel = aplicavel;
	}

	/**
	 * Converte este bean (do GTW) para um bean do GWT (que é mais reduzido). 
	 * @return um bean do GWT.
	 */
	public AmostraVeiculoGwtBean toGwtBean() {
		
		AmostraVeiculoGwtBean ret = new AmostraVeiculoGwtBean();
		
		ret.setData(this.getData().getTime());
		ret.setIdEquipamento(this.getIdLocal());
		ret.setPista(this.getIdPista());
		ret.setCodPista(this.getCodPista());
		ret.setCodPistaAlternativo(this.getCodPistaAlternativo());
		ret.setCodPistaProdam(this.getCodPistaProdam());
		ret.setMetrologica(this.isMetrologica());
		ret.setFixada(this.isFixada());
		ret.setPontuacao(this.getScore());
		ret.setId(this.getIdVeiculo() != null ? this.getIdVeiculo().toString() : null);
		ret.setEscolhidaManualmente(this.isEscolhidaManualmente());
		ret.setAplicavel(this.isAplicavel());
		
		return ret;
	}

	/**
	 * Converte uma coleção de beans do GTW para uma lista de beans do GWT.
	 * @param colecaoOriginal
	 * @return
	 * @throws ModelException caso a colecaoOriginal seja nula.
	 */
	public static List<AmostraVeiculoGwtBean> toGwtBeans(Iterable<AmostraVeiculoBean> colecaoOriginal)
		throws ModelException
	{
		if (colecaoOriginal == null)
			throw new ModelException("Argumento nulo: colecaoOriginal");
		
		List<AmostraVeiculoGwtBean> lRet = new ArrayList<AmostraVeiculoGwtBean>();
		
		for (AmostraVeiculoBean beanOriginal : colecaoOriginal) {
			lRet.add(beanOriginal.toGwtBean());
		}
		return lRet;
	}		
	
}
