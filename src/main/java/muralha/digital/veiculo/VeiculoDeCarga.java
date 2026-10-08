package muralha.digital.veiculo;

import java.util.Date;

public class VeiculoDeCarga {
    private long id;
    private String placa;
    private Date data;
    private int idLocal;
    private int serieEquipamento;
    private String codigoEquipamento;
    private String nome;
    private int idPista;
    private int faixa;
    private Double latitude;
    private Double longitude;
    private Double comprimento;
    private Double velocidade;
    private String idClasse;
    private String classificacao;
    private Boolean enviadoCliente;
    private Boolean comImagem;
    private Boolean possuiCoordenadas;
    private Boolean possuiAlerta;
    private String marca;
    private String modelo;
    private String cor;
    private String anoModelo;
    private String tipo;
    private String localidade;
    private String uf;
    private String anoFabricacao;
    private String renavam;
    private String chassi;
    private String restricao;
    private Double pbt;
    private Double pbtc;
    private int numeroEixos;
    private String rodagemDupla;
    private String categoria;
    private String classificacaoArt96;
    private Date dataImportado;
    private Boolean placaMercosul;
    private String corPlaca;
    private String veicAnterior;
    private String veicProximo;
    private int totalRegistros;
    private long idVeiculo;

    // Getters e Setters
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
    public Double getComprimento() {
        return comprimento;
    }
    public void setComprimento(Double comprimento) {
        this.comprimento = comprimento;
    }
    public Double getVelocidade() {
        return velocidade;
    }
    public void setVelocidade(Double velocidade) {
        this.velocidade = velocidade;
    }
    public String getIdClasse() {
        return idClasse;
    }
    public void setIdClasse(String idClasse) {
        this.idClasse = idClasse;
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
    public Boolean getPossuiCoordenadas() {
        return possuiCoordenadas;
    }
    public void setPossuiCoordenadas(Boolean possuiCoordenadas) {
        this.possuiCoordenadas = possuiCoordenadas;
    }
    public Boolean getPossuiAlerta() {
        return possuiAlerta;
    }
    public void setPossuiAlerta(Boolean possuiAlerta) {
        this.possuiAlerta = possuiAlerta;
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
    public String getCor() {
        return cor;
    }
    public void setCor(String cor) {
        this.cor = cor;
    }
    public String getAnoModelo() {
        return anoModelo;
    }
    public void setAnoModelo(String anoModelo) {
        this.anoModelo = anoModelo;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getLocalidade() {
        return localidade;
    }
    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }
    public String getUf() {
        return uf;
    }
    public void setUf(String uf) {
        this.uf = uf;
    }
    public String getAnoFabricacao() {
        return anoFabricacao;
    }
    public void setAnoFabricacao(String anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }
    public String getRenavam() {
        return renavam;
    }
    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }
    public String getChassi() {
        return chassi;
    }
    public void setChassi(String chassi) {
        this.chassi = chassi;
    }
    public String getRestricao() {
        return restricao;
    }
    public void setRestricao(String restricao) {
        this.restricao = restricao;
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
    public String getRodagemDupla() {
        return rodagemDupla;
    }
    public void setRodagemDupla(String rodagemDupla) {
        this.rodagemDupla = rodagemDupla;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getClassificacaoArt96() {
        return classificacaoArt96;
    }
    public void setClassificacaoArt96(String classificacaoArt96) {
        this.classificacaoArt96 = classificacaoArt96;
    }
    public Date getDataImportado() {
        return dataImportado;
    }
    public void setDataImportado(Date dataImportado) {
        this.dataImportado = dataImportado;
    }
    public Boolean getPlacaMercosul() {
        return placaMercosul;
    }
    public void setPlacaMercosul(Boolean placaMercosul) {
        this.placaMercosul = placaMercosul;
    }
    public String getCorPlaca() {
        return corPlaca;
    }
    public void setCorPlaca(String corPlaca) {
        this.corPlaca = corPlaca;
    }
    public String getVeicAnterior() {
        return veicAnterior;
    }
    public void setVeicAnterior(String veicAnterior) {
        this.veicAnterior = veicAnterior;
    }
    public String getVeicProximo() {
        return veicProximo;
    }
    public void setVeicProximo(String veicProximo) {
        this.veicProximo = veicProximo;
    }
    public int getTotalRegistros() {
        return totalRegistros;
    }
    public void setTotalRegistros(int totalRegistros) {
        this.totalRegistros = totalRegistros;
    }
    public long getIdVeiculo() {
        return idVeiculo;
    }
    public void setIdVeiculo(long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }
}