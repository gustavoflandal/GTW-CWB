package com.consilux.conf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuração que representa a configuração de descarga do GTW.
 * @author raoni
 */
public class ConfiguracaoDescarga implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Boolean ativo;
	
	private String diretorioSaida;
	
	private List<String> arquivosExternos = new ArrayList<String>();
	private String appJar;
	
	private int numeroMaximoDiasFinalDescarga;
	private long tamanhoMidia;
	
	private String horarioJobExportacao;
	
	public ConfiguracaoDescarga() {
		// Construtor padrão sem parâmetros, padrão JavaBeans		
	}

	public String getDiretorioSaida() {
		return diretorioSaida;
	}

	public void setDiretorioSaida(String diretorioSaida) {
		this.diretorioSaida = diretorioSaida;
	}

	public List<String> getArquivosExternos() {
		return arquivosExternos;
	}

	public void setArquivosExternos(List<String> arquivosExternos) {
		this.arquivosExternos = arquivosExternos;
	}
	
	public int getNumeroMaximoDiasFinalDescarga() {
		return numeroMaximoDiasFinalDescarga;
	}

	public void setNumeroMaximoDiasFinalDescarga(int numeroMaximoDiasFinalDescarga) {
		this.numeroMaximoDiasFinalDescarga = numeroMaximoDiasFinalDescarga;
	}

	public long getTamanhoMidia() {
		return tamanhoMidia;
	}

	public void setTamanhoMidia(long tamanhoMidia) {
		this.tamanhoMidia = tamanhoMidia;
	}

	public String getAppJar() {
		return appJar;
	}

	public void setAppJar(String appJar) {
		this.appJar = appJar;
	}

	public void setHorarioJobExportacao(String horarioJobExportacao) {
		this.horarioJobExportacao = horarioJobExportacao;
	}

	public String getHorarioJobExportacao() {
		return horarioJobExportacao;
	}

	/**
	 * @return the ativo
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
}
