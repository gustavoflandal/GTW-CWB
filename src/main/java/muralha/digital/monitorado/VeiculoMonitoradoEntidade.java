package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.UUID;

@XmlRootElement(name = "VeiculoMonitorado")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoEntidade {

    private UUID id;
    private String placa;
    private UUID idTipoAlertaOcorrencia;
    private String descricao;
    private Date dataInicio;
    private Date dataFim;
    private Date dataCadastro;
    private Integer idUsuario;
    private Date dataExclusao;
    private Integer idUsuarioExclusao;
    private String motivoExclusao;
    private Integer idUsuarioAtualizacao;
    private Date dataAtualizacao;
    private String nome;
    private Date dataInativacao;
    private Integer idUsuarioInativacao;
    private Boolean privado;
    private Boolean supervisionado;
    private Integer errosPermitidosPlaca;
    private String errosPermitidosIni;
    private String errosPermitidosFim;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public UUID getIdTipoAlertaOcorrencia() {
        return idTipoAlertaOcorrencia;
    }

    public void setIdTipoAlertaOcorrencia(UUID idTipoAlertaOcorrencia) {
        this.idTipoAlertaOcorrencia = idTipoAlertaOcorrencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(Date dataExclusao) {
        this.dataExclusao = dataExclusao;
    }

    public Integer getIdUsuarioExclusao() {
        return idUsuarioExclusao;
    }

    public void setIdUsuarioExclusao(Integer idUsuarioExclusao) {
        this.idUsuarioExclusao = idUsuarioExclusao;
    }

    public String getMotivoExclusao() {
        return motivoExclusao;
    }

    public void setMotivoExclusao(String motivoExclusao) {
        this.motivoExclusao = motivoExclusao;
    }

    public Integer getIdUsuarioAtualizacao() {
        return idUsuarioAtualizacao;
    }

    public void setIdUsuarioAtualizacao(Integer idUsuarioAtualizacao) {
        this.idUsuarioAtualizacao = idUsuarioAtualizacao;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(Date dataInativacao) {
        this.dataInativacao = dataInativacao;
    }

    public Integer getIdUsuarioInativacao() {
        return idUsuarioInativacao;
    }

    public void setIdUsuarioInativacao(Integer idUsuarioInativacao) {
        this.idUsuarioInativacao = idUsuarioInativacao;
    }

    public Boolean getPrivado() {
        return privado;
    }

    public void setPrivado(Boolean privado) {
        this.privado = privado;
    }

    public Boolean getSupervisionado() {
        return supervisionado;
    }

    public void setSupervisionado(Boolean supervisionado) {
        this.supervisionado = supervisionado;
    }

    public Integer getErrosPermitidosPlaca() {
        return errosPermitidosPlaca;
    }

    public void setErrosPermitidosPlaca(Integer errosPermitidosPlaca) {
        this.errosPermitidosPlaca = errosPermitidosPlaca;
    }

    public String getErrosPermitidosIni() {
        return errosPermitidosIni;
    }

    public void setErrosPermitidosIni(String errosPermitidosIni) {
        this.errosPermitidosIni = errosPermitidosIni;
    }

    public String getErrosPermitidosFim() {
        return errosPermitidosFim;
    }

    public void setErrosPermitidosFim(String errosPermitidosFim) {
        this.errosPermitidosFim = errosPermitidosFim;
    }
}
