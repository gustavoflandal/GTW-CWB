package com.consilux.model.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.consilux.model.Infracao;

@XmlRootElement(name="infracao")
public class InfoInfracaoBean {

	private int idInfracao;
	private String placa;
	private Long idVeiculo;
	private String data;
	private int idInconsistencia;
	private int idImagemObjetiva;

	public InfoInfracaoBean()
	{
		// Construtor sem parâmetros, padrão do JavaBeans.
	}

	/**
	 * Construtor que recebe um objeto infração do GTW.
	 * @param infracao
	 */
	public InfoInfracaoBean(Infracao infracao) {
		super();
		this.idInfracao = infracao.getIdInfracao();;
		this.placa = infracao.getPlaca();
		this.idVeiculo = infracao.getIdVeiculo().longValue();
		this.data = new SimpleDateFormat("dd/MM/yyyy").format(infracao.getData());
		this.idInconsistencia = infracao.getIdInconsistencia();
		this.idImagemObjetiva = infracao.getIdImagemOBJ();
	}

	@XmlElement(name="ID_INFRACAO")
	public int getIdInfracao() {
		return idInfracao;
	}

	public void setIdInfracao(int idInfracao) {
		this.idInfracao = idInfracao;
	}

	@XmlElement(name="PLACA")
	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	@XmlElement(name="VEICULO")
	public Long getIdVeiculo() {
		return idVeiculo;
	}

	public void setIdVeiculo(Long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	@XmlElement(name="DATA")
	public String getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	@XmlElement(name="INCONSISTENCIA")
	public int getIdInconsistencia() {
		return idInconsistencia;
	}

	public void setIdInconsistencia(int idInconsistencia) {
		this.idInconsistencia = idInconsistencia;
	}

	@XmlElement(name="ID_IMAGEM_OBJ")
	public int getIdImagemObjetiva() {
		return idImagemObjetiva;
	}

	public void setIdImagemObjetiva(int idImagemObjetiva) {
		this.idImagemObjetiva = idImagemObjetiva;
	}

}
