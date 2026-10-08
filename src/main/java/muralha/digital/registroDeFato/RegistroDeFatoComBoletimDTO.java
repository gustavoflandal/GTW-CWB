package muralha.digital.registroDeFato;

import java.util.List;

public class RegistroDeFatoComBoletimDTO {
    private RegistroDeFatoDTO registro;
    private BoletimDTO boletim;
    private Integer id;
    private List<RegistroDeFatoIndividuoDTO> individuos;
    private List<RegistroDeFatoEnderecoDTO> enderecos;
    private List<RegistroDeFatoVeiculoDTO> veiculos;
    private List<RegistroDeFatoDocumentoDTO> documentos;
    private List<RegistroDeFatoLinkDTO> links;
    private RegistroDeFatoUsuarioGrupoDTO grupos;
    private List<RegistroDeFatoObjetoDTO> objetos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    // Getters e Setters
    public RegistroDeFatoDTO getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroDeFatoDTO registro) {
        this.registro = registro;
    }

    public BoletimDTO getBoletim() {
        return boletim;
    }

    public void setBoletim(BoletimDTO boletim) {
        this.boletim = boletim;
    }

    public List<RegistroDeFatoIndividuoDTO> getIndividuos() {
        return individuos;
    }

    public void setIndividuos(List<RegistroDeFatoIndividuoDTO> individuos) {
        this.individuos = individuos;
    }

    public List<RegistroDeFatoEnderecoDTO> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<RegistroDeFatoEnderecoDTO> enderecos) {
        this.enderecos = enderecos;
    }

    public List<RegistroDeFatoVeiculoDTO> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<RegistroDeFatoVeiculoDTO> veiculos) {
        this.veiculos = veiculos;
    }

    public List<RegistroDeFatoDocumentoDTO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<RegistroDeFatoDocumentoDTO> documentos) {
        this.documentos = documentos;
    }

    public List<RegistroDeFatoLinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<RegistroDeFatoLinkDTO> links) {
        this.links = links;
    }

    public RegistroDeFatoUsuarioGrupoDTO getGrupos() {
        return grupos;
    }

    public void setGrupos(RegistroDeFatoUsuarioGrupoDTO grupos) {
        this.grupos = grupos;
    }

    public List<RegistroDeFatoObjetoDTO> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<RegistroDeFatoObjetoDTO> objetos) {
        this.objetos = objetos;
    }
}
