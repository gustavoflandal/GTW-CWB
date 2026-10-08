package muralha.digital.registroDeFato;

import java.util.Date;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFatoHistorico")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoHistorico {

    private Long idHistorico;          // PK
    private Long idRegistro;           // FK para registro_fato
    private String dadosAnteriores;    // JSON com estado anterior
    private String dadosNovos;         // JSON com estado novo
    private String tipoOperacao;       // INSERT, UPDATE, DELETE
    private Integer idUsuario;         // usuário que alterou
    private Date dataAlteracao;        // quando ocorreu

    // Getters e Setters
    public Long getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(Long idHistorico) {
        this.idHistorico = idHistorico;
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getDadosAnteriores() {
        return dadosAnteriores;
    }

    public void setDadosAnteriores(String dadosAnteriores) {
        this.dadosAnteriores = dadosAnteriores;
    }

    public String getDadosNovos() {
        return dadosNovos;
    }

    public void setDadosNovos(String dadosNovos) {
        this.dadosNovos = dadosNovos;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}
