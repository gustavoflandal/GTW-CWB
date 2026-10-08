package muralha.digital.blitz;

import java.util.Date;

public class VeiculoBlitzDTO {

    private String idVeiculoTempoReal;
    private String placa;
    private Date data;
    private Integer idLocal;
    private Integer idPista;
    private Integer idAlerta;
    private String tipoAlerta;
    private String observacaoAlerta;
    private Integer velocidade;
    private String classificacao;
    private String blitzes;

    // ======================
    // Getters & Setters
    // ======================

    public String getIdVeiculoTempoReal() {
        return idVeiculoTempoReal;
    }

    public void setIdVeiculoTempoReal(String idVeiculoTempoReal) {
        this.idVeiculoTempoReal = idVeiculoTempoReal;
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(Integer idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getObservacaoAlerta() {
        return observacaoAlerta;
    }

    public void setObservacaoAlerta(String observacaoAlerta) {
        this.observacaoAlerta = observacaoAlerta;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdPista() {
        return idPista;
    }

    public void setIdPista(Integer idPista) {
        this.idPista = idPista;
    }

    public Integer getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(Integer velocidade) {
        this.velocidade = velocidade;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getBlitzes() {
        return blitzes;
    }

    public void setBlitzes(String blitzes) {
        this.blitzes = blitzes;
    }
}
