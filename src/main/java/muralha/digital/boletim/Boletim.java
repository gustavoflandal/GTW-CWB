package muralha.digital.boletim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Boletim")
@XmlAccessorType(XmlAccessType.FIELD)
public class Boletim {
    private Integer id;
    private Integer idTipo;
    private Integer idLocal;
    private Integer idSituacao;
    private String detalhamento;
    private Integer idUsuario;
    private Date dataCriacao;
    private Date dataEncerramento;
    private Integer permite_atendimento;
    
    private List<BoletimVeiculo> veiculos;
    private List<BoletimIndividuo> individuos;
    private List<BoletimDocumento> documentos;
    private List<BoletimApreensao> apreensoes;
    // Campos adicionais baseados no diagrama
    private String tipo; // Descrição do tipo de ocorrência
    private String situacao; // Descrição da situação
    private String usuario; // Nome do usuário que criou
    private String nomeUsuario; // Nome completo do usuário
    private BoletimLocal local; // Objeto com detalhes do local

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public Integer getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Integer idLocal) {
        this.idLocal = idLocal;
    }

    public Integer getIdSituacao() {
        return idSituacao;
    }

    public void setPermitirAtendimento(Integer permitirAtendimento) {
        this.permite_atendimento = permitirAtendimento;
    }
    
    public Integer getPermitirAtendimento() {
        return permite_atendimento;
    }

    public void setIdSituacao(Integer idSituacao) {
        this.idSituacao = idSituacao;
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

    public List<BoletimVeiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<BoletimVeiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public List<BoletimIndividuo> getIndividuos() {
        return individuos;
    }

    public void setIndividuos(List<BoletimIndividuo> individuos) {
        this.individuos = individuos;
    }

    public List<BoletimDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<BoletimDocumento> documentos) {
        this.documentos = documentos;
    }
    
    public List<BoletimApreensao> getApreensoes() {
        return this.apreensoes;
    }

    public void setApreensoes(List<BoletimApreensao> apreensoes) {
        this.apreensoes = apreensoes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public BoletimLocal getLocal() {
        return local;
    }

    public void setLocal(BoletimLocal local) {
        this.local = local;
    }
}