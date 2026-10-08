package muralha.digital.registroDeFato;

import java.util.Date;
import java.math.BigDecimal;
import java.util.UUID;

public class VeiculoTempoReal {

    private UUID id;
    private String placa;
    private Date data;
    private Integer idLocal;
    private Byte idPista;
    private Short velocidade;
    private Boolean enviadoCliente;
    private Date dataEnviado;
    private String classificacao;
    private Byte estadoVeiculo;
    private Byte processadoTarefasAlerta;
    private Date dataProcessadoTarefasAlerta;
    private Date dataImportado;
    private Integer idUsuarioAlt;
    private Date dataAlt;
    private String dadosAltOrig;
    private Boolean enviadoBlitz;
    private Date dataEnviadoBlitz;
    private String perfil1;
    private String perfil2;
    private String placaFrontal;
    private String infoAdicional;
    private Integer numeroEixos;
    private Boolean rodagemDupla;
    private Integer categoria;

    public VeiculoTempoReal() {
    }

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

    public Byte getIdPista() {
        return idPista;
    }

    public void setIdPista(Byte idPista) {
        this.idPista = idPista;
    }

    public Short getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(Short velocidade) {
        this.velocidade = velocidade;
    }

    public Boolean getEnviadoCliente() {
        return enviadoCliente;
    }

    public void setEnviadoCliente(Boolean enviadoCliente) {
        this.enviadoCliente = enviadoCliente;
    }

    public Date getDataEnviado() {
        return dataEnviado;
    }

    public void setDataEnviado(Date dataEnviado) {
        this.dataEnviado = dataEnviado;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public Byte getEstadoVeiculo() {
        return estadoVeiculo;
    }

    public void setEstadoVeiculo(Byte estadoVeiculo) {
        this.estadoVeiculo = estadoVeiculo;
    }

    public Byte getProcessadoTarefasAlerta() {
        return processadoTarefasAlerta;
    }

    public void setProcessadoTarefasAlerta(Byte processadoTarefasAlerta) {
        this.processadoTarefasAlerta = processadoTarefasAlerta;
    }

    public Date getDataProcessadoTarefasAlerta() {
        return dataProcessadoTarefasAlerta;
    }

    public void setDataProcessadoTarefasAlerta(Date dataProcessadoTarefasAlerta) {
        this.dataProcessadoTarefasAlerta = dataProcessadoTarefasAlerta;
    }

    public Date getDataImportado() {
        return dataImportado;
    }

    public void setDataImportado(Date dataImportado) {
        this.dataImportado = dataImportado;
    }

    public Integer getIdUsuarioAlt() {
        return idUsuarioAlt;
    }

    public void setIdUsuarioAlt(Integer idUsuarioAlt) {
        this.idUsuarioAlt = idUsuarioAlt;
    }

    public Date getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(Date dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDadosAltOrig() {
        return dadosAltOrig;
    }

    public void setDadosAltOrig(String dadosAltOrig) {
        this.dadosAltOrig = dadosAltOrig;
    }

    public Boolean getEnviadoBlitz() {
        return enviadoBlitz;
    }

    public void setEnviadoBlitz(Boolean enviadoBlitz) {
        this.enviadoBlitz = enviadoBlitz;
    }

    public Date getDataEnviadoBlitz() {
        return dataEnviadoBlitz;
    }

    public void setDataEnviadoBlitz(Date dataEnviadoBlitz) {
        this.dataEnviadoBlitz = dataEnviadoBlitz;
    }

    public String getPerfil1() {
        return perfil1;
    }

    public void setPerfil1(String perfil1) {
        this.perfil1 = perfil1;
    }

    public String getPerfil2() {
        return perfil2;
    }

    public void setPerfil2(String perfil2) {
        this.perfil2 = perfil2;
    }

    public String getPlacaFrontal() {
        return placaFrontal;
    }

    public void setPlacaFrontal(String placaFrontal) {
        this.placaFrontal = placaFrontal;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public Integer getNumeroEixos() {
        return numeroEixos;
    }

    public void setNumeroEixos(Integer numeroEixos) {
        this.numeroEixos = numeroEixos;
    }

    public Boolean getRodagemDupla() {
        return rodagemDupla;
    }

    public void setRodagemDupla(Boolean rodagemDupla) {
        this.rodagemDupla = rodagemDupla;
    }

    public Integer getCategoria() {
        return categoria;
    }

    public void setCategoria(Integer categoria) {
        this.categoria = categoria;
    }
}
