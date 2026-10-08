package muralha.digital.registroDeFato;

import java.util.Date;
import java.util.List;

public class RegistroDeFatoDTO {
	private Long id;
	private Integer idTipo;
	private Integer idStatus;
	private Integer temBoletim;
	private Boolean privado;
	private Date dataEvento;
	private Integer idNaturezaTipo;
	private Boolean permitirAtendimento;
	private String detalhamento;
	private Boolean envolvimentoArma;
    private List<RegistroFatoAnotacaoDTO> anotacoes;

	public RegistroDeFatoDTO() {
	}

	public Integer getIdTipo() {
		return idTipo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIdTipo(Integer idTipo) {
		this.idTipo = idTipo;
	}

	public Integer getIdStatus() {
		return idStatus;
	}

	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
	}

	public Integer getIdNaturezaTipo() {
		return idNaturezaTipo;
	}

	public void setIdNaturezaTipo(Integer idNaturezaTipo) {
		this.idNaturezaTipo = idNaturezaTipo;
	}

	public Integer getTemBoletim() {
		return temBoletim;
	}

	public void setTemBoletim(Integer temBoletim) {
		this.temBoletim = temBoletim;
	}

	public Boolean getPrivado() {
		return privado;
	}

	public void setPrivado(Boolean privado) {
		this.privado = privado;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public Boolean getPermitirAtendimento() {
		return permitirAtendimento;
	}

	public void setPermitirAtendimento(Boolean permitirAtendimento) {
		this.permitirAtendimento = permitirAtendimento;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public Boolean getEnvolvimentoArma() {
		return envolvimentoArma;
	}

	public void setEnvolvimentoArma(Boolean envolvimentoArma) {
		this.envolvimentoArma = envolvimentoArma;
	}
	
    public List<RegistroFatoAnotacaoDTO> getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(List<RegistroFatoAnotacaoDTO> anotacoes) {
        this.anotacoes = anotacoes;
    }
}
