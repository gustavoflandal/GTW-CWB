package muralha.digital.registroDeFato;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@XmlRootElement(name = "CadastroVeiculoMonitorado")
@XmlAccessorType(XmlAccessType.FIELD)
public class CadastroVeiculoMonitorado {

    @XmlElement(name = "id")
    private UUID id;

    @XmlElement(name = "placa")
    private String placa;

    @XmlElement(name = "idTipoAlertaOcorrencia")
    private UUID idTipoAlertaOcorrencia;

    @XmlElement(name = "descricao")
    private String descricao;

    @XmlElement(name = "dataInicio")
    private Date dataInicio;

    @XmlElement(name = "dataFim")
    private Date dataFim;

    @XmlElement(name = "dataCadastro")
    private Date dataCadastro;

    @XmlElement(name = "idUsuario")
    private Integer idUsuario;

    @XmlElement(name = "dataExclusao")
    private Date dataExclusao;

    @XmlElement(name = "idUsuarioExclusao")
    private Integer idUsuarioExclusao;

    @XmlElement(name = "motivoExclusao")
    private String motivoExclusao;

    @XmlElement(name = "idUsuarioAtualizacao")
    private Integer idUsuarioAtualizacao;

    @XmlElement(name = "dataAtualizacao")
    private Date dataAtualizacao;

    @XmlElement(name = "nome")
    private String nome;

    @XmlElement(name = "dataInativacao")
    private Date dataInativacao;

    @XmlElement(name = "idUsuarioInativacao")
    private Integer idUsuarioInativacao;

    @XmlElement(name = "privado")
    private Boolean privado;

    @XmlElement(name = "supervisionado")
    private Boolean supervisionado;

    @XmlElement(name = "errosPermitidosPlaca")
    private Integer errosPermitidosPlaca;

    @XmlElement(name = "idRegistroFato")
    private Long idRegistroFato;

    // Getters e Setters

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

    public Long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(Long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }
}