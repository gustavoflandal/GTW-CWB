package muralha.digital.veiculo;

import java.util.Date;

import com.consilux.model.TipoMime;

import muralha.digital.util.Paginacao;

public class VeiculoDeCargaValidacao {
    private boolean filtroValido;
    private String mensagem;
    private String placa;
    private Date dataIni;
    private Date dataFim;
    private String equipamento;
    private String faixa;
    private String classificacao;
    private boolean buscarApenasVeiculoComImagem;
    private boolean consultaMapa;
    private boolean modoGrade;
    private boolean retornarImagens;
    private Paginacao paginacao;
    private TipoMime formato;
    private String corVeiculo;
    private String anoFabricacao;
    private String anoModelo;
    private String renavam;
    private String chassi;
    private String tipoVeiculo;
    private String restricao;
    private String marca;
    private String modelo;
    private String tipoPlaca;
    private int filtroPlaca;
    private int idLocalidade;
    private String motivo;
    private String veiculosSelecionados;
    private boolean somenteUltimaPassagem;
    private boolean apenasVeiculosComCarga;
    private boolean exportarConsulta;
    private boolean pesagem;
    private boolean deveAplicarFiltroImagem;

    public boolean isFiltroValido() {
        return filtroValido;
    }
    public void setFiltroValido(boolean filtroValido) {
        this.filtroValido = filtroValido;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    public Date getDataIni() {
        return dataIni;
    }
    public void setDataIni(Date dataIni) {
        this.dataIni = dataIni;
    }
    public Date getDataFim() {
        return dataFim;
    }
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
    public String getEquipamento() {
        return equipamento;
    }
    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }
    public String getFaixa() {
        return faixa;
    }
    public void setFaixa(String faixa) {
        this.faixa = faixa;
    }
    public String getClassificacao() {
        return classificacao;
    }
    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }
    public boolean isBuscarApenasVeiculoComImagem() {
        return buscarApenasVeiculoComImagem;
    }
    public void setBuscarApenasVeiculoComImagem(boolean buscarApenasVeiculoComImagem) {
        this.buscarApenasVeiculoComImagem = buscarApenasVeiculoComImagem;
    }
    public boolean isConsultaMapa() {
        return consultaMapa;
    }
    public void setConsultaMapa(boolean consultaMapa) {
        this.consultaMapa = consultaMapa;
    }
    public boolean isModoGrade() {
        return modoGrade;
    }
    public void setModoGrade(boolean modoGrade) {
        this.modoGrade = modoGrade;
    }
    public boolean isRetornarImagens() {
        return retornarImagens;
    }
    public void setRetornarImagens(boolean retornarImagens) {
        this.retornarImagens = retornarImagens;
    }
    public Paginacao getPaginacao() {
        return paginacao;
    }
    public void setPaginacao(Paginacao paginacao) {
        this.paginacao = paginacao;
    }
    public TipoMime getFormato() {
        return formato;
    }
    public void setFormato(TipoMime formato) {
        this.formato = formato;
    }
    public String getCorVeiculo() {
        return corVeiculo;
    }
    public void setCorVeiculo(String corVeiculo) {
        this.corVeiculo = corVeiculo;
    }
    public String getAnoFabricacao() {
        return anoFabricacao;
    }
    public void setAnoFabricacao(String anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }
    public String getAnoModelo() {
        return anoModelo;
    }
    public void setAnoModelo(String anoModelo) {
        this.anoModelo = anoModelo;
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
    public String getTipoVeiculo() {
        return tipoVeiculo;
    }
    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }
    public String getRestricao() {
        return restricao;
    }
    public void setRestricao(String restricao) {
        this.restricao = restricao;
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
    public String getTipoPlaca() {
        return tipoPlaca;
    }
    public void setTipoPlaca(String tipoPlaca) {
        this.tipoPlaca = tipoPlaca;
    }
    public int getFiltroPlaca() {
        return filtroPlaca;
    }
    public void setFiltroPlaca(int filtroPlaca) {
        this.filtroPlaca = filtroPlaca;
    }
    public int getIdLocalidade() {
        return idLocalidade;
    }
    public void setIdLocalidade(int idLocalidade) {
        this.idLocalidade = idLocalidade;
    }
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public void setVeiculosSelecionados(String veiculosSelecionados) {
        this.veiculosSelecionados = veiculosSelecionados;
    }
    public String getVeiculosSelecionados() {
        return veiculosSelecionados;
    }
    public boolean isSomenteUltimaPassagem() {
        return somenteUltimaPassagem;
    }
    public void setSomenteUltimaPassagem(boolean somenteUltimaPassagem) {
        this.somenteUltimaPassagem = somenteUltimaPassagem;
    }
    public boolean isApenasVeiculosComCarga() {
        return apenasVeiculosComCarga;
    }
    public void setApenasVeiculosComCarga(boolean apenasVeiculosComCarga) {
        this.apenasVeiculosComCarga = apenasVeiculosComCarga;
    }
    public boolean isExportarConsulta() {
        return exportarConsulta;
    }
    public void setExportarConsulta(boolean exportarConsulta) {
        this.exportarConsulta = exportarConsulta;
    }
    public boolean isPesagem() {
        return pesagem;
    }
    public void setPesagem(boolean pesagem) {
        this.pesagem = pesagem;
    }
	public boolean isDeveAplicarFiltroImagem() {
		return deveAplicarFiltroImagem;
	}
	public void setDeveAplicarFiltroImagem(boolean deveAplicarFiltroImagem) {
		this.deveAplicarFiltroImagem = deveAplicarFiltroImagem;
	}
}