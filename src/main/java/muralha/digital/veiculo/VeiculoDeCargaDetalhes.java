package muralha.digital.veiculo;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "VeiculoDeCargaDetalhes")
@XmlAccessorType	(XmlAccessType.FIELD)
public class VeiculoDeCargaDetalhes {
    private long id;
    private String placa;
    private Date data;
    private int idLocal;
    private int serieEquipamento;
    private String codigoEquipamento;
    private String nome;
    private String sentido;
    private int idPista;
    private int faixa;
    private Double latitude;
    private Double longitude;
    private int velocidade;
    private String classificacao;
    private Boolean enviadoCliente;
    private Boolean comImagem;
    private Boolean comPesagem;
    private Boolean possuiCoordenadas;
    private String marca;
    private String modelo;
    private Double pbt;
    private Double pbtc;
    private int numeroEixos;
    private Double e1;
    private Double e2;
    private Double e3;
    private Double e4;
    private Double e5;
    private Double e6;
    private Double e7;
    private Double e8;
    private Double e9;
    private Double distanciaE1E2;
    private Double distanciaE2E3;
    private Double distanciaE3E4;
    private Double distanciaE4E5;
    private Double distanciaE5E6;
    private Double distanciaE6E7;
    private Double distanciaE7E8;
    private Double distanciaE8E9;
    private String classificacaoArt96;
    private Double comprimento;

    public long getId() {
        return id;
    }
    public void setId(long id) {
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
    public int getIdLocal() {
        return idLocal;
    }
    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }
    public int getSerieEquipamento() {
        return serieEquipamento;
    }
    public void setSerieEquipamento(int serieEquipamento) {
        this.serieEquipamento = serieEquipamento;
    }
    public String getCodigoEquipamento() {
        return codigoEquipamento;
    }
    public void setCodigoEquipamento(String codigoEquipamento) {
        this.codigoEquipamento = codigoEquipamento;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getSentido() {
        return sentido;
    }
    public void setSentido(String sentido) {
        this.sentido = sentido;
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
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public int getVelocidade() {
        return velocidade;
    }
    public void setVelocidade(int velocidade) {
        this.velocidade = velocidade;
    }
    public String getClassificacao() {
        return classificacao;
    }
    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }
    public Boolean getEnviadoCliente() {
        return enviadoCliente;
    }
    public void setEnviadoCliente(Boolean enviadoCliente) {
        this.enviadoCliente = enviadoCliente;
    }
    public Boolean getComImagem() {
        return comImagem;
    }
    public void setComImagem(Boolean comImagem) {
        this.comImagem = comImagem;
    }
    public Boolean getComPesagem() {
        return comPesagem;
    }
    public void setComPesagem(Boolean comPesagem) {
        this.comPesagem = comPesagem;
    }
    public Boolean getPossuiCoordenadas() {
        return possuiCoordenadas;
    }
    public void setPossuiCoordenadas(Boolean possuiCoordenadas) {
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
    public Double getPbt() {
        return pbt;
    }
    public void setPbt(Double pbt) {
        this.pbt = pbt;
    }
    public Double getPbtc() {
        return pbtc;
    }
    public void setPbtc(Double pbtc) {
        this.pbtc = pbtc;
    }
    public int getNumeroEixos() {
        return numeroEixos;
    }
    public void setNumeroEixos(int numeroEixos) {
        this.numeroEixos = numeroEixos;
    }
    public Double getE1() {
        return e1;
    }
    public void setE1(Double e1) {
        this.e1 = e1;
    }
    public Double getE2() {
        return e2;
    }
    public void setE2(Double e2) {
        this.e2 = e2;
    }
    public Double getE3() {
        return e3;
    }
    public void setE3(Double e3) {
        this.e3 = e3;
    }
    public Double getE4() {
        return e4;
    }
    public void setE4(Double e4) {
        this.e4 = e4;
    }
    public Double getE5() {
        return e5;
    }
    public void setE5(Double e5) {
        this.e5 = e5;
    }
    public Double getE6() {
        return e6;
    }
    public void setE6(Double e6) {
        this.e6 = e6;
    }
    public Double getE7() {
        return e7;
    }
    public void setE7(Double e7) {
        this.e7 = e7;
    }
    public Double getE8() {
        return e8;
    }
    public void setE8(Double e8) {
        this.e8 = e8;
    }
    public Double getE9() {
        return e9;
    }
    public void setE9(Double e9) {
        this.e9 = e9;
    }
    public Double getDistanciaE1E2() {
        return distanciaE1E2;
    }
    public void setDistanciaE1E2(Double distanciaE1E2) {
        this.distanciaE1E2 = distanciaE1E2;
    }
    public Double getDistanciaE2E3() {
        return distanciaE2E3;
    }
    public void setDistanciaE2E3(Double distanciaE2E3) {
        this.distanciaE2E3 = distanciaE2E3;
    }
    public Double getDistanciaE3E4() {
        return distanciaE3E4;
    }
    public void setDistanciaE3E4(Double distanciaE3E4) {
        this.distanciaE3E4 = distanciaE3E4;
    }
    public Double getDistanciaE4E5() {
        return distanciaE4E5;
    }
    public void setDistanciaE4E5(Double distanciaE4E5) {
        this.distanciaE4E5 = distanciaE4E5;
    }
    public Double getDistanciaE5E6() {
        return distanciaE5E6;
    }
    public void setDistanciaE5E6(Double distanciaE5E6) {
        this.distanciaE5E6 = distanciaE5E6;
    }
    public Double getDistanciaE6E7() {
        return distanciaE6E7;
    }
    public void setDistanciaE6E7(Double distanciaE6E7) {
        this.distanciaE6E7 = distanciaE6E7;
    }
    public Double getDistanciaE7E8() {
        return distanciaE7E8;
    }
    public void setDistanciaE7E8(Double distanciaE7E8) {
        this.distanciaE7E8 = distanciaE7E8;
    }
    public Double getDistanciaE8E9() {
        return distanciaE8E9;
    }
    public void setDistanciaE8E9(Double distanciaE8E9) {
        this.distanciaE8E9 = distanciaE8E9;
    }
    public String getClassificacaoArt96() {
        return classificacaoArt96;
    }
    public void setClassificacaoArt96(String classificacaoArt96) {
        this.classificacaoArt96 = classificacaoArt96;
    }
    public Double getComprimento() {
        return comprimento;
    }
    public void setComprimento(Double comprimento) {
        this.comprimento = comprimento;
    }
}