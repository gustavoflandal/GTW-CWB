package muralha.digital.registroDeFato;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "RegistroDeFato")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFato {

    private Long id;
    private Integer idTipo;
    private Integer idNaturezaTipo;
    private Integer idStatus;
    private Integer temBoletim;
    private Integer idUsuario;
    private Date dataCriacao;
    private Date dataEncerramento;
    private Date dataModificada;
    private Date dataEvento;
    private Integer privado;

    // Campos auxiliares (opcionalmente preenchidos com JOINs ou mapeamentos externos)
    private String tipoDescricao;
    private String statusDescricao;
    private String nomeUsuario;
    private String detalhamento;
    private Integer permiteAtendimento;
    private Integer envolvimentoArmas;
    
    @XmlElementWrapper(name = "anotacoes")
    @XmlElement(name = "anotacao")
    private List<RegistroDeFatoAnotacao> anotacoes = new ArrayList<>();
    
    @XmlElementWrapper(name = "individuos")
    @XmlElement(name = "individuo")
    private List<RegistroDeFatoIndividuo> individuos = new ArrayList<>();
    
    @XmlElementWrapper(name = "veiculos")
    @XmlElement(name = "veiculo")
    private List<RegistroDeFatoVeiculo> veiculos = new ArrayList<>();
    
    @XmlElementWrapper(name = "enderecos")
    @XmlElement(name = "endereco")
    private List<RegistroDeFatoEndereco> enderecos = new ArrayList<>();
    
    @XmlElementWrapper(name = "boletins")
    @XmlElement(name = "boletim")
    private List<Boletim> boletins = new ArrayList<>();
    
    @XmlElementWrapper(name = "objetos")
    @XmlElement(name = "objeto")
    private List<RegistroDeFatoObjeto> objetos = new ArrayList<>();
    
    @XmlElementWrapper(name = "documentos")
    @XmlElement(name = "documento")
    private List<RegistroDeFatoDocumento> documentos = new ArrayList<>();
    
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    private List<RegistroDeFatoLink> links = new ArrayList<>();
    
    @XmlElementWrapper(name = "usuarioGrupos")
    @XmlElement(name = "usuarioGrupo")
    private List<RegistroDeFatoUsuarioGrupo> usuarioGrupos = new ArrayList<>();
    
    @XmlElementWrapper(name = "passagensVeiculo")
    @XmlElement(name = "passagemVeiculo")
    private List<RegistroFatoPassagemVeiculo> passagensVeiculo = new ArrayList<>();
    
    @XmlElement(name = "naturezaTipo")
    private RegistroDeFatoNatureza naturezaTipo;
    
    private List<Fato> fatos = new ArrayList<>();
    
    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }
 
    public Integer getIdNaturezaTipo() {
        return idNaturezaTipo;
    }

    public void setIdNaturezaTipo(Integer idNaturezaTipo) {
        this.idNaturezaTipo = idNaturezaTipo;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public Integer getTemBoletim() {
        return temBoletim;
    }

    public void setTemBoletim(Integer temBoletim) {
        this.temBoletim = temBoletim;
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
    
    public Integer getPrivado() {
        return this.privado;
    }
    
    public Date getDataModificada() {
        return dataModificada;
    }

    public void setDataModificada(Date dataModificada) {
        this.dataModificada = dataModificada;
    }
    
    public Date getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(Date dataEvento) {
        this.dataEvento = dataEvento;
    }

    public void setPrivado(Integer privado) {
        this.privado = privado;
    }

    // Campos descritivos auxiliares

    public String getTipoDescricao() {
        return tipoDescricao;
    }

    public void setTipoDescricao(String tipoDescricao) {
        this.tipoDescricao = tipoDescricao;
    }

    public String getStatusDescricao() {
        return statusDescricao;
    }

    public void setStatusDescricao(String statusDescricao) {
        this.statusDescricao = statusDescricao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    
    public List<RegistroDeFatoIndividuo> getIndividuos() {
        return individuos;
    }

    public void setIndividuos(List<RegistroDeFatoIndividuo> individuos) {
        this.individuos = individuos;
    }
    
    public List<RegistroDeFatoVeiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<RegistroDeFatoVeiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public List<RegistroDeFatoEndereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<RegistroDeFatoEndereco> enderecos) {
        this.enderecos = enderecos;
    }
    
    public List<Boletim> getBoletins() {
        return boletins;
    }

    public void setBoletins(List<Boletim> boletins) {
        this.boletins = boletins;
    }
    
    public List<RegistroDeFatoObjeto> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<RegistroDeFatoObjeto> objetos) {
        this.objetos = objetos;
    }
    
    public List<RegistroDeFatoDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<RegistroDeFatoDocumento> documentos) {
        this.documentos = documentos;
    }
    
    public List<RegistroDeFatoLink> getLinks() {
        return links;
    }

    public void setLinks(List<RegistroDeFatoLink> links) {
        this.links = links;
    }
    
    public List<RegistroDeFatoUsuarioGrupo> getUsuarioGrupos() {
        return usuarioGrupos;
    }

    public void setUsuarioGrupos(List<RegistroDeFatoUsuarioGrupo> usuarioGrupos) {
        this.usuarioGrupos = usuarioGrupos;
    }
    
    public List<RegistroFatoPassagemVeiculo> getPassagensVeiculo() {
        return passagensVeiculo;
    }

    public void setPassagensVeiculo(List<RegistroFatoPassagemVeiculo> passagensVeiculo) {
        this.passagensVeiculo = passagensVeiculo;
    }
    
    public RegistroDeFatoNatureza getNaturezaTipo() {
        return naturezaTipo;
    }

    public void setNaturezaTipo(RegistroDeFatoNatureza naturezaTipo) {
        this.naturezaTipo = naturezaTipo;
    }

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public Integer getPermiteAtendimento() {
		return permiteAtendimento;
	}

	public void setPermiteAtendimento(Integer permiteAtendimento) {
		this.permiteAtendimento = permiteAtendimento;
	}

	public Integer getEnvolvimentoArmas() {
		return envolvimentoArmas;
	}

	public void setEnvolvimentoArmas(Integer envolvimentoArmas) {
		this.envolvimentoArmas = envolvimentoArmas;
	}   
	
	public List<Fato> getFatos() {
	    return fatos;
	}

	public void setFatos(List<Fato> fatos) {
	    this.fatos = fatos;
	}
	
    public List<RegistroDeFatoAnotacao> getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(List<RegistroDeFatoAnotacao> anotacoes) {
        this.anotacoes = anotacoes;
    }
}
