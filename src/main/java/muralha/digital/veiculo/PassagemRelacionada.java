package muralha.digital.veiculo;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import muralha.digital.veiculo.imagem.VeiculoImagem;


@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class PassagemRelacionada {
    private UUID id;
    private String placa;
    private Date dataVeic;
    private String dataVeicFormatada;
    private int idLocal;
    private Integer serieEquipamento;
    private String codigoEquipamento;
    private String descLocal;
    private int idPista;
    private int faixa;
    private double latitude;
    private double longitude;
    private Integer velocidade;
    private String classificacao;
    private String tipoVeiculo;
    private boolean comImagem;
    private boolean possuiCoordenadas;
    private String marca;
    private String modelo;
    private String corPlaca;
    private boolean possuiAlerta;
    private String idAlerta;
    @XmlElement(name = "tipo_alerta")
    private String tipoAlerta;
    @XmlElement(name = "observacao_alerta")
    private String observacaoAlerta;
    
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
    public Date getDataVeic() {
        return dataVeic;
    }
    public void setDataVeic(Date dataVeic) {
        this.dataVeic = dataVeic;
        setDataVeicFormatada();
    }
    public String getDataVeicFormatada() {
        return dataVeicFormatada;
    }
    public void setDataVeicFormatada() {
        if (dataVeic != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            this.dataVeicFormatada = sdf.format(dataVeic);
        }
    }
    public int getIdLocal() {
        return idLocal;
    }
    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }
    public Integer getSerieEquipamento() {
        return serieEquipamento;
    }
    public void setSerieEquipamento(Integer serieEquipamento) {
        this.serieEquipamento = serieEquipamento;
    }
    public String getCodigoEquipamento() {
        return codigoEquipamento;
    }
    public void setCodigoEquipamento(String codigoEquipamento) {
        this.codigoEquipamento = codigoEquipamento;
    }
    public String getDescLocal() {
        return descLocal;
    }
    public void setDescLocal(String descLocal) {
        this.descLocal = descLocal;
    }
    public int getIdPista() {
        return idPista;
    }
    public void setIdPista(int idPista) {
        this.idPista = idPista;
    }
    public int getFaixa() {
        return faixa;
    }
    public void setFaixa(int faixa) {
        this.faixa = faixa;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
    public String getTipoVeiculo() {
        return tipoVeiculo;
    }
    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }
    public boolean isComImagem() {
        return comImagem;
    }
    public void setComImagem(boolean comImagem) {
        this.comImagem = comImagem;
    }
    public boolean isPossuiCoordenadas() {
        return possuiCoordenadas;
    }
    public void setPossuiCoordenadas(boolean possuiCoordenadas) {
        this.possuiCoordenadas = possuiCoordenadas;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getCorPlaca() {
        return corPlaca;
    }
    public void setCorPlaca(String corPlaca) {
        this.corPlaca = corPlaca;
    }
    public boolean isPossuiAlerta() {
        return possuiAlerta;
    }
    public void setPossuiAlerta(boolean possuiAlerta) {
        this.possuiAlerta = possuiAlerta;
    }
    public String getIdAlerta() {
        return idAlerta;
    }
    public void setIdAlerta(String idAlerta) {
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
}