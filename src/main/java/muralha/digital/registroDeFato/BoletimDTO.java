package muralha.digital.registroDeFato;

import java.util.Date;
import java.util.List;

public class BoletimDTO {
	private Integer id;
    private Integer idSituacao;
    private Date dataHoraEvento;
    private String detalhamento;
    private Boolean permiteAtendimento;
    private List<BoletimApreensaoDTO> apreensoes;

    public BoletimDTO() {
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public Integer getIdSituacao() {
        return idSituacao;
    }

    public void setIdSituacao(Integer idSituacao) {
        this.idSituacao = idSituacao;
    }

    public Date getDataHoraEvento() {
        return dataHoraEvento;
    }

    public void setDataHoraEvento(Date dataHoraEvento) {
        this.dataHoraEvento = dataHoraEvento;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }

    public Boolean getPermiteAtendimento() {
        return permiteAtendimento;
    }

    public void setPermiteAtendimento(Boolean permiteAtendimento) {
        this.permiteAtendimento = permiteAtendimento;
    }

    public List<BoletimApreensaoDTO> getApreensoes() {
        return apreensoes;
    }

    public void setApreensoes(List<BoletimApreensaoDTO> apreensoes) {
        this.apreensoes = apreensoes;
    }
}
