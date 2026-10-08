package muralha.digital.registroDeFato;

import java.util.Date;
import java.util.List;

public class Boletim {
	private Integer id;
	private Long idRegistroFato;
	private Integer idSituacao;
	private Date dataHoraEvento;
	private String detalhamento;
	private Integer idUsuario;
	private Date dataCriacao;
	private Date dataEncerramento;
	private Integer permiteAtendimento;
	private BoletimSituacao situacao;
	private List<BoletimApreensao> apreensoes;
	
	public Boletim() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getIdRegistroFato() {
		return idRegistroFato;
	}

	public void setIdRegistroFato(Long idRegistroFato) {
		this.idRegistroFato = idRegistroFato;
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

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	public Integer getPermiteAtendimento() {
		return permiteAtendimento;
	}

	public void setPermiteAtendimento(Integer permiteAtendimento) {
		this.permiteAtendimento = permiteAtendimento;
	}

	public BoletimSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(BoletimSituacao situacao) {
		this.situacao = situacao;
	}
	
	public List<BoletimApreensao> getApreensoes() {
		return apreensoes;
	}

	public void setApreensoes(List<BoletimApreensao> apreensoes) {
		this.apreensoes = apreensoes;
	}
	
    public boolean equalsConteudo(BoletimApreensao other) {
        if (other == null) {
            return false;
        }
        return java.util.Objects.equals(this.getId(), other.getId());
    }
    
    public boolean equalsConteudo(Boletim outro) {
        if (outro == null) {
            return false;
        }
        return java.util.Objects.equals(this.getId(), outro.getId());
    }
}
