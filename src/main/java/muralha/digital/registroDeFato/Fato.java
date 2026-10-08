package muralha.digital.registroDeFato;

import java.util.Date;

public class Fato {

    private Integer id;
    private Long idRegistroFato;
    private Integer idSituacao;
    private Date dataHoraEvento;
    private String detalhamento;
    private Integer existeArmaEnvolvida;
    private Integer idUsuario;
    private Date dataCriacao;
    private Date dataEncerramento;
    private Integer permiteAtendimento;

    public Fato() {
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

    public Integer getExisteArmaEnvolvida() {
        return existeArmaEnvolvida;
    }

    public void setExisteArmaEnvolvida(Integer existeArmaEnvolvida) {
        this.existeArmaEnvolvida = existeArmaEnvolvida;
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
    
    public boolean equalsConteudo(Fato outro) {
        if (outro == null) {
            return false;
        }
        return java.util.Objects.equals(this.getId(), outro.getId());
    }
}