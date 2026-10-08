package muralha.digital.registroDeFato;

import java.util.List;

public class RegistroFatoDTO {
	
	private int id;
    private int atendimentoPermitido;
    private int envolvimentoArmas;
    private int privado;
    private int tipoRegistro;
    private int idStatus;
    private String dataHoraOcorrido;
    private String detalhamentoFato;
    private List<Integer> idsGrupos;
    private List<Integer> idsUsuarios;
    private List<RegistroFatoAnotacaoDTO> anotacoes;
          
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAtendimentoPermitido() {
		return atendimentoPermitido;
	}
	
	public void setAtendimentoPermitido(int atendimentoPermitido) {
		this.atendimentoPermitido = atendimentoPermitido;
	}
	
	public int getEnvolvimentoArmas() {
		return envolvimentoArmas;
	}
	
	public void setEnvolvimentoArmas(int envolvimentoArmas) {
		this.envolvimentoArmas = envolvimentoArmas;
	}
	
	public int getPrivado() {
		return privado;
	}
	
	public void setPrivado(int privado) {
		this.privado = privado;
	}
	
	public int getTipoRegistro() {
		return tipoRegistro;
	}
	
	public void setTipoRegistro(int tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}
			
	public int getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}

	public String getDataHoraOcorrido() {
		return dataHoraOcorrido;
	}
	
	public void setDataHoraOcorrido(String dataHoraOcorrido) {
		this.dataHoraOcorrido = dataHoraOcorrido;
	}
	
	public String getDetalhamentoFato() {
		return detalhamentoFato;
	}
	
	public void setDetalhamentoFato(String detalhamentoFato) {
		this.detalhamentoFato = detalhamentoFato;
	}

	public List<Integer> getIdsGrupos() {
		return idsGrupos;
	}

	public void setIdsGrupos(List<Integer> idsGrupos) {
		this.idsGrupos = idsGrupos;
	}

	public List<Integer> getIdsUsuarios() {
		return idsUsuarios;
	}

	public void setIdsUsuarios(List<Integer> idsUsuarios) {
		this.idsUsuarios = idsUsuarios;
	}		
	
    public List<RegistroFatoAnotacaoDTO> getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(List<RegistroFatoAnotacaoDTO> anotacoes) {
        this.anotacoes = anotacoes;
    }
}
